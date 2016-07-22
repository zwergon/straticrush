lc = 10;
   
//Region
//Points de contour
Point(0)={0,0,0,lc};
Point(1)={0.5,0,0,lc};
Point(2)={1,0.5,0,lc};
Point(3)={4,0.5,0,lc};
Point(4)={4,2.5,0,lc};
Point(5)={3,2.5,0,lc};
Point(6)={2.5,2,0,lc};
Point(7)={0,2,0,lc};



//Segments de contour
Line(0)={0,1};
Line(1)={1,2};
Line(2)={2,3};
Line(3)={3,4};
Line(4)={4,5};
Line(5)={5,6};
Line(6)={6,7};
Line(7)={7,0};


Line Loop(0)={0,1,2,3,4,5,6,7};

//Ecriture de la surface
Plane Surface(0)={0};

//Physical Line : feature : HorizonFeatureClass H0
Physical Line(0)={0,1,2};
//Physical Line : feature : HorizonFeatureClass H1
Physical Line(1)={4,5,6};

//Ecriture de la physical surface
Physical Surface(0)={0};
