package frontend;

import backend.*;
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
import java.util.function.Function;

public class PaintPane extends BorderPane {

	// BackEnd
	private final List<FrontFigure<? extends Figure>> canvasState;

	// Canvas y relacionados
	private final Canvas canvas = new Canvas(800, 600);
	private final GraphicsContext gc = canvas.getGraphicsContext2D();

	private final Color DEFAULT_FILL_COLOR = Color.YELLOW;
	private final Color LINE_COLOR = Color.BLACK;
	private final Color SELECTED_LINE_COLOR = Color.RED;
	private boolean mixed = false;


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
	private  final ColorPicker fillColorPicker = new ColorPicker(DEFAULT_FILL_COLOR);

	private boolean wasDragged = false;

	// Dibujar una figura
	private Point startPoint;

	private final Set<FrontFigure<? extends Figure>> selectedFigures = new HashSet<>(); //asi no se puede agregar el mismo puntero dos veces, sirve para seleccion multiple comboinado con group
	private final Set<FrontFigure<? extends Figure>> maintainFigures = new HashSet<>();

	private final List<List<FrontFigure<? extends Figure>>> shapeGroups = new ArrayList<>();

	FrontFigure<? extends Figure> transitionFigure;

	// StatusBar
	private final StatusPane statusPane;

	private final CheckBoxState checkBoxState = new CheckBoxState();

	Map<CheckBox, Function< FrontFigure<? extends Figure>, Boolean>> statusMap = new HashMap<>() {{
		put(checkBoxShadow, FrontFigure::getShadowStatus);
		put(checkBoxGradient, FrontFigure::getGradientStatus);
		put(checkBoxBiselado, FrontFigure::getBeveledStatus);
	}};


	private Point startPointAux;

	// Colores de relleno de cada figura
	public PaintPane(List<FrontFigure<? extends Figure>> canvasState, StatusPane statusPane) {
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

		List<ToggleButton> figureList = new ArrayList<>(Arrays.asList(figuresArr));

		//Mapa que asocia a los botones de crear figuras con su funcion para crear la figura
		Map<ToggleButton, BiFunction<Point, Point, FrontFigure<? extends Figure>>> buttonMap = new HashMap<>() {{
			put(figuresArr[0], (startPoint, endPoint) -> new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, fillColorPicker.getValue()));
			put(figuresArr[1], (startPoint, endPoint) -> new FrontEllipse<>(new Circle(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, fillColorPicker.getValue()));
			put(figuresArr[2], (startPoint, endPoint) -> new FrontRectangle<>(new Square(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, fillColorPicker.getValue()));
			put(figuresArr[3], (startPoint, endPoint) -> new FrontEllipse<>(new Ellipse(new Point(Math.abs(endPoint.getX() + startPoint.getX()) / 2, (Math.abs((endPoint.getY() + startPoint.getY())) / 2)), Math.abs(endPoint.getX() - startPoint.getX()), Math.abs(endPoint.getY() - startPoint.getY())), gc, fillColorPicker.getValue()));
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

		//Presionar el mouse,
		canvas.setOnMousePressed(event -> {
			startPoint = new Point(event.getX(), event.getY());
			startPointAux = new Point(event.getX(), event.getY());
			for (FrontFigure<? extends Figure> figure : selectedFigures) {
				if (figureBelongs(figure, new Point(event.getX(), event.getY()))) {
					maintainFigures.addAll(selectedFigures);
					return;
				}
			}
			selectedFigures.clear();
		});

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if (startPointAux == null) {
				return;
			}
			FrontFigure<? extends Figure> newFigureAux = null;

			boolean found = false;

			StringBuilder label = new StringBuilder("Se seleccionó: ");

			//agregamos el recorrido por el mapa de los botones y aplicamos la respectiva funcion para ahorrarnos una concatenacion de if-else
			for (ToggleButton button : figuresArr) {
				if (button.isSelected() && !startPointAux.equals(endPoint)) {
					found = true;
					BiFunction<Point, Point, FrontFigure<? extends Figure>> figureFunction = buttonMap.get(button);
					newFigureAux = figureFunction.apply(startPointAux, endPoint);
					selectedFigures.remove(newFigureAux);

				}
			}

			if (selectionButton.isSelected() && !startPointAux.equals(endPoint)) {
				if (wasDragged) {
					Optional<FrontFigure<? extends Figure>> aux = canvasState.stream().filter(figureAux -> figureBelongs(figureAux, endPoint)).reduce((first, second) -> second);
					FrontFigure<? extends Figure> auxFig = aux.orElse(null);
					if (!maintainFigures.contains(auxFig)) {
						selectedFigures.clear();
					}
					maintainFigures.clear();
					selectedFigures.add(auxFig);
				} else {
					canvasState.stream().filter(figure -> figure.getFigure().belongsInRectangle(new Rectangle(startPointAux, endPoint))).forEach(figure -> {
						shapeGroups.stream().filter(group -> group.contains(figure)).forEach(selectedFigures::addAll);
						selectedFigures.add(figure);
					});
				}
				selectedFigures.forEach(figureAux -> label.append(figureAux).append(", "));
				label.delete(label.length() - 2, label.length());
				statusPane.updateStatus(label.toString());
				wasDragged = false;
				redrawCanvas();
				return;
			}
			if (!found) {
				return;
			}
			canvasState.add(newFigureAux);
			redrawCanvas();

		});


		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			StringBuilder label = new StringBuilder();

			if (!selectedFigures.isEmpty()) {
				label.append("Se seleccionó: ");
				updateStatusLabel(label, true);
			} else {
				Optional<FrontFigure<? extends Figure>> selectedFigure = canvasState.stream().filter(figure -> figureBelongs(figure, eventPoint)).reduce((first, second) -> second);
				selectedFigure.ifPresent(label::append);
				if (selectedFigure.isEmpty()) {
					label.append(eventPoint);
				}
			}

			statusPane.updateStatus(label.toString());
		});



