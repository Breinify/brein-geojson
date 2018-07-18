## Bugs / Warnings

### Multi Polygon encasing problem

GeometryCollection.encase(...) has a bug where if we have two shapes, A and B, that touch (but not necessarily overlap), 
then there can be a shape C such that A and B combined encase C, but neither A or B (separately) encase C. This use case 
is not currently supported.
