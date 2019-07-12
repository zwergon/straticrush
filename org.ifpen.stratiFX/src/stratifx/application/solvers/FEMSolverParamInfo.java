package stratifx.application.solvers;

import stratifx.application.fxcontrollers.ISolverParameter;
import stratifx.application.fxcontrollers.ParamInfo;

public class FEMSolverParamInfo extends ParamInfo implements ISolverParameter {

    public FEMSolverParamInfo(){
        key = "FEMSolver";
        fxmlFile = "../solvers/FEMSolverUI.fxml";
    }
}
