
import java.util.*;

/**
 * Throughout this interview, we'll pretend we're building a new analytical database. Don't worry about actually
 * building a database though – these will all be toy problems.
 *
 * Here's how the database works: all records are represented as maps, with string keys and integer values. The records
 * are contained in an array, in no particular order.
 *
 * Step 1: minByKey
 *
 * To begin with, the database will support just one function: min_by_key. This function scans the array of records and
 * returns the record that has the minimum value for a specified key. Records that do not contain the specified key are
 * considered to have value 0 for the key. Note that keys may map to negative values!
 *
 * Here's an example use case: each of your records contains data about a school student. You can use min_by_key to
 * answer questions such as "who is the youngest student?" and "who is the student with the lowest grade-point average?"
 *
 * Implementation notes:
 *
 * - You should handle an empty array of records in an idiomatic way in your language of choice.
 * - If several records share the same minimum value for the chosen key, you may return any of them.
 *
 * Step 2: first_by_key
 *
 * Our next step in database development is to add a new function. We'll call this function first_by_key. It has much
 * in common with min_by_key. first_by_key takes three arguments:
 *
 * - a string key
 * - a string sort direction (which must be either "asc" or "desc")
 * - an array of records, just as in min_by_key.
 *
 * If the sort direction is "asc", then we should return the minimum record, otherwise we should return the maximum
 * record. As before, records without a value for the key should be treated as having value 0.
 *
 * Once you have a working solution, you should re-implement min_by_key in terms of first_by_key.
 *
 * Step 3: first_by_key comparator
 *
 * As we build increasingly rich orderings for our records, we'll find it useful to extract the comparison of records
 * into a comparator. This is a function or object (depending on your language) which determines if a record is
 * "less than", equal, or "greater than" another.
 *
 * In object-oriented languages, you should write a class whose constructor accepts two parameters: a string key and a
 * string direction. The class should implement a method compare that takes as its parameters two records. This method
 * should return -1 if the first record comes before the second record (according to key and direction), zero if neither
 * record comes before the other, or 1 if the first record comes after the second.
 *
 * In functional languages, you should write a function which accepts two parameters: a string key and a string
 * direction. The function should return a method that takes as its parameters two records. This function should return
 * -1 if the first record comes before the second record (according to key and direction), zero if neither record comes
 * before the other, or 1 if the first record comes after the second.
 *
 * You should then use your comparator in your implementation of firstByKey.
 *
 * Step 4:
 *
 * Time to take this"firstby" functionality further, to sort by more than one key at a time. Consider an array of
 * records like this one: [{“a”: 1, “b”: 1}, {“a”: 1, “b”: 2}, {“a”: 2, “b”: 1}, {“a”: 2, “b”: 2}]. Using first_by_key
 * with this array of records with key “a” might not give us as much control as we’d like over the result, since more
 * than one record has the same value for"a" (similarly for “b”).
 *
 * More generally, we might say order by the first key, in the first direction. Break ties according to the second key,
 * in the second direction. Break remaining ties by the third key,in the third direction, and so on. Note that the sort
 * direction for different keys need not be the same. We might represent this ordering witha list of pairs like --
 * [ ("a", "asc"), ("b", "asc"), ("c", "desc") ].
 *
 * We’ll call this type of representation a sort_order. You’ll need to write a function first_by_sort_order. It takes
 * two arguments: 1, a sort_order; 2, an array of records. It returns the first record according to the given
 * sort_order. As before, we’ll ask that you reimplement your previous functionality (first_by_key) in terms of
 * first_by_sort_order.
 *
 * Hint: can you construct a “sortorder” comparator using your comparator from the previous step? How might constructing
 * a sort order comparator be helpful?
 */
public class AnaDB {
    // Step 1
    public static Map<String, Integer> minByKey1(String key, List<Map<String, Integer>> records) {
        int minVal = Integer.MAX_VALUE;
        Map<String, Integer> result = null;
        for (Map<String, Integer> rec : records) {
            if (rec.containsKey(key)) {
                if (rec.get(key) < minVal) {
                    result = rec;
                    minVal = rec.get(key);
                }
            } else if (minVal > 0) {
                minVal = 0;
                result = rec;
            }
        }
        return result;
    }

    // Step 2
    public static Map<String, Integer> minByKey(String key, List<Map<String, Integer>> records) {
        return firstByKey(key, "asc", records);
    }

    public static Map<String, Integer> firstByKey2(String key, String direction, List<Map<String, Integer>> records) {
        // a string sort direction, "asc" or "desc"
        boolean minimal = direction.equals("asc") ? true : false;
        int currVal = minimal ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        Map<String, Integer> result = null;
        for (Map<String, Integer> rec : records) {
            if (rec.containsKey(key)) {
                if ((minimal && rec.get(key) < currVal) || (!minimal && rec.get(key) > currVal)) {
                    result = rec;
                    currVal = rec.get(key);
                }
            } else {
                if ((minimal && currVal > 0) || (!minimal && currVal < 0)) {
                    currVal = 0;
                    result = rec;
                }
            }
        }
        return result;
    }

