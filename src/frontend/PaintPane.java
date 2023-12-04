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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	List<FrontFigure<? extends Figure>> selectedFigures = new ArrayList<>();

	// StatusBar
	private StatusPane statusPane;

	// Colores de relleno de cada figura
	private Map<FrontFigure<? extends Figure>, Color> figureColorMap = new HashMap<>();

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

		Map<ToggleButton, Bifunction<Point, Point, FrontFigure> buttonMap = new HashMap<>();
		buttonMap.put(figuresArr[0], (startPoint, endPoint) -> new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, CanvasState.DEFAULT_FILL_COLOR));
		buttonMap.put(figuresArr[1], (startPoint, endPoint) -> new FrontEllipse<>(new Circle(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, CanvasState.DEFAULT_FILL_COLOR);
		buttonMap.put(figuresArr[2], (startPoint, endPoint) -> new FrontRectangle<>(new Square(startPoint, Math.abs(endPoint.getX() - startPoint.getX())), gc, CanvasState.DEFAULT_FILL_COLOR);
		buttonMap.put(figuresArr[3], (startPoint, endPoint) -> new FrontEllipse<>(new Ellipse(new Point(Math.abs(endPoint.x + startPoint.x) / 2, (Math.abs((endPoint.y + startPoint.y)) / 2)), Math.abs(endPoint.x - startPoint.x), Math.abs(endPoint.y - startPoint.y)), gc, CanvasState.DEFAULT_FILL_COLOR);

		
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

		canvas.setOnMouseReleased(event -> {
			Point endPoint = new Point(event.getX(), event.getY());
			if(startPoint == null) {
				return ;
			}
			if(endPoint.getX() < startPoint.getX() || endPoint.getY() < startPoint.getY()) {
				return ;
			}
			FrontFigure<? extends Figure> newFigure = null;

			for (ToggleButton button : figuresArr) {
				if(button.isSelected()){
					figureFunction = buttonMap.get(button);
					newFigure = figureFunction.apply(startPoint, endPoint);
					canvasState.addFigure(newFigure);
					// redraw canvas?
				}
			}
			
			if(selectionButton.isSelected() && !startPoint.equals(endPoint)) {
				newFigure = new FrontRectangle<>(new Rectangle(startPoint, endPoint), gc, CanvasState.DEFAULT_FILL_COLOR);
//				figureColorMap.put(newFigure, Color.color(0,0,0,0));
//				canvasState.addFigure(newFigure);
				for (FrontFigure<? extends Figure> figure : canvasState) {
					if (figure.getFigure().belongsInRectangle(new Rectangle(startPoint, endPoint))) {
						selectedFigures.add(figure);
						System.out.println(figure);
					}
				}
				startPoint = null;
				redrawCanvas();
				return;
			} else {
				return ;
			}
			figureColorMap.put(newFigure, fillColorPicker.getValue());
			canvasState.add(newFigure);
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
				StringBuilder label = new StringBuilder("Se seleccionó: ");
				for (FrontFigure<? extends Figure> figure : canvasState) {
					if(figureBelongs(figure, eventPoint)) {
						found = true;
						selectedFigures.clear(); //esto hace que no se seleccionen 2 si hay una arriba de la otra
						selectedFigures.add(figure);
						//selectedFigure = figure;
						label.append(figure.toString());
					}
				}
				if (found) {
					statusPane.updateStatus(label.toString());
				} else {
					selectedFigures.clear();
					//selectedFigure = null;
					statusPane.updateStatus("Ninguna figura encontrada");
				}
				redrawCanvas();
			}
		});

		canvas.setOnMouseDragged(event -> {
			if(selectionButton.isSelected()) {
				Point eventPoint = new Point(event.getX(), event.getY());
				double diffX = (eventPoint.getX() - startPoint.getX()) / 100;
				double diffY = (eventPoint.getY() - startPoint.getY()) / 100;
				//selectedFigure.getFigure().move(diffX, diffY);
				if (!selectedFigures.isEmpty()) {
					for (FrontFigure<? extends Figure> figure : selectedFigures) {
						figure.getFigure().move(diffX, diffY);
					}
				}
				redrawCanvas();
			}
		});

		deleteButton.setOnAction(event -> {
			if (!selectedFigures.isEmpty()) {
				canvasState.removeAll(selectedFigures);
				selectedFigures.clear();
				//selectedFigure = null;
				redrawCanvas();
			}
		});

		setLeft(buttonsBox);
		setRight(canvas);
	}

	private void redrawCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for(FrontFigure<? extends Figure> figure : canvasState) {
			if(selectedFigures.contains(figure)){
				gc.setStroke(Color.RED);
			}else {
				gc.setStroke(CanvasState.LINE_COLOR);
			}
			gc.setFill(figureColorMap.get(figure));
			figure.create();
		}
	}

	private boolean figureBelongs(FrontFigure<? extends Figure> figure, Point eventPoint) {
		return figure.getFigure().belongs(eventPoint);
	}
		
}
