lc = 10;
//RadialNode Points
Point(0)={434.886,485.15,0,lc};
Point(1)={563.584,521.501,0,lc};
Point(2)={360.868,438.111,0,lc};
Point(3)={1103.9,520.874,0,lc};
Point(4)={1800.12,515.676,0,lc};
Point(5)={303.494,385.821,0,lc};
Point(6)={254.87,330.811,0,lc};
Point(7)={222.867,280.278,0,lc};
Point(8)={192.954,218.455,0,lc};
Point(10)={0,218.455,0,lc};
Point(11)={587.231,482.071,0,lc};
Point(12)={596.893,465.96,0,lc};
Point(13)={617.742,431.196,0,lc};
Point(14)={651.357,375.147,0,lc};
Point(15)={684.328,320.171,0,lc};
Point(16)={712.742,272.791,0,lc};
Point(17)={745.598,217.918,0,lc};
Point(18)={582.762,432.138,0,lc};
Point(19)={555.749,378.08,0,lc};
Point(20)={528.162,324.04,0,lc};
Point(21)={495.628,276.11,0,lc};
Point(22)={455.882,219.486,0,lc};
Point(23)={1104.54,471.614,0,lc};
Point(24)={1105.24,418.074,0,lc};
Point(25)={1105.98,361.197,0,lc};
Point(26)={1106.65,309.707,0,lc};
Point(27)={1107.21,266.762,0,lc};
Point(28)={1107.87,215.958,0,lc};
Point(29)={1800.12,536.926,0,lc};
Point(30)={0,536.926,0,lc};
Point(31)={554.333,536.926,0,lc};
Point(32)={1103.69,536.926,0,lc};

//Lines 
//Internal Points 
Point(33)={495.772,509.99,0,lc};
Line(60)={0,33};
Line(1)={33,1};
//Internal Points 
Point(34)={409.445,474.771,0,lc};
Line(2)={2,34};
Line(3)={34,0};
//Internal Points 
Point(35)={587.986,525.643,0,lc};
Point(36)={727.286,523.686,0,lc};
Line(4)={1,35};
Line(5)={35,36};
Line(6)={36,3};
Line(7)={3,4};
//Internal Points 
Point(37)={336.851,419.986,0,lc};
Line(8)={2,37};
Line(9)={37,5};
//Internal Points 
Point(38)={264.258,345.635,0,lc};
Line(10)={5,38};
Line(11)={38,6};
Line(12)={6,7};
//Internal Points 
Point(39)={217.17,271.284,0,lc};
Line(16)={10,8};
Line(17)={11,12};
Line(18)={1,11};
Line(19)={13,12};
Line(20)={14,13};
Line(21)={15,14};
Line(22)={16,15};
//Internal Points 
Line(25)={12,18};
//Internal Points 
Point(41)={580.138,425.856,0,lc};
Line(26)={19,41};
Line(27)={41,18};
Line(28)={20,19};
//Internal Points 
Line(31)={22,21};
Line(32)={0,11};
Line(33)={11,23};
Line(34)={13,18};
Line(35)={18,2};
Line(36)={24,13};
Line(37)={24,23};
Line(38)={23,3};
Line(39)={25,24};
Line(40)={26,25};
Line(41)={27,26};
Line(42)={28,27};
Line(43)={5,19};
Line(44)={14,19};
Line(45)={25,14};
Line(46)={6,20};
Line(47)={15,20};
Line(48)={26,15};
Line(49)={16,21};
Line(50)={27,16};
Line(51)={7,21};
Line(52)={22,17};
Line(53)={17,28};
Line(55)={4,29};
Line(56)={30,10};
Line(57)={31,30};
Line(58)={29,32};
Line(59)={32,31};
Line(61) = {17, 16};
Line(66) = {20, 21};
Line(67) = {7, 39};
Line(68) = {39, 8};
Line(69) = {22, 8};


