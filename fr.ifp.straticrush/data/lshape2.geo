cl__1 = 1;


Point(1) =  {-1,   0, 0, 1};
Point(2) =  {-0.8, 0, 0, 1};
Point(3) =  {-0.6, 0, 0, 1};
Point(4) =  {-0.4, 0, 0, 1};
Point(5) =  {-0.2, 0, 0, 1};
Point(6) =  { 0,   0, 0, 1};
Point(7) =  { 0, 0.2, 0, 1};
Point(8) = { 0, 0.4, 0, 1};
Point(9) = { 0, 0.6, 0, 1};
Point(10) = { 0, 0.8, 0, 1};
Point(11) =  { 0,   1, 0, 1};
Point(12) = {-0.2, 1, 0, 1};
Point(13) = {-0.4, 1, 0, 1};
Point(14) = {-0.6, 1, 0, 1};
Point(15) = {-0.8, 1, 0, 1};
Point(16) =  {-1,   1, 0, 1};
Point(17) = {-1, 0.8, 0, 1};
Point(18) = {-1, 0.6, 0, 1};
Point(19) = {-1, 0.4, 0, 1};
Point(20) = {-1, 0.2, 0, 1};


Point(21) = {  0, 1, 0, 1};
Point(22) = {0.2, 1, 0, 1};
Point(23) = {0.4, 1, 0, 1};
Point(24) = {0.6, 1, 0, 1};
Point(25) = {0.8, 1, 0, 1};
Point(26) = {  1, 1, 0, 1};
Point(27) = {  1, 0.8, 0, 1};
Point(28) = {  1, 0.6, 0, 1};
Point(29) = {  1, 0.4, 0, 1};
Point(30) = {  1, 0.2, 0, 1};
Point(31) = {  1, 0, 0, 1};
Point(32) = {  1, -0.2, 0, 1};
Point(33) = {  1, -0.4, 0, 1};
Point(34) = {  1, -0.6, 0, 1};
Point(35) = {  1, -0.8, 0, 1};
Point(36) = {  1, -1, 0, 1};
Point(37) = {0.8, -1, 0, 1};
Point(38) = {0.6, -1, 0, 1};
Point(39) = {0.4, -1, 0, 1};
Point(40) = {0.2, -1, 0, 1};
Point(41) = {  0, -1, 0, 1};
Point(42) = {  0, -0.8, 0, 1};
Point(43) = {  0, -0.6, 0, 1};
Point(44) = {  0, -0.4, 0, 1};
Point(45) = {  0, -0.2, 0, 1};
Point(46) = {  0,    0, 0, 1};
Point(47) = {  0,  0.2, 0, 1};
Point(48) = {  0,  0.4, 0, 1};
Point(49) = {  0,  0.6, 0, 1};
Point(50) = {  0,  0.8, 0, 1};

Line(1) = {1, 2};
Line(2) = {2, 3};
Line(3) = {3, 4};
Line(4) = {4, 5};
Line(5) = {5, 6};
Line(6) = {6, 7};
Line(7) = {7, 8};
Line(8) = {8, 9};
Line(9) = {9, 10};
Line(10) = {10, 11};
Line(11) = {11, 12};
Line(12) = {12, 13};
Line(13) = {13, 14};
Line(14) = {14, 15};
Line(15) = {15, 16};
Line(16) = {16, 17};
Line(17) = {17, 18};
Line(18) = {18, 19};
Line(19) = {19, 20};
Line(20) = {20, 1};

Line(28) = {21, 22};
Line(29) = {22, 23};
Line(30) = {23, 24};
Line(31) = {24, 25};
Line(32) = {25, 26};
Line(33) = {26, 27};
Line(34) = {27, 28};
Line(35) = {28, 29};
Line(36) = {29, 30};
Line(37) = {30, 31};
Line(38) = {31, 32};
Line(39) = {32, 33};
Line(40) = {33, 34};
Line(41) = {34, 35};
Line(42) = {35, 36};
Line(43) = {36, 37};
Line(44) = {37, 38};
Line(45) = {38, 39};
Line(46) = {39, 40};
Line(47) = {40, 41};
Line(48) = {41, 42};
Line(49) = {42, 43};
Line(50) = {43, 44};
Line(51) = {44, 45};
Line(52) = {45, 46};
Line(53) = {46, 47};
Line(54) = {47, 48};
Line(55) = {48, 49};
Line(56) = {49, 50};
Line(57) = {50, 21};

Line Loop(58) = {13, 14, 15, 16, 17, 18, 19, 20, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
Plane Surface(59) = {58};

Line Loop(60) = {28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 };
Plane Surface(61) = {60};
