# JSONtoXML

Simple streaming tool for conversion JSON to XML

It is purposed to save attribute order of original document and provide "correct" null values processing during conversion

To build this project run "mvn package" from root directory. It's only JAR with small utility class. Nothing more.

----

## Problem
Default PostgreSQL build does not contain any API for XML support it has only JSON API. JSON.org suggest small set of tools for conversion JSON data to XML documents. It seems you can generate JSON object in PostgeSQL and convert to XML but you can't because JSON is a data format but XML is a document format, so you loose information about attribute sequence during conversion from JSON to XML. So standard way to generate XML document from default PostgreSQL build does not exist. Also, for example, org.json.XML class converts null values to a string "null" during XML document generation. This is incorrect because a string "null" is not null value.

## Solution
JSONtoXML utility class from this project preserve attribute order in JSON source during XML document generation and correctly transforms null value to the tag with empty content. You can use json_object (not jsonb!) API functions to generate JSON text and translate it to XML as document not as data.
Also JSONtoXML does not build JSON object attribute map during conversion and you does not have unneeded long live objects with document copy.

## License
This code licensed as limited FPL (FPL with some small limitations aka sanctions). Read please LICENSE.md about details. If you interested in commercial support or commercial extension of this code write please to inl@yandex.com a request of commercial suggestion.
