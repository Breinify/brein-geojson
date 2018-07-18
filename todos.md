## Bugs / Warnings

### Multi Polygon encasing problem

GeometryCollection.encase(...) has a bug where if we have two shapes, A and B, that touch (but not necessarily overlap), 
then there can be a shape C such that A and B combined encase C, but neither A or B (separately) encase C. This use case 
is not currently supported.

### Polygon / Polygon Encasing with Holes

If there's a doughnut-like-shape centered at (0,0) and with inner radius of 0.5 and outer radius of 1.5, then a polygon 
that's also centered at (0,0) and has a radius of 1.0 will not currently be detected as not being encased. This becomes 
more of a problem because we have to handle cases when the second polygon *also* has an inner radius of 0.5 (in which 
case it actually does encase the polygon.)