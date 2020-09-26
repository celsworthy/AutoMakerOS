package celtech.coreUI.visualisation;

import celtech.Lookup;
import celtech.appManager.Project;
import celtech.coreUI.visualisation.DimensionLine.LineDirection;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ProjectifiableThing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;

/**
 *
 * @author Ian
 */
public class DimensionLineManager
{

    private final Map<ProjectifiableThing, List<DimensionLine>> dimensionLines = new HashMap<>();

    private final ChangeListener<Boolean> dragModeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
    {
        boolean labelVisible = (newValue == false);
        for (List<DimensionLine> dimensionLineList : dimensionLines.values())
        {
            for (DimensionLine dimensionLine : dimensionLineList)
            {
                dimensionLine.getDimensionLabel().setVisible(labelVisible);
            }
        }
    };

    public DimensionLineManager(Pane paneToAddDimensionsTo, Project project, ReadOnlyBooleanProperty hideDimensionsProperty)
    {
        hideDimensionsProperty.addListener(dragModeListener);

        Lookup.getProjectGUIState(project).getProjectSelection().addListener(
                new ProjectSelection.SelectedModelContainersListener()
                {

                    @Override
                    public void whenAdded(ProjectifiableThing projectifiableThing)
                    {
                        ArrayList<DimensionLine> lineList = new ArrayList<>();

                        if (projectifiableThing instanceof ScreenExtentsProviderTwoD)
                        {
                            DimensionLine verticalDimension = new DimensionLine();
                            DimensionLine horizontalDimension = new DimensionLine();
                            projectifiableThing.addScreenExtentsChangeListener(verticalDimension);
                            paneToAddDimensionsTo.getChildren().add(verticalDimension);
                            verticalDimension.initialise(project,
                                    projectifiableThing,
                                    LineDirection.VERTICAL);
                            lineList.add(verticalDimension);

                            projectifiableThing.addScreenExtentsChangeListener(horizontalDimension);
                            paneToAddDimensionsTo.getChildren().add(horizontalDimension);
                            horizontalDimension.initialise(project, projectifiableThing, LineDirection.HORIZONTAL);
                            lineList.add(horizontalDimension);

                            paneToAddDimensionsTo.getChildren().add(verticalDimension.getDimensionLabel());
                            paneToAddDimensionsTo.getChildren().add(horizontalDimension.getDimensionLabel());
                        }

                        if (projectifiableThing instanceof ScreenExtentsProviderThreeD)
                        {
                            DimensionLine frontBackDimension = new DimensionLine();
                            projectifiableThing.addScreenExtentsChangeListener(frontBackDimension);
                            paneToAddDimensionsTo.getChildren().add(frontBackDimension);
                            frontBackDimension.initialise(project, projectifiableThing, LineDirection.FORWARD_BACK);
                            lineList.add(frontBackDimension);

                            paneToAddDimensionsTo.getChildren().add(frontBackDimension.getDimensionLabel());
                        }

                        if (!lineList.isEmpty())
                        {
                            dimensionLines.put(projectifiableThing, lineList);

                            Platform.runLater(() ->
                            {
                                lineList.forEach((line) -> {
                                    line.screenExtentsChanged(projectifiableThing);
                                });
                            });
                        }
                    }

                    @Override

                    public void whenRemoved(ProjectifiableThing projectifiableThing)
                    {
                        List<DimensionLine> dimensionLinesToRemove = dimensionLines.get(projectifiableThing);
                        dimensionLinesToRemove.forEach(line ->
                        {
                            projectifiableThing.removeScreenExtentsChangeListener(line);
                            paneToAddDimensionsTo.getChildren().remove(line);
                            paneToAddDimensionsTo.getChildren().remove(line.getDimensionLabel());
                        });
                        dimensionLines.remove(projectifiableThing);
                    }
                }
        );
    }
}
