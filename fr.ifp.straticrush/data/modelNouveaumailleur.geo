lc = 10;
   
//Region
//Points de contour
Point(0)={0,0,0,lc};
Point(1)={0.5,0,0,lc};
Point(2)={1,-0.5,0,lc};
Point(3)={1.5,-1,0,lc};
Point(4)={0,-1,0,lc};

Point(5)={2,-1.5,0,lc};
Point(6)={2.5,-2,0,lc};
Point(7)={0,-2,0,lc};

Point(8)={4,-0.5,0,lc};
Point(9)={4,-1.5,0,lc};

Point(10)={4,-2.5,0,lc};
Point(11)={3,-2.5,0,lc};

//Segments de contour
Line(0)={0,1};
Line(1)={1,2};
Line(2)={2,3};
Line(3)={3,4};
Line(4)={4,0};

Line Loop(0)={0,1,2,3,4};

//Segments de contour
Line(5)={3,5};
Line(6)={5,6};
Line(7)={6,7};
Line(8)={7,4};

Line Loop(1)={-3,5,6,7,8};
//Segments de contour
Line(9)={2,8};
Line(10)={8,9};
Line(11)={9,5};

Line Loop(2)={-5,-2,9,10,11};

//Segments de contour
Line(12)={5,9};
Line(13)={9,10};
Line(14)={10,11};
Line(15)={11,6};

Line Loop(3)={-6,12,13,14,15};

//Physical Line : feature : HorizonFeatureClass H0
Physical Line(0)={7,14};
//Physical Line : feature : HorizonFeatureClass H1
Physical Line(1)={3,11};
//Physical Line : feature : HorizonFeatureClass H2
Physical Line(2)={0,9};
//Physical Line : feature : FaultFeatureClass F
Physical Line(3)={1,2,5,6,-15};

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