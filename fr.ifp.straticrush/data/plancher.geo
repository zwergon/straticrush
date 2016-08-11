lc = 1;
   
//Region
//Points de contour
Point(0)={0,0,0,lc};
Point(1)={0.2,0,0,lc};
Point(2)={0.2,0.2,0,lc};
Point(3)={0,0.2,0,lc};
Point(4)={0,0.12,0,lc};
Point(5)={0.01,0.1173,0,lc};
Point(6)={0.01732,0.11,0,lc};
Point(7)={0.02,0.1,0,lc};
Point(8)={0.01732,0.09,0,lc};
Point(9)={0.01,0.08268,0,lc};
Point(10)={0,0.08,0,lc};


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
Line(10)={10,0};



Line Loop(0)={0,1,2,3,4,5,6,7,8,9,10};

//Ecriture de la surface
Plane Surface(0)={0};