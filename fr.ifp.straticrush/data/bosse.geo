
lc = 1;
   
//Region
//Points de contour
Point(0)={0.000000,0.00000,0,lc};
Point(1)={0.500000,0.008787,0,lc};
Point(2)={0.600000,0.027067,0,lc};
Point(3)={0.700000,0.064930,0,lc};
Point(4)={0.800000,0.121306,0,lc};
Point(5)={0.900000,0.176499,0,lc};
Point(6)={1.000000,0.200000,0,lc};
Point(7)={1.100000,0.176499,0,lc};
Point(8)={1.200000,0.121306,0,lc};
Point(9)={1.300000,0.064930,0,lc};
Point(10)={1.400000,0.027067,0,lc};
Point(11)={1.500000,0.008787,0,lc};
Point(12)={2,0,0,lc};
Point(13)={2,1,0,lc};
Point(14)={0,1,0,lc};

//Segments de contour
Line(0)={0,1};
Line(1)={1,2};
Line(2)={2,3};
Line(3)={3,4};
Line(4)={4,5};
Line(5)={5,6};
Line(6)={6,7};
Line(7)={7,8};
Line(8)={8,9};
Line(9)={9,10};
Line(10)={10,11};
Line(11)={11,12};
Line(12)={12,13};
Line(13)={13,14};
Line(14)={14,0};




Line Loop(0)={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14};

//Ecriture de la surface
Plane Surface(0)={0};

//Physical Line : feature : HorizonFeatureClass H0
Physical Line(0)={0,1,2,3,4,5,6,7,8,9,10,11};
//Physical Line : feature : HorizonFeatureClass H1
Physical Line(1)={12,13};

//Ecriture de la physical surface
Physical Surface(0)={0};

