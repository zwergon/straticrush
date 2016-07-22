lc = 10;
 
//Region
//Points de contour
Point(0)={-1,0,0,lc};
Point(1)={-1,-1,0,lc};
Point(2)={1,-1,0,lc};
Point(3)={1,0,0,lc};

Point(4)={2,0,0,lc};
Point(5)={2,-1,0,lc};

Point(6)={-1,-2,0,lc};
Point(7)={1,-2,0,lc};

Point(8)={2,-2,0,lc};

//Segments de contour
Line(0)={0,1};
Line(1)={1,2};
Line(2)={2,3};
Line(3)={3,0};

Line Loop(0)={0,1,2,3};

//Segments de contour
Line(4)={2,5};
Line(5)={5,4};
Line(6)={4,3};

Line Loop(1)={-2,4,5,6};
//Segments de contour
Line(7)={1,6};
Line(8)={6,7};
Line(9)={7,2};

Line Loop(2)={-1,7,8,9};

//Segments de contour
Line(10)={7,8};
Line(11)={8,5};
Line(12)={5,2};
Line(13)={2,7};

Line Loop(3)={10,11,12,13};

//Physical Line : feature : HorizonFeatureClass H0
Physical Line(0)={-3,-6};
//Physical Line : feature : HorizonFeatureClass H1
Physical Line(1)={-1,-4};
//Physical Line : feature : HorizonFeatureClass H2
Physical Line(2)={-8,-10};
//Physical Line : feature : FaultFeatureClass F
Physical Line(3)={9,2};

//Ecriture de la surface
Plane Surface(0)={0};
 Plane Surface(1)={1};
Plane Surface(2)={2};
Plane Surface(3)={3};

//Ecriture de la physical surface
Physical Surface(0)={0};
Physical Surface(1)={1};
Physical Surface(2)={2};
Physical Surface(3)={3};