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

public class PaintPane extends BorderPane {

	// BackEnd
	private CanvasState canvasState;

	// Canvas y relacionados
	private Canvas canvas = new Canvas(800, 600);
	private GraphicsContext gc = canvas.getGraphicsContext2D();

	private final Color lineColor = Color.BLACK;
	private final Color defaultFillColor = Color.YELLOW;

	// Botones Barra Izquierda
	private ToggleButton selectionButton = new ToggleButton("Seleccionar");
	private ToggleButton rectangleButton = new ToggleButton("Rectángulo");
	private ToggleButton circleButton = new ToggleButton("Círculo");
	private ToggleButton squareButton = new ToggleButton("Cuadrado");
	private ToggleButton ellipseButton = new ToggleButton("Elipse");
	private ToggleButton deleteButton = new ToggleButton("Borrar");
	private Button groupButton = new Button("Agrupar");
	private Button ungroupButton = new Button("Desagrupar");
	private Button turnRightButton = new Button("Girar D");
	private Button flipHorizontalButton = new Button("Voltear H");
	private Button flipVerticalButton = new Button("Voltear V");
	private Button scaleButton = new Button("Escalar +");
	private Button descaleButton = new Button("Escalar -");
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
	private StatusPane statusPane;

	// Colores de relleno de cada figura
	public PaintPane(CanvasState canvasState, StatusPane statusPane) {
		this.canvasState = canvasState;
		this.statusPane = statusPane;

		Button[] functionalities = {groupButton , ungroupButton, turnRightButton, flipHorizontalButton, flipVerticalButton, scaleButton, descaleButton};
		for(Button functionality : functionalities){
			functionality.setMinWidth(90);
			functionality.setCursor(Cursor.HAND);
		}

		ToggleButton[] toolsArr = {selectionButton, rectangleButton, circleButton, squareButton, ellipseButton, deleteButton};
		ToggleGroup tools = new ToggleGroup();
		for (ToggleButton tool : toolsArr) {
			tool.setMinWidth(90);
			tool.setToggleGroup(tools);
			tool.setCursor(Cursor.HAND);
		}

		ToggleButton[] figuresArr = {rectangleButton, circleButton, squareButton, ellipseButton};

		Map<ToggleButton, BiFunction<Point, Point, FrontFigure<? extends Figure>>> buttonMap = new HashMap<>(){{
			put(figuresArr[0], (startPoint, endPoint) -> new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, fillColorPicker.getValue()));
			put(figuresArr[1], (startPoint, endPoint) -> new FrontEllipse<>(new Circle(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, fillColorPicker.getValue()));
			put(figuresArr[2], (startPoint, endPoint) -> new FrontRectangle<>(new Square(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc,fillColorPicker.getValue()));
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
		for(CheckBox checkbox : effects){
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
			if(startPoint == null || endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}
			FrontFigure<? extends Figure> newFigureAux = null;

			boolean found = false;

			for (ToggleButton button : figuresArr) {
				if(button.isSelected()){
					found = true;
					BiFunction<Point, Point, FrontFigure<? extends Figure>> figureFunction = buttonMap.get(button);
					newFigureAux = figureFunction.apply(startPoint, endPoint);
					// redraw canvas?
				}
			}

			if(selectionButton.isSelected() && !startPoint.equals(endPoint)) {
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

				checkBoxShadow.setSelected(selectedFigures.isEmpty() || (selectedFigures.iterator().next().shadowStatus && selectedFigures.size() == 1));
				Iterator<FrontFigure<? extends Figure>> auxIt = selectedFigures.iterator();
				while (auxIt.hasNext()) {
					boolean status = auxIt.next().shadowStatus;
					if (auxIt.hasNext() && auxIt.next().shadowStatus != status) {
						checkBoxShadow.allowIndeterminateProperty();
						checkBoxShadow.setIndeterminate(true);
						break;
					}
				}
				redrawCanvas();
				return;
			}
			if (!found) {
				checkBoxShadow.setAllowIndeterminate(false);
				checkBoxShadow.setIndeterminate(false);
				return;
			}
			canvasState.add(newFigureAux);
			startPoint = null;
			redrawCanvas();
		});

		canvas.setOnMouseMoved(event -> {
			Point eventPoint = new Point(event.getX(), event.getY());
			boolean found = false;
			StringBuilder label = new StringBuilder();
			for(FrontFigure<? extends Figure> figure : canvasState) {
				if(figureBelongs(figure, eventPoint)) {
					found = true;
					label.append(figure.toString());
				}
			}
			if(found) {
				statusPane.updateStatus(label.toString());
			} else {
				statusPane.updateStatus(eventPoint.toString());
			}
		});

		canvas.setOnMouseClicked(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				boolean found = false;
				boolean groupFound = false;
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (FrontFigure<? extends Figure> figure : canvasState) {

					if(figureBelongs(figure, eventPoint)) {
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
							selectedFigures.clear(); //esto hace que no se seleccionen 2 si hay una arriba de la otra
							selectedFigures.add(figure);
							label.append(figure.toString());
						}
						found = true;
					}
				}
				if (found) {
					checkBoxShadow.setSelected(selectedFigures.isEmpty() || (selectedFigures.iterator().next().shadowStatus && selectedFigures.size() == 1));
					Iterator<FrontFigure<? extends Figure>> auxIt = selectedFigures.iterator();

					while (auxIt.hasNext()) {
						boolean status = auxIt.next().shadowStatus;
						if (auxIt.hasNext() && auxIt.next().shadowStatus != status) {
							checkBoxShadow.allowIndeterminateProperty();
							checkBoxShadow.setIndeterminate(true);
							break;
						}
					}

					statusPane.updateStatus(label.toString());
				} else if (startPoint != null && startPoint.equals(eventPoint)) {
					selectedFigures.clear();
					checkBoxShadow.setSelected(false);
					checkBoxShadow.setAllowIndeterminate(false);
					statusPane.updateStatus("Ninguna figura encontrada");
				}else {
					checkBoxShadow.setSelected(false);
					checkBoxShadow.setAllowIndeterminate(false);
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			}
		});

		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
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

		checkBoxBiselado.setOnAction(event -> selectedFigures.forEach(figure ->{
			if(checkBoxBiselado.isSelected()){
				figure.applyBeveled();
			}
			else{
				figure.removeBeveled();
			}

		}));

		checkBoxShadow.setOnAction(event -> selectedFigures.forEach(figure -> {
			if(checkBoxShadow.isSelected()) {
				figure.applyShadow();
			}
			else{
				figure.removeShadow();
			}
		}));

		checkBoxGradient.setOnAction(event -> selectedFigures.forEach(figure -> {
			if(checkBoxGradient.isSelected()) {
				figure.applyGradient();
			}
			else{
				figure.removeGradient();
			}
		}));

		groupButton.setOnAction(event -> groupButtonAction());

		ungroupButton.setOnAction(event -> ungroupButtonAction());

		turnRightButton.setOnAction(event -> {selectedFigures.forEach(figure -> figure.getFigure().rotate());});

		flipVerticalButton.setOnAction(event -> {selectedFigures.forEach(figure -> figure.getFigure().flipVertically());});

		flipHorizontalButton.setOnAction(event -> {selectedFigures.forEach(figure -> figure.getFigure().flipHorizontally());});

		scaleButton.setOnAction(event -> {selectedFigures.forEach(figure -> figure.getFigure().scale());});

		descaleButton.setOnAction(event -> {selectedFigures.forEach(figure -> figure.getFigure().descale());});

		buttonsBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());
		effect.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> redrawCanvas());

		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(FrontFigure<? extends Figure> figure : canvasState) {
			if(selectedFigures.contains(figure) && !figure.isShadow){
				gc.setStroke(Color.RED);
			}else {
				gc.setStroke(CanvasState.LINE_COLOR);
			}
			gc.setFill(figure.getColor());
			if (figure.shadowStatus) {
				figure.createShadow();
			}
			figure.create(0);


		}
	}

	private boolean figureBelongs(FrontFigure<? extends Figure> figure, Point eventPoint) {
		return figure.getFigure().belongs(eventPoint);
	}

	private void deleteButtonAction(){
		if (!selectedFigures.isEmpty()) {
			shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains));
			canvasState.removeAll(selectedFigures);
			selectedFigures.clear();
			redrawCanvas();
		}
	}

	private void groupButtonAction(){
		if (selectedFigures.size() <= 1) {
			return;
		}
		ShapeGroup aux = new ShapeGroup();
		aux.addAll(selectedFigures);
		shapeGroups.add(aux);
		selectedFigures.clear();
		redrawCanvas();
	}

	private void ungroupButtonAction(){
		if (selectedFigures.isEmpty() || shapeGroups.isEmpty()) {
			return;
		}
		shapeGroups.removeIf(shapeGroup -> shapeGroup.stream().anyMatch(selectedFigures::contains));
		selectedFigures.clear();
		redrawCanvas();
	}

}
