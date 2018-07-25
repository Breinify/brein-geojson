<!--
  Title: Breinify GeoJSON-Utilities (e.g., Parsing, Shape-in-Shape, Summary Statistics)
  Description: Java GeoJSON parser and geometry tools (point in polygon, distance, surface area, etc)
  Author: breinify
  -->

<p align="center">
  <img src="https://www.breinify.com/img/Breinify_logo.png" alt="Breinify: Leading Temporal AI Engine" width="250">
</p>

# GeoJSON Utilities

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<sup>Features: **GeoJSON**, **Geometry**</sup>

## When Should I Use This Library?
This library was developed to mainly support queries such as `Which shapes are near shape X?`, `Is shape Y within 
polygon P` or `How large is polygon Z?` with the underlying shapes are available through 
[GeoJSON](https://tools.ietf.org/html/rfc7946) formatted strings.

This library does *not* currently support features such as tools to modify GeoJSONs (e.g. transformations or dialation). 
It also does not natively support loading shapes from non-geojson objects (e.g. Shapefiles or KML), although one could 
map these other formats to our constructors if they want to use the other functionality of our utilities.

## Getting Started

A complete copy of the code below is available under [`TestDemo`](https://github.com/Breinify/brein-geojson/blob/master/test/com/brein/geojson/docs/TestDemo.java).

### Loading Your First GeoJSON

We provide a helper class, `GeoJsonLoader` that takes either a `String` or a `File` to load a GeoJSON. For example, 
given a file, stored as a resource, that looks like 

```javascript
{
  "type": "Feature",
  "geometry": {
    "type": "Polygon",
    "coordinates": [
      [
        [0.0, 0.0],
        [10.0, 0.0],
        [10.0, 10.0],
        [0.0, 10.0],
        [0.0, 0.0]
      ]
    ]
  }
}
```

One can load it through the helper function:

```java
IGeometryObject square = GeoJsonLoader.loadFromResource("/data/samplePolygon.json");
```

The object can be stored back as a json-like-object (Map<String, Object>) with the `toMap()` function (`square.toMap()`) 
to get:

```text
{coordinates=[[[0.0, 0.0], [10.0, 0.0], [10.0, 10.0], [0.0, 10.0], [0.0, 0.0]]], type=Polygon}
```

### Statistics On A Geometry Object

An IGeometryObject supports calls to get the `centroid()`, `surfaceArea()` or `boundingBox()` of the shape, for example:

```java
square.centroid(); // the point (5.0,5.0)
square.surfaceArea(); // 100 square units
sqiare.boundingBox(); // the bounding box [(0.0,0.0),(10.0,10.0)]
```

### Object Interaction

A shape can be tested to see if it fully `within()` another object or if it fully `encases()` another object.

For example,

```java
new Point(1, 6).within(square); // True
``` 

or

```java
square.encases(new Point(1, 6)); // True
```

or


```java
new Point(12, -2).within(square); // False
``` 



## Misc Questions

### Does this library enforce the `right-hand rule` for polygons?

No

