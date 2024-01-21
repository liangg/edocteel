/**
 * Test program to organize data as is done in the data block/page in a database.
 *
 * You're given a simple block of memory: char page[4096];
 *
 * Create a program which reads a simple set of commands and strings such as:
 *
 *  INS ABRQA097230423793274
 *  INS RXZAR035250483103408329
 *  INS BBAQQ925723982945777
 *  DEL ABRQA
 *  INS EIURO92347209502987
 *  UPD BBAQQ72398509235790988925
 *  SEL RXZAR
 *
 * The strings consist of two parts:
 *  Key:  The first 5 chars
 *  Data: The remainder of the string
 *
 * The string will consist of simple ascii text.  The strings will be from
 * 30 to 50 char's in length. The string will also be referred to as a Row
 *
 * Commands:
 *  INS: If the Key isn't already in the block insert it.
 *       If the Key is already in the block print the error:
 *            Key <KEY> exists
 *       If the string doesn't fit print:
 *            Block is too full to insert <STRING>
 *
 *  DEL: Delete the string for the given Key.
 *       If the Key doesn't exist print the message:
 *            Key <KEY> doesn't exist
 *
 *  UPD: Update the Key with the new string.
 *       If the Key doesn't exist print the message:
 *            Key <KEY> doesn't exist
 *       If the new string doesn't fit print:
 *            Block is too full to update Row to <STRING>
 *
 *  SEL: Return and print the string with the given Key
 *       If the Key doesn't exist print the message:
 *            Key <KEY> doesn't exist
 *
 * Additional requirements:
 *   The insert function shall return a ROWID which uniquely identifies
 *   the Row.  This is an integer with a value from 0 to 255.  ROWID's
 *   of deleted Rows should be reused for subsequent inserts.
 *
 *   Upon successful insert return and print the ROWID.
 *
 *   If time permits, add code to reorganize the block on inserts and
 *   updates to allow Rows to fit that otherwise would not fit due to
 *   fragmentation.  The reorganization shall not change the ROWID of
 *   any Rows.  If you had Rows with ROWID's 0, 1, 2, 3, and 4 and then
 *   deleted the Row with ROWID 2 and then did a reorganization due to
 *   a new insert, it would not change the ROWID's of 0, 1, 3 and 4.
 *   The new insert would return the ROWID of 2.
 *
 *   It is acceptable to do a linear search of the Rows to do lookups.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define BLOCKSIZE 4096
#define MAXROWS 256
#define KEYLEN 5 // byte length of key
#define VALLEN 1 // byte length of value

#define COMMAND_LEN 3
#define DATASTR_LEN 50

/**
 * The block format is as follows -
 *
 * BlockMeta has control data incl. number of used slots, byte offset
 * of next inserted row etc.
 *
 * Row slot table has 256 entries, each of which has offset to where
 * data is stored in block.
 *
 * At each offset, key, length of value, and byte array of value are
 * write sequentially.
 */

/**
 * A ROW_SLOT_TABLE has max 256 entries and each entry format
 * [OFFSET] has byte offset in the block. OFFSET is 2 bytes.
 */
#define ROWSLOT_SIZE 2

/* Delete row slot marker */
#define ROWSLOT_DEAD -1

/**
 * Block control meta data
 */
struct BlockMeta
{
  int numRows; // total number of occupied row slots incl. deletes
  int numDels;  // number of deleted rows
  int offset;  // where to write new row
};
typedef struct BlockMeta BlockMeta;

#define SLOT_TABLE_OFFSET sizeof(BlockMeta)

/* Init the block and return meta data structure */
BlockMeta* init_block(unsigned char *block)
{
  BlockMeta *meta = (BlockMeta *)&block[0];

  meta->numRows = 0;
  meta->numDels = 0;
  meta->offset = sizeof(BlockMeta) + MAXROWS*ROWSLOT_SIZE;
  return meta;
}

/**
 * It seraches for a given key in the row slot table and returns
 * the offset to key value in the block. It also returns slot#
 * in OUT param "slotNum".
 *
 * The search function should be optimized with either search tree
 * e.g. B+tree or some hashing index (FIXME).
 */
int search_key(unsigned char *block, BlockMeta *meta, char *key, int *slotNum)
{
  int i = 0;
  unsigned short *slots = (unsigned short *)&block[SLOT_TABLE_OFFSET];

  for( i = 0; i < meta->numRows; ++i ) {
    int off = slots[i];
    /* row slot not deleted */
    if( off != (unsigned short)ROWSLOT_DEAD )
    {
      /* return offset to value in the block */
      if( !strncmp((char*)&block[off], key, KEYLEN) ) {
        *slotNum = i;
        return off + KEYLEN;
      }
    }
  }
  return 0;
}

