lc = 10;
//RadialNode Points
Point(0)={25525.7,5514.29,0,lc};
Point(1)={24140,4129.99,0,lc};
Point(2)={26011.9,6000,0,lc};
Point(3)={23790.9,3781.27,0,lc};
Point(4)={23151.1,3142.15,0,lc};
Point(5)={22617.3,2608.89,0,lc};
Point(6)={18500,5170.89,0,lc};
Point(7)={32000,5514.29,0,lc};
Point(8)={30000,3734.69,0,lc};
Point(9)={15000,2608.89,0,lc};
Point(10)={29000,2608.89,0,lc};
Point(11)={17000,4166.62,0,lc};
Point(12)={33000,6000,0,lc};
Point(13)={20000,6000,0,lc};
Point(14)={29600,3116.26,0,lc};

//Lines 
Line(1)={0,1};
Line(2)={2,0};
Line(3)={3,4};
Line(4)={1,3};
Line(5)={4,5};
Line(6)={6,1};
Line(7)={7,0};
Line(8)={8,3};
Line(9)={9,5};
Line(10)={5,10};
Line(11)={4,11};
Line(12)={12,2};
Line(13)={2,13};
Line(14)={13,6};
Line(15)={7,12};
Line(16)={6,11};
Line(17)={14,8};
Line(18)={8,7};
Line(19)={10,14};
Line(20)={11,9};

//Physical Line : feature : FaultFeatureClass f1
Physical Line(0)={1,2,3,4,5};
//Physical Line : feature : HorizonFeatureClass C
Physical Line(1)={6,7};
//Physical Line : feature : HorizonFeatureClass B
Physical Line(2)={8};
//Physical Line : feature : HorizonFeatureClass A
Physical Line(3)={9,10};
//Physical Line : feature : HorizonFeatureClass Erosion
Physical Line(4)={11};
//Physical Line : feature : ModelBoundaryFeatureClass left_border
Physical Line(5)={14,16,20};
//Physical Line : feature : ModelBoundaryFeatureClass bottom_border
Physical Line(6)={12,13};
//Physical Line : feature : ModelBoundaryFeatureClass right_border
Physical Line(7)={15,17,18,19};

//Loop C
Line Loop(0)={15,12,2,-7};
Plane Surface(0)={0};
//Loop C
Line Loop(1)={-1,-2,13,14,6};
Plane Surface(1)={1};
//Loop B
Line Loop(2)={1,4,-8,18,7};
Plane Surface(2)={2};
//Loop B
Line Loop(3)={-3,-4,-6,16,-11};
Plane Surface(3)={3};
//Loop A
Line Loop(4)={5,10,19,17,8,3};
Plane Surface(4)={4};
//Loop A
Line Loop(5)={20,9,-5,11};
Plane Surface(5)={5};

//StratigraphicUnit C
Physical Surface(0)={0,1};
//StratigraphicUnit B
Physical Surface(1)={2,3};
//StratigraphicUnit A
Physical Surface(2)={4,5};
