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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PaintPane extends BorderPane {

	// BackEnd
	private final CanvasState canvasState;

	// Canvas y relacionados
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();

	private final Color DEFAULT_FILL_COLOR = Color.YELLOW;
	private final Color LINE_COLOR = Color.BLACK;
	private final Color SELECTED_LINE_COLOR = Color.RED;


	// Botones Barra Izquierda
	private final ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private final ToggleButton circleButton = new ToggleButton("Círculo");
	private final ToggleButton squareButton = new ToggleButton("Cuadrado");
	private final ToggleButton ellipseButton = new ToggleButton("Elipse");
	private final Button deleteButton = new Button("Borrar");
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
	private ColorPicker fillColorPicker = new ColorPicker(DEFAULT_FILL_COLOR);

	// Dibujar una figura
	private Point startPoint;

	private Set<FrontFigure<? extends Figure>> selectedFigures = new HashSet<>(); //asi no se puede agregar el mismo puntero dos veces, sirve para seleccion multiple comboinado con group

	private List<ShapeGroup> shapeGroups = new ArrayList<>();

	// StatusBar
	private final StatusPane statusPane;

	private final CheckBoxState checkBoxState = new CheckBoxState();

	Map<CheckBox, Function< FrontFigure<? extends Figure>, Boolean>> statusMap = new HashMap<>() {{
		put(checkBoxShadow, FrontFigure::getShadowStatus);
		put(checkBoxGradient, FrontFigure::getGradientStatus);
		put(checkBoxBiselado, FrontFigure::getBeveledStatus);
	}};

	// Colores de relleno de cada figura
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;

		Button[] functionalities = {deleteButton, groupButton, ungroupButton, turnRightButton, flipHorizontalButton, flipVerticalButton, scaleButton, descaleButton};
		for (Button functionality : functionalities) {
			functionality.setMinWidth(90);
			functionality.setCursor(Cursor.HAND);
		}

		ToggleButton rectangleButton = new ToggleButton("Rectángulo");
		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}

		ToggleButton[] figuresArr = {rectangleButton, circleButton, squareButton, ellipseButton, selectionButton};

		Map<ToggleButton, BiFunction<Point, Point, FrontFigure<? extends Figure>>> buttonMap = new HashMap<>() {{
			put(figuresArr[0], (startPoint, endPoint) -> new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, fillColorPicker.getValue()));
			put(figuresArr[1], (startPoint, endPoint) -> new FrontEllipse<>(new Circle(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, fillColorPicker.getValue()));
			put(figuresArr[2], (startPoint, endPoint) -> new FrontRectangle<>(new Square(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, fillColorPicker.getValue()));
			put(figuresArr[3], (startPoint, endPoint) -> new FrontEllipse<>(new Ellipse(new Point(Math.abs(endPoint.x + startPoint.x) / 2, (Math.abs((endPoint.y + startPoint.y)) / 2)), Math.abs(endPoint.x - startPoint.x), Math.abs(endPoint.y - startPoint.y)), gc, fillColorPicker.getValue()));
			put(figuresArr[4], (startPoint, endPoint) -> new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, Color.color(0,0,0,0)));
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
			if (startPoint == null) {
				return;
			}
			FrontFigure<? extends Figure> newFigureAux = null;

			boolean found = false;

			for (ToggleButton button : figuresArr) {
				if (button.isSelected() && !startPoint.equals(endPoint)) {
					found = true;
					BiFunction<Point, Point, FrontFigure<? extends Figure>> figureFunction = buttonMap.get(button);
					newFigureAux = figureFunction.apply(startPoint, endPoint);
					selectedFigures.remove(newFigureAux);

				}
			}

			if (selectionButton.isSelected() && !startPoint.equals(endPoint)) {
				for (FrontFigure<? extends Figure> figure : canvasState) {
					if (figure.getFigure().belongsInRectangle(new Rectangle(startPoint, endPoint))) {
						for (ShapeGroup group : shapeGroups) {
							if (group.contains(figure)) {
								selectedFigures.addAll(group);
							}
						}
						selectedFigures.add(figure);
					}
				}
//				startPoint = null;
				redrawCanvas();
				return;
			}
			if (!found) {
				return;
			}
			canvasState.add(newFigureAux);
//			startPoint = null;
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
			Point eventPoint = new Point(event.getX(), event.getY());
			if (selectionButton.isSelected()  && startPoint != null  && startPoint.equals(eventPoint)) {
				boolean found = false;
				boolean groupFound;
				StringBuilder label = new StringBuilder("Se seleccionó: ");

				int maxIndex = 0, currentIndex = 0, selectedFiguresSize = selectedFigures.size();


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
							currentIndex = canvasState.indexOf(figure);
							if (currentIndex > maxIndex && selectedFiguresSize <= 1) {
								maxIndex = currentIndex;
								selectedFigures.clear();
							}
							selectedFigures.add(figure);
							label.append(figure);
						}
						found = true;
					}
				}
				if (!found) {
					selectedFigures.clear();
					statusPane.updateStatus("Ninguna figura encontrada");
				}else statusPane.updateStatus(label.toString());
				redrawCanvas();
			}
		});


		canvas.setOnMouseDragged(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			if (selectionButton.isSelected()) {
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

			FrontFigure<? extends Figure> transitionFigure = new FrontRectangle<>(new Rectangle(startPoint,eventPoint), gc, fillColorPicker.getValue());
			for (ToggleButton button : figuresArr) {
				if (button.isSelected()) {
					BiFunction<Point, Point, FrontFigure<? extends Figure>> figureFunction = buttonMap.get(button);
					transitionFigure = figureFunction.apply(startPoint, eventPoint);
					canvasState.add(transitionFigure);
					redrawCanvas();
					canvasState.remove(transitionFigure);

				}
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

		clearSelectedFigures(toolsArr);

		buttonsBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());
		effect.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());

		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (FrontFigure<? extends Figure> figure : canvasState) {
			if (selectedFigures != null && (selectedFigures.contains(figure))) {
				gc.setStroke(SELECTED_LINE_COLOR);
			} else {
				gc.setStroke(LINE_COLOR);
			}
			if (figure.shadowStatus) {
				figure.createShadow();
			}
			gc.setFill(figure.getColor());
			figure.create(0);
		}
		updateCheckBoxesState();
	}

	private boolean figureBelongs(FrontFigure<? extends Figure> figure, Point eventPoint) {
		return figure.getFigure().belongs(eventPoint);
	}

	private void deleteButtonAction() {
		if (!selectedFigures.isEmpty()) {
			shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains));
			canvasState.removeAll(selectedFigures);
			selectedFigures.clear();
		}
	}

	private void groupButtonAction() {
		if (selectedFigures.size() <= 1) {
			return;
		}
		ShapeGroup aux = new ShapeGroup();
		aux.addAll(selectedFigures);
		shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains)); // elegimos que se desagrupe toodo en vez de separarse en grupos originales porque tiene mas sentido en nuestra opinion
		shapeGroups.add(aux);
		selectedFigures.clear();
	}

	private void ungroupButtonAction() {
		if (selectedFigures.isEmpty() || shapeGroups.isEmpty()) {
			return;
		}
		shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains));
		selectedFigures.clear();
	}

	private void updateCheckBoxesState() {
		if (!selectedFigures.isEmpty()) {
			checkBoxState.resetState();

			boolean mixed = false;

			for (FrontFigure<? extends Figure> figure : selectedFigures) {
				for(CheckBox checkBox : statusMap.keySet()) {
					checkCustomState(checkBox, figure, statusMap.get(checkBox).apply(selectedFigures.iterator().next()), mixed);
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

	public void checkCustomState(CheckBox checkBox, FrontFigure<? extends Figure> figure, boolean status, boolean mixed){
		if (statusMap.get(checkBox).apply(figure) != status) {
			mixed = true;
			checkBox.allowIndeterminateProperty();
			checkBox.setIndeterminate(true);
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

	private void clearSelectedFigures(ToggleButton[] toolsArr) {
		for (ToggleButton toggleButton : toolsArr) {
			toggleButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				if (toggleButton.isSelected()) {
					selectedFigures.clear();
					redrawCanvas();
				}
			});
		}
	}


}
