# Kafka Connect JMESPath Plugin

A [Kafka Connect][connect] plugin makes the [JMESPath][jmespath] query 
language available to connectors. It currently comes with the following 
components:

* [`MatchesJMESPath` Predicates](#matchesjmespath-predicates) 

## Installation

You can install or download the latest version of the plugin from 
[Confluent Hub][confluent-hub].

## `MatchesJMESPath` Predicates

The `de.denisw.kafka.connect.jmespath.MatchesJMESPath$Key`
and `de.denisw.kafka.connect.jmespath.MatchesJMESPath$Value` 
[transformation predicates][connect-predicate] applies a JMESPath query to
the key or value of each record. It matches if the query yields a "true 
value" according to the [JMESPath specification][jmespath-true-value],
that is, any value except the following:

* null
* false
* empty string
* empty array (`[]`)
* empty object (`{}`)

The `MatchesJMESPath` predicates are designed to be used with Kafka 
Connect's built-in [Filter transformation][connect-filter] to drop all 
records matching a JMESPath query (see the examples below). However, they 
can more generally be used to apply any [Single Message Transformation][connect-smt] 
conditionally based on the data in the record key or value. 

> Note that unlike the `filter` operation known from many programming
> languages and libraries (including Kafka Streams), the Filter 
> transformation **drops** records matching the predicate rather than 
> keeping them. The JMESPath query should thus match records that should
> NOT be kept. Alternatively, you can enable the `negate` option of
> the Filter transformation to reverse the predicate.

### Configuration Examples

Skip records whose nested `publishDate.year` field is below 2000:

```json
"transforms": "Filter",
"transforms.Filter.type": "org.apache.kafka.connect.transforms.Filter",
"transforms.Filter.predicate": "Before2000",

"predicates": "Before2000",
"predicates.Before2000.type": "de.denisw.kafka.connect.jmespath.MatchesJMESPath$Value",
"predicates.Before2000.query": "publishDate.year < `2000`"
```

Process only records whose `author` equals "Stephen Hawking",
using Filter's `negate` option to reverse the predicate:

```json
"transforms": "Filter",
"transforms.Filter.type": "org.apache.kafka.connect.transforms.Filter",
"transforms.Filter.predicate": "Author",
"transforms.Filter.negate": "true",
        
"predicates": "Author",
"predicates.Author.type": "de.denisw.kafka.connect.jmespath.MatchesJMESPath$Value",
"predicates.Author.query": "author == 'Stephen Hawking'"
```

Drop records with null keys, using JMESPath's `@` syntax to match
the whole key:

```json
"transforms": "Filter",
"transforms.Filter.type": "org.apache.kafka.connect.transforms.Filter",
"transforms.Filter.predicate": "NullKey",

"predicates": "NullKey",
"predicates.NullKey.type": "de.denisw.kafka.connect.jmespath.MatchesJMESPath$Key",
"predicates.NullKey.query": "@ == `null`"
```

### Configuration Reference

#### `query`

The JMESPath query to apply to the key or value data. See the
JMESPath [tutorial][jmespath-tutorial], [examples][jmespath-examples]
and [specification][jmespath-spec] to learn about the supported
syntax.

## Demo

See the [`demo` subfolder](./demo) for a Docker-based setup to test the
Kafka Connect JMESPath plugin locally.

## License

This codebase is licensed under the Apache License 2.0. See the
`LICENSE` file for more details.

The JMESPath logo (`images/jmespath-logo.png`) is taken from the
[JMESPath website codebase][jmespath-site-github] and is licensed 
under the [Creative Commons license (CC BY 4.0)][cc-by-4.0].

[cc-by-4.0]: https://github.com/jmespath/jmespath.site/blob/master/LICENSE-docs.txt
[confluent-hub]: https://www.confluent.io/hub/denisw/kafka-connect-jmespath
[jmespath]: https://jmespath.org/
[jmespath-examples]: https://jmespath.org/examples.html
[jmespath-site-github]: https://github.com/jmespath/jmespath.site
[jmespath-true-value]: https://jmespath.org/specification.html#or-expressions
[jmespath-tutorial]: https://jmespath.org/tutorial.html
[connect]: https://docs.confluent.io/platform/current/connect/
[connect-filter]: https://docs.confluent.io/platform/current/connect/transforms/filter-ak.html
[connect-predicate]: https://docs.confluent.io/platform/current/connect/transforms/filter-ak.html#predicates
[connect-smt]: https://docs.confluent.io/platform/current/connect/transforms/overview.html