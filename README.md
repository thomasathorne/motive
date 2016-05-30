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

### Generator function middleware

Most of the motive library consists of functions that act as
middleware for generator functions, in much tha same way that
transducers have been described as middleware for reducer functions.

For example, `motive.generator/choosing` takes any number of generator
functions and returns a single generator function that each time it
runs makes a random choice of which generator function to use. A
similar example is `looping`, which instead cycles predictably through
the generator functions it is given.

Besides `map-g` and `filter-g`, which behave exactly as their names
suggest, there is a second kind of filter-like behaviour possible on
generator functions. When `filter-g` removes a value `x` from the
stream, it continues running `gf` from the new state that it reached
after generating `x`---so the unwanted `x`s are simply passed over.
However, since most of the time `gf` is non-deterministic and has
state that captures some pretty important information, we often want
to use `blocking-filter` instead. After discarding an unwanted `x`,
`blocking-filter` will continue from the *original* state and simply
keep trying until it finds an `x` that is more appropriate. Needless
to say, one should be careful about calling this on deterministic
generator functions!

### Melody generator functions

A good example of this kind of `blocking-filter` behaviour is found in
some of the _melody_ generator functions in the `motive.melody`
namespace. These functions simply generate integers between 0 and 128,
that represent midi note values. Hence they have no concept of rhythm,
instrumentation, dynamics, time signatures or phrasing. It's the most
minimal possible notion of melody: a sequence of pitches.

...

### Rhythm generator functions

### Beat generator functions

## Using `motive.live` with Overtone

## License

Copyright 2016 Thomas Athorne

This program is intended to be free software, but obviously I'm
currently doing a really bad job putting any legal weight behind this
intention.