//Physical Line : feature : FaultFeatureClass f30
Physical Line(0)={60,1,2,3,4,5,6,7,8,9,10,11,12,67,68};
//Physical Line : feature : HorizonFeatureClass B7
Physical Line(1)={16};
//Physical Line : feature : FaultFeatureClass f31
Physical Line(2)={17,18,19,20,21,22,61};
//Physical Line : feature : FaultFeatureClass f32
Physical Line(3)={25,27,26,28,66,31};
//Physical Line : feature : HorizonFeatureClass B6
Physical Line(4)={32,33};
//Physical Line : feature : HorizonFeatureClass B5
Physical Line(5)={34,35,36};
//Physical Line : feature : FaultFeatureClass f33
Physical Line(6)={37,38,39,40,41,42};
//Physical Line : feature : HorizonFeatureClass B4
Physical Line(7)={43,44,45};
//Physical Line : feature : HorizonFeatureClass B3
Physical Line(8)={46,47,48};
//Physical Line : feature : HorizonFeatureClass B2
Physical Line(9)={49,50,51};
//Physical Line : feature : HorizonFeatureClass B1
Physical Line(10)={52,53,69};
//Physical Line : feature : ModelBoundaryFeatureClass left_border
Physical Line(11)={56};
//Physical Line : feature : ModelBoundaryFeatureClass bottom_border
Physical Line(12)={57,58,59};
//Physical Line : feature : ModelBoundaryFeatureClass right_border
Physical Line(13)={55};

//Loop B6
Line Loop(0)={-1,-60,32,-18};
Plane Surface(0)={0};
//Loop B5
Line Loop(1)={25,-34,19};
Plane Surface(1)={1};
//Loop B5
Line Loop(2)={-32,-3,-2,-35,-25,-17};
Plane Surface(2)={2};
//Loop B5
Line Loop(3)={-19,-36,37,-33,17};
Plane Surface(3)={3};
//Loop B6
Line Loop(4)={33,38,-6,-5,-4,18};
Plane Surface(4)={4};
//Loop B4
Line Loop(5)={-45,39,36,-20};
Plane Surface(5)={5};
//Loop B4
Line Loop(6)={34,-27,-26,-44,20};
Plane Surface(6)={6};
//Loop B4
Line Loop(7)={35,8,9,43,26,27};
Plane Surface(7)={7};
//Loop B3
Line Loop(8)={-48,40,45,-21};
Plane Surface(8)={8};
//Loop B3
Line Loop(9)={44,-28,-47,21};
Plane Surface(9)={9};
//Loop B3
Line Loop(10)={28,-43,10,11,46};
Plane Surface(10)={10};
//Loop B2
Line Loop(11)={-50,41,48,-22};
Plane Surface(11)={11};
//Loop B2
Line Loop(12) = {69, -68, -67, 51, -31};
Plane Surface(12) = {12};
Line Loop(13) = {46, 66, -51, -12};
Plane Surface(13) = {13};
//Loop B1
Line Loop(14) = {53, 42, 50, -61};
Plane Surface(14) = {14};
//Loop B1
Line Loop(15) = {52, 61, 49, -31};
Plane Surface(15) = {15};
//Loop B7
Line Loop(16) = {16, -68, -67, -12, -11, -10, -9, -8, 2, 3, 60, 1, 4, 5, 6, 7, 55, 58, 59, 57, 56};
Plane Surface(16) = {16};
Line Loop(17) = {49, -66, -47, -22};
Plane Surface(17) = {17};

//StratigraphicUnit B6
Physical Surface(0)={0,4};
//StratigraphicUnit B5
Physical Surface(1)={1,2,3};
//StratigraphicUnit B4
Physical Surface(2)={5,6,7};
//StratigraphicUnit B3
Physical Surface(3)={8,9,10};
//StratigraphicUnit B2
Physical Surface(4)={11,12,13};
//StratigraphicUnit B1
Physical Surface(5)={14,15,17};
//StratigraphicUnit B7
Physical Surface(6)={16};



