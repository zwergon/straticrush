package stratifx.application.solvers;

import fr.ifp.kronosflow.fem2d.solve.FEMSolverStyle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import stratifx.application.main.GParameters;

import java.net.URL;
import java.util.ResourceBundle;

public class FEMSolverUIController implements Initializable {

    @FXML
    AnchorPane femsolverPane;

    @FXML
    CheckBox lsConstraintCB;

    @FXML
    ComboBox reductionTypeCb;

    @FXML
    ComboBox solverTypeCb;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        for (FEMSolverStyle.ReductionType rt : FEMSolverStyle.ReductionType.values()) {
            reductionTypeCb.getItems().add(rt.toString());
        }

        for (FEMSolverStyle.SolverType rt : FEMSolverStyle.SolverType.values()) {
            solverTypeCb.getItems().add(rt.toString());
        }

        FEMSolverStyle solverStyle = new FEMSolverStyle(GParameters.getStyle());
        reductionTypeCb.setValue(solverStyle.getReductionType().toString());
        solverTypeCb.setValue(solverStyle.getSolverType().toString());
        lsConstraintCB.setSelected(solverStyle.getLSConstraint());


    }

    @FXML
    public void onApplyAction(ActionEvent action) {

        FEMSolverStyle solverStyle = new FEMSolverStyle(GParameters.getStyle());
        solverStyle.setSolverType( FEMSolverStyle.SolverType.valueOf(solverTypeCb.getValue().toString()));
        solverStyle.setReductionType( FEMSolverStyle.ReductionType.valueOf(reductionTypeCb.getValue().toString()));
        solverStyle.setLSConstraint( lsConstraintCB.isSelected() );
    }
}
