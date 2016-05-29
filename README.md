# Motive

A woefully under-documented library for generating algorithmic music
using Clojure. Designed to work with Overtone (but only the
`motive.live` namespace actually cares).

## Core Ideas

The central idea of motive is the concept of a _generator function_,
which is basically the dual concept to a reducer function.

In the case of a reducer functionq, `rf`, we have an accumulator to
which we repeatedly apply the function, each time providing another
value `x`:

```
(rf accum x) ===> new-accum,
```

and the reducer function 'incorporates' `x` into the accumulator to
give a new accumulator value, which we use in the next iteration.

Conversely, for a generator function `gf` the role of the accumulator
is played by a value we call the _state_. The function signature is

```
(gf state) ===> [x new-state],
```

so that each time we apply `gf` to the state value it generates some
`x` together with a new state value that we will use in the next
iteration. So while a reducer function consumes a sequence of `x`s, a
generator function produces a sequence of `x`s.

For a given `gf` it is important that the state value is of the
correct form. However, for the generator functions in the motive
library you don't usually have to worry about this. Because most of
these generator functions are non-deterministic and have simple,
standard, initial conditions, by convention each function has a
zero-arity case that creates its own valid initial state value:

```
(gf) ===> [x state].
```

### Melody generator functions

### Rhythm generator functions

### Beat generator functions

## Using `motive.live` with Overtone

## License

Copyright 2016 Thomas Athorne

This program is intended to be free software, but obviously I'm
currently doing a really bad job putting any legal weight behind this
intention.