int do_insert(unsigned char *block, BlockMeta *meta, char *key, char *val)
{
  unsigned char valSize = (unsigned char)strlen(val);
  int rowid;
  int offset = meta->offset;
  int slowNum;
  unsigned short *slots = (unsigned short *)&block[SLOT_TABLE_OFFSET];

  if( search_key(block, meta, key, &slowNum) ) {
    printf("Key %s exists\n", key);
    return;
  }

  /* check if block has enough space for new key */
  if( (offset + KEYLEN + VALLEN + valSize) > BLOCKSIZE ) {
    /* FIXME: run compaction to reclaim space */
    printf("Block is too full to insert %s %s\n", key, val);
    return;
  }

  /* write to the next available offset */
  slots[meta->numRows] = meta->offset;
  memcpy((void*)&block[offset], (void*)key, KEYLEN);
  rowid = meta->numRows;
  /* write value byte length before value data */
  offset += KEYLEN;
  memcpy((void*)&block[offset], (void*)&valSize, VALLEN);
  /* write value data */
  offset += VALLEN;
  memcpy((void*)&block[offset], (void*)val, valSize);
  /* update block meta data */
  meta->numRows++;
  meta->offset += KEYLEN + VALLEN + valSize;

  return rowid;
}

void do_update(unsigned char *block, BlockMeta *meta, char *key, char *val)
{
  unsigned char valSize = (unsigned char)strlen(val);
  int off;
  int slotNum;
  int oldSize;
  int writeOff;
  int inplace = 0;
  unsigned short *slots = (unsigned short *)&block[SLOT_TABLE_OFFSET];

  off = search_key(block, meta, key, &slotNum);
  if( !off ) {
    printf("Key %s doesn't exist\n", key);
    return;
  }

  /* check whether we can update in place, otherwise we have to write to
     the end */
  oldSize = block[off+KEYLEN];
  if( oldSize >= valSize ) {
    inplace = 1;
    writeOff = off;
  }
  /* check if block has enough space for the updated key */
  if( (meta->offset + KEYLEN + VALLEN + valSize) > BLOCKSIZE ) {
    /* FIXME: run compaction to reclaim space */
    printf("Block is too full to update %s %s\n", key, val);
    return;
  }
  else {
    writeOff = meta->offset;
    slots[slotNum] = meta->offset;
  }

  /* write to the next available offset */
  memcpy((void*)&block[writeOff], (void*)key, KEYLEN);
  /* write value byte length before value data */
  writeOff += KEYLEN;
  memcpy((void*)&block[writeOff], (void*)&valSize, VALLEN);
  /* write value data */
  writeOff += VALLEN;
  memcpy((void*)&block[writeOff], (void*)val, valSize);
  /* update block meta data */
  meta->numRows++;
  /* bump block write offset only if not in-place update */
  if( !inplace ) {
    meta->offset += KEYLEN + VALLEN + valSize;
  }
}

/**
 * It marks deleted row as dead in row slot and leaves fragments in
 * the block. A compaction would reclaim all dead slots (FIXME).
 */
void do_delete(unsigned char *block, BlockMeta *meta, char *key)
{
  int slotNum;
  unsigned short *slots = (unsigned short *)&block[SLOT_TABLE_OFFSET];

  if( search_key(block, meta, key, &slotNum) ) {
    /* mark the deleted row as dead */
    slots[slotNum] = ROWSLOT_DEAD;
    meta->numDels++;
  } else {
    printf("Key %s doesn't exist\n", key);
  }
}

void do_select(unsigned char *block, BlockMeta *meta, char *key)
{
  int off;
  int slotNum;

  if( off = search_key(block, meta, key, &slotNum) )
  {
    unsigned char *v = &block[off];
    unsigned char vlen = v[0];
    unsigned char vbuf[DATASTR_LEN];
    strncpy(vbuf, &v[1], vlen);
    vbuf[vlen+1] = '\0';
    printf("Key %s, %s\n", key, vbuf);
  } else {
    printf("Key %s doesn't exist\n", key);
  }
}

void dml_driver(FILE *input)
{
  char command[COMMAND_LEN+1];
  char data[DATASTR_LEN+1];
  unsigned char *block =
    (unsigned char*)malloc(BLOCKSIZE*sizeof(unsigned char));
  BlockMeta *meta = init_block(block);

  while( fscanf(input, "%s %s", command, data) == 2 ) {
    char key[KEYLEN+1];
    char *val;

    printf("%s %s\n", command, data);
    strncpy(key, data, KEYLEN);
    val = &data[KEYLEN];
    if( !strcmp(command, "INS") ) {
      int rowid = do_insert(block, meta, key, val);
      printf("Rowid %d\n", rowid);
    }
    else if ( !strcmp(command, "DEL") ) {
      do_delete(block, meta, key);
    }
    else if ( !strcmp(command, "UPD") ) {
      do_update(block, meta, key, val);
    }
    else if ( !strcmp(command, "SEL") ) {
      do_select(block, meta, key);
    }
  }

  /* finally, free the block */
  free(block);
}

int main(int argc, unsigned char **argv)
{
  FILE *input = fopen("input.txt", "r");
  dml_driver(input);
}