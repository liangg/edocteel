# Development Log

- Orders can be asynchronously read from a json file
- The kitchen should receive orders at rate following a Poisson Distribution with an average of 3.25 deliveries per second (lambda)
- If all the shelves incl. the overflow shelf are full then an order should be removed and considered waste. 
- If shelves free up, you may move an order back from the overflow shelf.
- Orders that have reached a value of zero are considered waste and should be removed from the shelves
- Decay rates double for orders on the overflow shelf.
- Driver should be dispatched to pick up each order as it is placed.
- Randomly assign a drive time of 2 to 10 seconds for the driver to arrive to pick up the order
- A normalized value for the order is the current value divided by the shelf life of the order.
- Display the contents of each shelf including the normalized value of each order. The display should be updated every time an order is added or removed.
- Abstract the order decay formula so that is can be dynamic per order.

__Notes__

- Shelf life does not seem to matter, because order value changes dynamically with decay rate. 
- The strategy is to optimize for max value order at driver delivery.

## Tasks

- [x] Raw Order immutable
- [x] ShelfOrder wraps Order with order time in millisec and last determined value
- [ ] Shelf: thread safe, order set
- [ ] Shelf: priority queue on order value, give max value (pick) and min value (discard)
- [ ] Overflow shelf decay 2x faster
- [ ] Overflow shelf: selection
- [ ] Dispatch order: max heap of size 3 max valued orders from 3 shelves
- [ ] Asynchronously read orders in poisson distribution
- [ ] Scheduled arrival of drivers every 2~10 seconds
- [x] Metric counters for total orders, delivered orders, normalized values, number of wasted orders etc.

## Tests

- [x] Read, parse orders json file, and create list of Order
- [ ] Test poisson distribution with avg 3.25 orders per second
- [x] Test driver arrival 2~10 seconds in scheduled tasks
- [ ] Order overflow shelf selection -- different shelf composition, what to discard
