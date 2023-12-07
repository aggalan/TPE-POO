package frontend;

import backend.CanvasState;
import backend.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;


import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class PaintPane extends BorderPane {

	// BackEnd
	private final CanvasState canvasState;

	// Canvas y relacionados
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();

	private final Color defaultFillColor = Color.YELLOW;

	// Botones Barra Izquierda
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final ToggleButton circleButton = new ToggleButton("Círculo");
	private final ToggleButton squareButton = new ToggleButton("Cuadrado");
	private final ToggleButton ellipseButton = new ToggleButton("Elipse");
	private final ToggleButton deleteButton = new ToggleButton("Borrar");
	private final Button groupButton = new Button("Agrupar");
	private final Button ungroupButton = new Button("Desagrupar");
	private final Button turnRightButton = new Button("Girar D");
	private final Button flipHorizontalButton = new Button("Voltear H");
	private final Button flipVerticalButton = new Button("Voltear V");
	private final Button scaleButton = new Button("Escalar +");
	private final Button descaleButton = new Button("Escalar -");
	private final CheckBox checkBoxShadow = new CheckBox("Sombra");
	private final CheckBox checkBoxGradient = new CheckBox("Gradiente");
	private final CheckBox checkBoxBiselado = new CheckBox("Biselado");

	// Selector de color de relleno
	private ColorPicker fillColorPicker = new ColorPicker(defaultFillColor);

	// Dibujar una figura
	private Point startPoint;

	// Seleccionar una figura
	//private FrontFigure<? extends Figure> selectedFigure;
	private Set<FrontFigure<? extends Figure>> selectedFigures = new HashSet<>(); //asi no se puede agregar el mismo puntero dos veces, sirve para seleccion multiple comboinado con group

	private List<ShapeGroup> shapeGroups = new ArrayList<>();

	// StatusBar
	private final StatusPane statusPane;

	private final CheckBoxState checkBoxState = new CheckBoxState();

	// Colores de relleno de cada figura
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;

		Button[] functionalities = {groupButton, ungroupButton, turnRightButton, flipHorizontalButton, flipVerticalButton, scaleButton, descaleButton};
		for (Button functionality : functionalities) {
			functionality.setMinWidth(90);
			functionality.setCursor(Cursor.HAND);
		}

		ToggleButton rectangleButton = new ToggleButton("Rectángulo");
		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton, deleteButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}

		ToggleButton[] figuresArr = {rectangleButton, circleButton, squareButton, ellipseButton};

		Map<ToggleButton, BiFunction<Point, Point, FrontFigure<? extends Figure>>> buttonMap = new HashMap<>() {{
			put(figuresArr[0], (startPoint, endPoint) -> new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, fillColorPicker.getValue()));
			put(figuresArr[1], (startPoint, endPoint) -> new FrontEllipse<>(new Circle(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, fillColorPicker.getValue()));
			put(figuresArr[2], (startPoint, endPoint) -> new FrontRectangle<>(new Square(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, fillColorPicker.getValue()));
			put(figuresArr[3], (startPoint, endPoint) -> new FrontEllipse<>(new Ellipse(new Point(Math.abs(endPoint.x + startPoint.x) / 2, (Math.abs((endPoint.y + startPoint.y)) / 2)), Math.abs(endPoint.x - startPoint.x), Math.abs(endPoint.y - startPoint.y)), gc, fillColorPicker.getValue()));
		}};

		VBox buttonsBox = new VBox(10);
		buttonsBox.getChildren().addAll(toolsArr);
		buttonsBox.getChildren().addAll(functionalities);
		buttonsBox.getChildren().add(fillColorPicker);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.setStyle("-fx-background-color: #999");
		buttonsBox.setPrefWidth(100);
		gc.setLineWidth(1);

		HBox effect = new HBox(10);
		Label label1 = new Label("Efectos:");
		CheckBox[] effects = {checkBoxShadow, checkBoxGradient, checkBoxBiselado};
		effect.getChildren().addAll(label1, checkBoxShadow, checkBoxGradient, checkBoxBiselado);
		for (CheckBox checkbox : effects) {
			checkbox.setMinWidth(10);
			checkbox.setCursor(Cursor.HAND);
		}
		effect.setAlignment(Pos.CENTER);
		effect.setStyle("-fx-background-color: #999");
		BorderPane topPane = new BorderPane();
		topPane.setTop(effect);
		setTop(topPane);

		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
			for (FrontFigure<? extends Figure> figure : selectedFigures) { // this makes it so when multiple figures selected and you try to select more creating a new imaginary rectangle, it does that insted of moving the selected figures
				if (figureBelongs(figure, new Point(event.getX(), event.getY()))) {
					return;
				}
			}
			selectedFigures.clear();


		});


		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if (startPoint == null || endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return;
			}
			FrontFigure<? extends Figure> newFigureAux = null;

			boolean found = false;

			for (ToggleButton button : figuresArr) {
				if (button.isSelected()) {
					found = true;
					BiFunction<Point, Point, FrontFigure<? extends Figure>> figureFunction = buttonMap.get(button);
					newFigureAux = figureFunction.apply(startPoint, endPoint);

				}
			}

			if (selectionButton.isSelected() && !startPoint.equals(endPoint)) {
				for (FrontFigure<? extends Figure> figure : canvasState) {
					if (figure.getFigure().belongsInRectangle(new Rectangle(startPoint, endPoint))) {
						for (ShapeGroup group : shapeGroups) {
							if (group.contains(figure)) {
								selectedFigures.addAll(group); // ver posibilidad de que sea set selectedFigures?
								selectedFigures.remove(figure); //boca boca boca pero paja arreglenlo si quieren ni se si necesario
							}
						}
						selectedFigures.add(figure);
					}
				}
				startPoint = null;
				redrawCanvas();
				return;
			}
			if (!found) {
				return;
			}
			canvasState.add(newFigureAux);
			startPoint = null;
			updateCheckBoxesState();
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for (FrontFigure<? extends Figure> figure : canvasState) {
				if (figureBelongs(figure, eventPoint)) {
					found = true;
					label.append(figure);
				}
			}
			if (found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});

		canvas.setOnMouseClicked(event -> {
			if (selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				boolean groupFound;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (FrontFigure<? extends Figure> figure : canvasState) {
					if (figureBelongs(figure, eventPoint)) {
						groupFound = false;
						for (ShapeGroup group : shapeGroups) {
							if (group.contains(figure)) {
								selectedFigures.clear();
								selectedFigures.addAll(group);
								groupFound = true;
								label.append(group);
								break;
							}
						}
						if (!groupFound) {
							selectedFigures.clear();
							selectedFigures.add(figure);
							label.append(figure);
						}
						found = true;
					}
				}
				if (!found && startPoint != null && startPoint.equals(eventPoint)) {
					selectedFigures.clear();

					statusPane.updateStatus("Ninguna figura encontrada");
				}else statusPane.updateStatus(label.toString());
				updateCheckBoxesState();
				redrawCanvas();
			}
		});


		canvas.setOnMouseDragged(event -> {
			if (selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.getX() - startPoint.getX());
				double diffY = (eventPoint.getY() - startPoint.getY());
				if (!selectedFigures.isEmpty()) {
					for (FrontFigure<? extends Figure> figure : selectedFigures) {
						figure.getFigure().move(diffX, diffY);
					}
					startPoint.movePoint(diffX, diffY);
				}
				redrawCanvas();
			}
		});


		deleteButton.setOnAction(event -> deleteButtonAction());

		checkBoxBiselado.setOnAction(event -> selectedFigures.forEach(figure -> figure.beveledStatus(checkBoxBiselado.isSelected())));

		checkBoxShadow.setOnAction(event -> selectedFigures.forEach(figure -> figure.shadowStatus(checkBoxShadow.isSelected())));

		checkBoxGradient.setOnAction(event -> selectedFigures.forEach(figure -> figure.gradientStatus(checkBoxGradient.isSelected())));

		groupButton.setOnAction(event -> groupButtonAction());

		ungroupButton.setOnAction(event -> ungroupButtonAction());

		turnRightButton.setOnAction(event -> selectedFigures.forEach(figure -> figure.getFigure().rotate()));

		flipVerticalButton.setOnAction(event -> selectedFigures.forEach(figure -> figure.getFigure().flipVertically()));

		flipHorizontalButton.setOnAction(event -> selectedFigures.forEach(figure -> figure.getFigure().flipHorizontally()));

		scaleButton.setOnAction(event -> selectedFigures.forEach(figure -> figure.getFigure().scale()));

		descaleButton.setOnAction(event -> selectedFigures.forEach(figure -> figure.getFigure().descale()));

		buttonsBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());
		effect.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());

		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (FrontFigure<? extends Figure> figure : canvasState) {
			if (selectedFigures != null && (selectedFigures.contains(figure))) {
				gc.setStroke(Color.RED);
			} else {
				gc.setStroke(CanvasState.LINE_COLOR);
			}
			if (figure.shadowStatus) {
				figure.createShadow();
			}
			gc.setFill(figure.getColor());
			figure.create(0);
		}
	}

	private boolean figureBelongs(FrontFigure<? extends Figure> figure, Point eventPoint) {
		return figure.getFigure().belongs(eventPoint);
	}

	private void deleteButtonAction() {
		if (!selectedFigures.isEmpty()) {
			shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains));
			canvasState.removeAll(selectedFigures);
			selectedFigures.clear();
			redrawCanvas();
		}
	}

	private void groupButtonAction() {
		if (selectedFigures.size() <= 1) {
			return;
		}
		ShapeGroup aux = new ShapeGroup();
		aux.addAll(selectedFigures);
		shapeGroups.add(aux);
		selectedFigures.clear();
		redrawCanvas();
	}

	private void ungroupButtonAction() {
		if (selectedFigures.isEmpty() || shapeGroups.isEmpty()) {
			return;
		}
		shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains));
		selectedFigures.clear();
		redrawCanvas();
	}

	private void updateCheckBoxesState() {
		if (!selectedFigures.isEmpty()) {
			checkBoxState.resetState();

			boolean mixed = false;
			boolean auxShadowStatus = selectedFigures.iterator().next().shadowStatus;
			boolean auxGradientStatus = selectedFigures.iterator().next().gradientStatus;
			boolean auxBeveledStatus = selectedFigures.iterator().next().beveledStatus;

			boolean auxCurrentShadowStatus;
			boolean auxCurrentGradientStatus;
			boolean auxCurrentBeveledStatus;

			for (FrontFigure<? extends Figure> figure : selectedFigures) {
				if (figure.getShadowStatus() != auxShadowStatus) {
					mixed = true;
					checkBoxShadow.allowIndeterminateProperty();
					checkBoxShadow.setIndeterminate(true);
				}
				if (figure.getGradientStatus() != auxGradientStatus) {
					mixed = true;
					checkBoxGradient.allowIndeterminateProperty();
					checkBoxGradient.setIndeterminate(true);
				}
				if (figure.getBeveledStatus() != auxBeveledStatus) {
					mixed = true;
					checkBoxBiselado.allowIndeterminateProperty();
					checkBoxBiselado.setIndeterminate(true);
				}


				if (!mixed) {
					checkBoxState.updateState(figure);
				}

			}
			checkBoxShadow.setSelected(checkBoxState.getCommonShadowStatus());
			checkBoxGradient.setSelected(checkBoxState.getCommonGradientStatus());
			checkBoxBiselado.setSelected(checkBoxState.getCommonBeveledStatus());


		} else {
			resetCheckBoxes();
		}
	}

	private void resetCheckBoxes() {
		checkBoxShadow.setSelected(false);
		checkBoxGradient.setSelected(false);
		checkBoxBiselado.setSelected(false);
		resetCheckBoxState(checkBoxShadow);
		resetCheckBoxState(checkBoxGradient);
		resetCheckBoxState(checkBoxBiselado);
	}



	private void resetCheckBoxState(CheckBox checkBox) {
		checkBox.setIndeterminate(false);
		checkBox.setAllowIndeterminate(false);
	}

}
