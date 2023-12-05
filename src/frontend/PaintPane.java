package frontend;

import backend.CanvasState;
import backend.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;



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
	private final CheckBox checkBox1 = new CheckBox("Sombra");
	private final CheckBox checkBox2 = new CheckBox("Gradiente");
	private final CheckBox checkBox3 = new CheckBox("Biselado");

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
		CheckBox[] effects = {checkBox1, checkBox2, checkBox3};
		effect.getChildren().addAll(label1, checkBox1, checkBox2, checkBox3);
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
		});

		groupButton.setOnAction(event -> {
			if (selectedFigures.size() <= 1) {
				return;
            }
			ShapeGroup aux = new ShapeGroup();
			aux.addAll(selectedFigures);
			shapeGroups.add(aux);
			selectedFigures.clear();
			redrawCanvas();
		});


		ungroupButton.setOnAction(event -> {
			if (selectedFigures.isEmpty() || shapeGroups.isEmpty()) {
				return;
			}
			Iterator<ShapeGroup> auxIt = shapeGroups.iterator();
			while (auxIt.hasNext()) {
				if (selectedFigures.containsAll(auxIt.next())) { //muy poco eficiente, pero anda. ver q onda
					auxIt.remove();
				}
			}
			selectedFigures.clear();
			redrawCanvas();
		});

		//sombra
		checkBox1.setOnAction(event -> {
		if(!selectedFigures.isEmpty()){
			for (FrontFigure<? extends Figure> figure : selectedFigures) {
				if(checkBox1.isSelected()) {
					applyShadow(figure);
				}
				else{
					removeShadow(figure);
				}
			}
			redrawCanvas();
		}
		});


		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
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
				newFigureAux = new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, CanvasState.DEFAULT_FILL_COLOR);
//				figureColorMap.put(newFigure, Color.color(0,0,0,0));
//				canvasState.addFigure(newFigure);
				for (FrontFigure<? extends Figure> figure : canvasState) {
					if (figure.getFigure().belongsInRectangle(new Rectangle(startPoint, endPoint))) {
						for (ShapeGroup group : shapeGroups) {
							if (group.contains(figure)) {
								selectedFigures.addAll(group); // ver posibilidad de que sea set selectedFigures?
								selectedFigures.remove(figure); //boca boca boca pero paja arreglenlo si quieren ni se si necesario
							}
						}
						selectedFigures.add(figure);
						//System.out.println(figure);
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
						for (ShapeGroup group : shapeGroups) {
							if (group.contains(figure)) {
								selectedFigures.clear();
								selectedFigures.addAll(group);
								groupFound = true;
								break;
							}
						}
						if (!groupFound) {
							selectedFigures.clear(); //esto hace que no se seleccionen 2 si hay una arriba de la otra
							selectedFigures.add(figure);
							//selectedFigure = figure;
							label.append(figure.toString());
						}
						found = true;
					}
				}
				if (found) {  //habria que adaptar lo de label pero paja
					statusPane.updateStatus(label.toString());
				} else if (startPoint != null && startPoint.equals(eventPoint)) {
					selectedFigures.clear();
					statusPane.updateStatus("Ninguna figura encontrada");
				}else {
					//selectedFigure = null;
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

		deleteButton.setOnAction(event -> {


			if (!selectedFigures.isEmpty()) {
				//evito concurrent modification exception asi
				//					ShapeGroup auxGroup = auxIt.next();
				//					for (FrontFigure<? extends Figure> figure : selectedFigures) {   //ver esta posibilidad mejor??
				//						if (auxGroup.contains(figure)) {
				//							auxIt.remove();
				//						}
				//					}
				//muy poco eficiente, pero anda. ver q onda
				shapeGroups.removeIf(frontFigures -> selectedFigures.containsAll(frontFigures));
				canvasState.removeAll(selectedFigures);
				selectedFigures.clear();
				//selectedFigure = null;
				redrawCanvas();
			}
		});

		turnRightButton.setOnAction(event -> {
			if(!selectedFigures.isEmpty()){
				for (FrontFigure<? extends Figure> figure : selectedFigures) {
					figure.getFigure().rotate();
				}
				redrawCanvas();
			}

		});

		flipVerticalButton.setOnAction(event -> {
			if(!selectedFigures.isEmpty()){
				for (FrontFigure<? extends Figure> figure : selectedFigures) {
					figure.getFigure().flipVertically();
				}
				redrawCanvas();
			}
		});

		flipHorizontalButton.setOnAction(event -> {
			if(!selectedFigures.isEmpty()){
				for (FrontFigure<? extends Figure> figure : selectedFigures) {
					figure.getFigure().flipHorizontally();
				}
				redrawCanvas();
			}
		});

		scaleButton.setOnAction(event -> {
			if(!selectedFigures.isEmpty()){
				for (FrontFigure<? extends Figure> figure : selectedFigures) {
					figure.getFigure().scale();
				}
				redrawCanvas();
			}
		});

		descaleButton.setOnAction(event -> {
			if(!selectedFigures.isEmpty()){
				for (FrontFigure<? extends Figure> figure : selectedFigures) {
					figure.getFigure().deScale();
				}
				redrawCanvas();
			}
		});

		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		//System.out.println(selectedFigures.size() + "pito");
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(FrontFigure<? extends Figure> figure : canvasState) {
			if(selectedFigures.contains(figure)){
				gc.setStroke(Color.RED);
			}else {
				gc.setStroke(CanvasState.LINE_COLOR);
			}
			gc.setFill(figure.getColor());
			figure.create();
		}
	}

	private boolean figureBelongs(FrontFigure<? extends Figure> figure, Point eventPoint) {
		return figure.getFigure().belongs(eventPoint);
	}
		
}