    // Step 3
    public static Map<String, Integer> firstByKey(String key, String direction, List<Map<String, Integer>> records) {
        RecordComparactor rcomparator = new RecordComparactor(key, direction);
        List<Map<String, Integer>> copy = new ArrayList<Map<String, Integer>>(records);
        Collections.sort(copy, rcomparator);
        System.out.println(records);
        System.out.println(copy);
        return copy.get(0);
    }

    private static class RecordComparactor implements Comparator<Map<String, Integer>> {
        private String key;
        private String direction;

        RecordComparactor(String key, String direction) {
            this.key = key;
            this.direction = direction;
        }

        @Override
        public int compare(Map<String, Integer> r1, Map<String, Integer> r2) {
            Integer v1 = r1.containsKey(key) ? r1.get(key) : 0;
            Integer v2 = r2.containsKey(key) ? r2.get(key) : 0;
            if (v1 == v2) return 0;
            return direction.equals("asc") ? (v1 < v2 ? -1 : 1) : (v1 > v2 ? -1 : 1);
        }
    }

    // Step 4
    public static Map<String, Integer> firstBySortOrder(LinkedHashMap<String, String> sortOrder,
                                                        List<Map<String, Integer>> records) {
        SortOrderComparator soComparator = new SortOrderComparator(sortOrder);
        List<Map<String, Integer>> copy = new ArrayList<Map<String, Integer>>(records);
        Collections.sort(copy, soComparator);
        return null;
    }

    private static class SortOrderComparator implements Comparator<Map<String, Integer>> {
        LinkedHashMap<String, String> sortOrder;
        Map<String, RecordComparactor> comparactorMap = new HashMap<>();

        SortOrderComparator(LinkedHashMap<String, String> sortOrder) {
            this.sortOrder = sortOrder;
            for (Map.Entry<String, String> e : sortOrder.entrySet()) {
                RecordComparactor recordComparactor = new RecordComparactor(e.getKey(), e.getValue());
                comparactorMap.put(e.getKey(), recordComparactor);
            }
        }

        @Override
        public int compare(Map<String, Integer> r1, Map<String, Integer> r2) {
            for (Map.Entry<String, String> e : sortOrder.entrySet()) {
                RecordComparactor recordComparactor = comparactorMap.get(e.getKey());
                int result = recordComparactor.compare(r1, r2);
                if (result != 0)
                    return result;
            }
            return 0;
        }
    }

    public static <T> void assertEqual(T expected, T actual) {
        if (expected == null && actual == null || actual != null && actual.equals(expected)) {
            System.out.println("PASSED");
        } else {
            throw new AssertionError("Expected:\n  " + expected + "\nActual:\n  " + actual + "\n");
        }
    }

    public static void testMinByKey() {
        List<Map<String, Integer>> example1 = Arrays.asList(
            Maps.of("a", 1, "b", 2),
            Maps.of("a", 2)
        );
        List<Map<String, Integer>> example2 = Arrays.asList(example1.get(1), example1.get(0));
        List<Map<String, Integer>> example3 = Arrays.asList(Maps.of());
        List<Map<String, Integer>> example4 = Arrays.asList(
            Maps.of("a", -1),
            Maps.of("b", -1)
        );

        System.out.println("minByKey");
        assertEqual(example1.get(0), minByKey("a", example1));
        assertEqual(example2.get(1), minByKey("a", example2));
        assertEqual(example1.get(1), minByKey("b", example1));
        assertEqual(example3.get(0), minByKey("a", example3));
        assertEqual(example4.get(1), minByKey("b", example4));
    }

    public static void testFirstByKey() {
        List<Map<String, Integer>> example1 = Arrays.asList(Maps.of("a", 1));
        List<Map<String, Integer>> example2 = Arrays.asList(
            Maps.of("b", 1),
            Maps.of("b", -2),
            Maps.of("a", 10)
        );
        List<Map<String, Integer>> example3 = Arrays.asList(
            Maps.of(),
            Maps.of("a", 10, "b", -10),
            Maps.of(),
            Maps.of("a", 3, "c", 3)
        );

        System.out.println("firstByKey");
        assertEqual(example1.get(0), firstByKey("a", "asc", example1));
        assertEqual(example2.get(0), firstByKey("a", "asc", example2));  // example2.get(1) ok too
        assertEqual(example2.get(2), firstByKey("a", "desc", example2));
        assertEqual(example2.get(1), firstByKey("b", "asc", example2));
        assertEqual(example2.get(0), firstByKey("b", "desc", example2));
        assertEqual(example3.get(1), firstByKey("a", "desc", example3));
    }

    public static void main(String[] args) {
        testMinByKey();
        testFirstByKey();
    }
}

class Maps {
    public static <K, V> Map<K, V> of() {
        return new HashMap<K, V>();
    }

    public static <K, V> Map<K, V> of(K k1, V v1) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    public static <K, V> LinkedHashMap<K, V> ordered(K k1, V v1) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        return map;
    }

    public static <K, V> LinkedHashMap<K, V> ordered(K k1, V v1, K k2, V v2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static <K, V> LinkedHashMap<K, V> ordered(K k1, V v1, K k2, V v2, K k3, V v3) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }
}