		canvas.setOnMouseClicked(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			if (selectionButton.isSelected()  && startPointAux != null  && startPointAux.equals(eventPoint)) {
				boolean found = false;
				boolean groupFound;
				StringBuilder label = new StringBuilder("Se seleccionó: ");

				for (FrontFigure<? extends Figure> figure : canvasState) {
					if (figureBelongs(figure, eventPoint)) {
						groupFound = false;
						//manejamos las funcionalidades del grupo
						for (Collection<FrontFigure<? extends Figure>> group : shapeGroups) {
							if (group.contains(figure)) {
								selectedFigures.clear();
								selectedFigures.addAll(group);
								groupFound = true;
								break;
							}
						}
						if (!groupFound) {
							if (startPointAux.equals(eventPoint)) {
								selectedFigures.clear();
							}
							selectedFigures.add(figure);
						}
						found = true;
					}
				}
				updateStatusLabel(label, found);
				redrawCanvas();
			}

		});


		canvas.setOnMouseDragged(event -> {
			wasDragged = false;
			Point eventPoint = new Point(event.getX(), event.getY());
			if (selectionButton.isSelected()) {
				double diffX = (eventPoint.getX() - startPoint.getX());
				double diffY = (eventPoint.getY() - startPoint.getY());
				if (!selectedFigures.isEmpty()) {
					wasDragged = true;
					selectedFigures.forEach(figure -> figure.getFigure().move(diffX, diffY));
					startPoint.movePoint(diffX, diffY);
				}
				redrawCanvas();
			}
			//Para poder ver que figura se esta creando y que se selecciona con el invisible rectangle
			figureList.stream().filter(ToggleButton::isSelected).forEach(button -> {
				BiFunction<Point, Point, FrontFigure<? extends Figure>> figureFunction = buttonMap.get(button);
				transitionFigure = figureFunction.apply(startPoint, eventPoint);
				transitionFigure.setInvisibleRectangle(button == selectionButton);
				canvasState.add(transitionFigure);
				redrawCanvas();
				canvasState.remove(transitionFigure);
			});

		});



		//manejamos los eventos de todos los botones
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

		//llama al redrawCanvas al clickear algun botton para no tener que repetir el llamado en todos lados
		buttonsBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());
		effect.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());

		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvasState.forEach(figure -> {
			setGcStroke(figure);
			figure.createShadow(figure.shadowStatus);
			gc.setFill(figure.getColor());
			figure.create(0);
		});
		updateCheckBoxesState();
	}

	private void setGcStroke(FrontFigure<? extends Figure> figure){
		if (!selectedFigures.isEmpty() && (selectedFigures.contains(figure))) {
			gc.setStroke(SELECTED_LINE_COLOR);
			return;
		}
		gc.setStroke(LINE_COLOR);
	}

	private boolean figureBelongs(FrontFigure<? extends Figure> figure, Point eventPoint) {
		return figure.getFigure().belongs(eventPoint);
	}

	private void deleteButtonAction() {
		if(selectedFigures.isEmpty()){
			return;
		}
		shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains));
		canvasState.removeAll(selectedFigures);
		selectedFigures.clear();
	}

	//modularizacion del codigo para que quede mas prolijo el codigo

	private void groupButtonAction() {
		if (selectedFigures.size() <= 1) {
			return;
		}
		List<FrontFigure<? extends Figure>> aux = new ArrayList<>(selectedFigures);
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

	//manejo de las checkboxes y el estado indeterminado
	private void updateCheckBoxesState() {

		resetCheckBoxes();

		if(selectedFigures.isEmpty()){
			return;
		}


		checkBoxState.resetState();
		mixed = false;

		for (FrontFigure<? extends Figure> figure : selectedFigures) {
			FrontFigure<? extends Figure > auxFig = selectedFigures.iterator().next();
			statusMap.keySet().forEach(checkBox -> checkCustomState(checkBox, figure, statusMap.get(checkBox).apply(auxFig)));

			if (!mixed) {
				checkBoxState.updateState(figure);
			}

		}


		checkBoxShadow.setSelected(checkBoxState.getCommonShadowStatus());
		checkBoxGradient.setSelected(checkBoxState.getCommonGradientStatus());
		checkBoxBiselado.setSelected(checkBoxState.getCommonBeveledStatus());
	}

	private void checkCustomState(CheckBox checkBox, FrontFigure<? extends Figure> figure, boolean status){
		if (statusMap.get(checkBox).apply(figure) != status) {
			checkBox.allowIndeterminateProperty();
			checkBox.setIndeterminate(true);
			mixed = true;
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

	//funciona para deseleccionar las figuras al clickear uno de los toggle buttons ya que al crear una nueva figura no se debe tener seleccionada una
	private void clearSelectedFigures(ToggleButton[] toolsArr) {
		Arrays.stream(toolsArr).forEach(toggleButton -> toggleButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (toggleButton.isSelected()) {
				selectedFigures.clear();
				redrawCanvas();
			}
		}));


	}

	private void updateStatusLabel(StringBuilder label, boolean found) {
		selectedFigures.forEach(figureAux -> label.append(figureAux).append(", "));
		label.delete(label.length() - 2, label.length());
		if (!found) {
			selectedFigures.clear();
			statusPane.updateStatus("Ninguna figura encontrada");
		} else {
			statusPane.updateStatus(label.toString());
		}
	}






}
