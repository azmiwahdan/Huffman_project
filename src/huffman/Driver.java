package huffman;
//Azmi Wahdan

import java.util.ArrayList;
import java.util.PriorityQueue;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Driver extends Application {
	private static int headLength = 0;
	private static int actualfileLength;
	private static double rate = 0;
	private static String s = "";
	private static int frq[];
	private static byte[] bytes;
	private static int numOfChar = 0;
	private static PriorityQueue<HuffmanNode> heap;
	private static HuffmanNode huffmanTreeRoot;
	private static ArrayList<Node> huffmanCodes;
	private static File inputUnCompressionFile, inputDeCompressionFile;
	private static String typeFile = "";
	private static String headerLength = "";
	private static String dataLength = "";

	@Override
	public void start(Stage stage) throws Exception {

		stage.getIcons().add(new Image("file:bzu.jpg"));
		stage.setTitle("Project 2");
		//stage.resizableProperty().setValue(Boolean.FALSE);

		Label azmiCompressor = new Label("Azmi Compressor");
		azmiCompressor.setFont(new Font("Algerian", 34));
		azmiCompressor.setTextFill(Color.WHITE);
		azmiCompressor.setAlignment(Pos.TOP_CENTER);

		Label compressor = new Label("Compressor");
		compressor.setFont(new Font("Algerian", 24));
		compressor.setTextFill(Color.WHITE);

		Label decompressor = new Label("Decompressor");
		decompressor.setFont(new Font("Algerian", 24));
		decompressor.setTextFill(Color.WHITE);

		Button close = new Button("Exit");
		close.setTextFill(Color.RED);
		close.setTooltip(new Tooltip("exit from application"));
		close.setPrefWidth(100);
		close.setFont(new Font("Copperplate Gothic Bold", 12));

		Button compFilChooser = new Button("Enter to select");
		compFilChooser.setTextFill(Color.BLACK);
		compFilChooser.setFont(new Font("Courier New", 15));

		Button deCompFilChooser = new Button("Enter to select");
		deCompFilChooser.setFont(new Font("Courier New", 15));

		TextArea ta = new TextArea("");
		//ta.setPrefWidth(140);
		ta.setPrefHeight(400);
		ta.setFont(new Font("Lucida Sans Unicode", 13));
		

		VBox compVBox = new VBox(20, compressor, compFilChooser);
		VBox deCompVBox = new VBox(20, decompressor, deCompFilChooser);

		HBox hbox1 = new HBox(70, compVBox, deCompVBox);

		VBox primaryVBox = new VBox(60, azmiCompressor, hbox1, ta, close);
		primaryVBox.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		close.setOnAction(e -> {
			JOptionPane.showMessageDialog(null, "Good-Luck-@azmi.com");
			System.exit(0);
		});

		// set Alignment
		primaryVBox.setAlignment(Pos.CENTER);
		Image im = new Image("file:image.jpg");
		BackgroundImage backgroundimage = new BackgroundImage(im, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		Background background = new Background(backgroundimage);
		primaryVBox.setBackground(background);

		hbox1.setAlignment(Pos.CENTER);

		deCompVBox.setAlignment(Pos.CENTER);

		compVBox.setAlignment(Pos.CENTER);

		// create a File chooser
		FileChooser compChooser = new FileChooser();
		compChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"),
				new ExtensionFilter("Text Files", "*.txt"), new ExtensionFilter("Java Files", "*.java"),
				new ExtensionFilter("Web Files", "*.html", "*.css", "*.js", "*.php"),
				new ExtensionFilter("Image files", "*.png", "*.jpg"), new ExtensionFilter("Word files", "*.docx"),
				new ExtensionFilter("Pdf files", "*.pdf")

		);

		// create an Event Handler
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {

				// get the file selected
				inputUnCompressionFile = compChooser.showOpenDialog(stage);// select file to compress

				if (inputUnCompressionFile != null) {

					try {
						comp(stage, compVBox, deCompVBox, ta);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};

		compFilChooser.setOnAction(event);

		// create a File chooser
		FileChooser decompFileChooser = new FileChooser();
		decompFileChooser.getExtensionFilters().add(new ExtensionFilter("Huffman Files", "*.huff"));

		// create an Event Handler
		EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {

				// get the file selected
				inputDeCompressionFile = decompFileChooser.showOpenDialog(stage);// selected file to decompress..

				if (inputDeCompressionFile != null) {

					ta.setText("Decompression \n" + inputDeCompressionFile.getAbsolutePath() + "  selected");

					try {
						deComp(stage, deCompVBox, compVBox);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};

		deCompFilChooser.setOnAction(event2);

		StackPane stp = new StackPane(primaryVBox);

		Scene scene = new Scene(stp, 900, 850);
		stage.setScene(scene);
		stage.show();

	}

	private void deComp(Stage stage4, VBox vb, VBox vb2) throws IOException {

		if (vb.getChildren().size() > 2) {
			vb.getChildren().remove(2);
			vb2.getChildren().remove(2);
		}

		Button button = new Button("Save a Uncomparsion file");
		button.setFont(new Font("Arial", 12));

		Button bu = new Button("Save a Uncomparsion file");
		bu.setVisible(false);
		bu.setFont(new Font("Arial", 12));

		vb2.getChildren().add(bu);

		vb.getChildren().add(button);
		BinaryStreamIn binaryRead = new BinaryStreamIn(inputDeCompressionFile);

		button.setOnAction(e -> {

			splitHeader(binaryRead);

			FileChooser fileChooser = new FileChooser();

			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
					typeFile + " files (*." + typeFile + ")", "*." + typeFile);
			fileChooser.getExtensionFilters().add(extFilter);

			// Show save file dialog
			File file = fileChooser.showSaveDialog(stage4);

			if (file != null) {
				FileOutputStream out = null;

				try {
					out = new FileOutputStream(file);
					BinaryStreamOut bout = new BinaryStreamOut(out);

					Long datalength = Long.parseLong(dataLength);

					int length = Integer.parseInt(headerLength);

					StringBuilder header = new StringBuilder("");

					for (int i = 0; i < length; i++) {
						boolean b = binaryRead.readBoolean();
						if (b)
							header.append("1");
						else
							header.append("0");

					}

					s = header.toString();

					HuffmanNode root = decodeHeader();

					for (int i = 0; i < datalength; i++) {
						HuffmanNode node = root;
						while (!node.isLeaf()) {
							boolean bit = binaryRead.readBoolean();
							if (bit)
								node = node.getRight();
							else
								node = node.getLeft();
						}
						bout.write(node.getVal());
					}
					bout.close();

				} catch (FileNotFoundException e1) {
					System.out.println("File not found" + e1);
				} finally {
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException ioe) {
						System.out.println("Error while closing stream: " + ioe);
					}

				}
			}

		});

	}

	private void splitHeader(BinaryStreamIn binaryRead) {

		boolean n = true;

		while (n) {
			char c = (char) binaryRead.readByte();
			if (c == ':')
				n = false;
			else
				typeFile += c;
		}
		n = true;
		while (n) {
			char c = (char) binaryRead.readByte();
			if (c == ':')
				n = false;
			else
				headerLength += c;
		}

		n = true;
		while (n) {
			char c = (char) binaryRead.readByte();
			if (c == ':')
				n = false;
			else
				dataLength += c;
		}

	}

	private static HuffmanNode decodeHeader() {

		String st = peek(1);
		boolean n = st.equals("");
		if (n)
			return null;
		boolean isLeaf = st.equals("1");

		if (isLeaf) {

			byte b = (byte) ((Integer.parseInt(peek(8), 2)) - 128);
			return new HuffmanNode(b, -1, null, null, true);
		}

		return new HuffmanNode(decodeHeader(), decodeHeader());

	}

	private static String getByteBinaryString(byte b) {

		StringBuilder sb = new StringBuilder();
		for (int i = 7; i >= 0; --i) {
			sb.append(b >>> i & 1);
		}
		return sb.toString();
	}

	private void comp(Stage stage3, VBox vb, VBox vb2, TextArea ta) throws IOException {

		numOfChar = 0;

		if (vb.getChildren().size() > 2) {
			vb.getChildren().remove(2);
			vb2.getChildren().remove(2);
		}
		Button button = new Button("Save a comparsion file");
		button.setFont(new Font("Arial", 12));

		Button bu = new Button("Save a comparsion file");
		bu.setFont(new Font("Arial", 12));
		bu.setVisible(false);
		vb2.getChildren().add(bu);
		String nameOfFile = inputUnCompressionFile.getName().split("\\.")[1];

		readFile(inputUnCompressionFile);

		vb.getChildren().addAll(button);

//		ta.setText("Compression \n" + inputUnCompressionFile.getAbsolutePath() + "  selected" + "\n"
//				+ "File length :" + inputUnCompressionFile.length());

		ta.setText(" _________________________________________________________________________________________"
				+ "\nInput File information" + "\nFile Path:" + inputUnCompressionFile.getAbsolutePath() + "  selected"
				+ "\nFile length :" + inputUnCompressionFile.length() + "\nNumber of Distinguished Charcter: "
				+ numOfChar
				+ "\n _________________________________________________________________________________________"
				+ "\nCompressed File Information " + "\nFile Head Length : " + headLength + "\n Actual Data Length : "
				+ actualfileLength + "\nCompression Rate : " + rate + "%"
				+ "\n _________________________________________________________________________________________");

		// ta.setText(ta.getText() + "\n# of Distinguished Character : " + numOfChar);

		button.setOnAction(e -> {

			FileChooser fileChooser = new FileChooser();

			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("huff files (*.huff)", "*.huff");
			fileChooser.getExtensionFilters().add(extFilter);

			// Show save file dialog
			File file = fileChooser.showSaveDialog(stage3);

			if (file != null) {

				FileOutputStream outStream = null; // fileoutputstream

				try {

					outStream = new FileOutputStream(file);// 1

					BinaryStreamOut binaryStrem = new BinaryStreamOut(outStream);// BinaryRead

					BitOutputStream bitStream = new BitOutputStream(outStream);// BitStream

					initializePriorityQueue();// build heap
					buildHuffmanTree();//

					getHeaderLength(huffmanTreeRoot);

					bitStream.writeH(new StringBuilder(nameOfFile + ":" + headLength + ":"));

					buildHeader(huffmanTreeRoot, binaryStrem);

					huffmanCodes = addCode(huffmanTreeRoot);// get codes

					writeCompressedData(bitStream, binaryStrem);
					actualfileLength = (int) file.length();

					rate = ((double) (bytes.length - actualfileLength) / bytes.length) * 100;
					String ratio = rate + "";
					if (ratio.length() > 5) {
						ratio = ratio.substring(0, 5);
					}

//					ta.setText(ta.getText() + "\n"
//							+ "**********************************************************************" + "\n"
//							+ "File Head length (in Bit) :" + headLength + "\n" + "Actual data length:" + file.length()
//							+ "\nCompression Rate :" + rate + "%"
//							+ "\n**********************************************************************");

					ta.setText(
							" _________________________________________________________________________________________"
									+ "\nInput File information" + "\nFile Path:"
									+ inputUnCompressionFile.getAbsolutePath() + "  selected" + "\nFile length :"
									+ inputUnCompressionFile.length() + "\nNumber of Distinguished Charcter: "
									+ numOfChar
									+ "\n _________________________________________________________________________________________"
									+ "\nCompressed File Information " + "\nFile Head Length : " + headLength
									+ "\n Actual Data Length : " + actualfileLength + "\nCompression Rate : " + ratio
									+ "%"
									+ "\n _________________________________________________________________________________________");

					ta.setText(ta.getText() + "\n" + "byte  --> frequancy --> Huffman code" + "\n");
					for (int i = 0; i < huffmanCodes.size(); i++) {
						ta.setText(ta.getText() + "\n(" + (char) huffmanCodes.get(i).getVal().getVal() + ") --> "
								+ huffmanCodes.get(i).getVal().getFreq() + " --> " + huffmanCodes.get(i).gethCode()
								+ "\n");
					}

				} catch (FileNotFoundException e1) {
					System.out.println("File not found" + e1);
				} catch (IOException ioe) {
					System.out.println("Exception while writing file " + ioe);
				} finally {
					try {
						if (outStream != null) {
							outStream.close();
						}
					} catch (IOException ioe) {
						System.out.println("Error while closing stream: " + ioe);
					}

				}
			}

		});

	}

	private void writeCompressedData(BitOutputStream bitStream, BinaryStreamOut binaryStrem) throws IOException {

		StringBuilder s = new StringBuilder("");

		for (int i = 0; i < bytes.length; i++)
			for (int j = 0; j < huffmanCodes.size(); j++)
				if ((bytes[i]) == huffmanCodes.get(j).getVal().getVal()) {
					s.append(huffmanCodes.get(j).gethCode());
					break;
				}
		bitStream.writeH(new StringBuilder(bytes.length + ":"));

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '1')
				binaryStrem.write(true);
			else if (s.charAt(i) == '0')
				binaryStrem.write(false);
		}

		binaryStrem.close();
	}

	private static ArrayList<Node> addCode(HuffmanNode root) {
		ArrayList<Node> huffmanCodes = new ArrayList<Node>();
		String s = ("");
		addCode(root, s, huffmanCodes);

		return huffmanCodes;

	}

	private static void addCode(HuffmanNode root, String s, ArrayList<Node> huffmanCodes) {
		if (!(root.isLeaf())) {
			String sl = s + "0";
			String sr = s + "1";
			addCode(root.getLeft(), sl, huffmanCodes);
			addCode(root.getRight(), sr, huffmanCodes);
		} else {
			huffmanCodes.add(new Node(s.toString(), root));
		}
	}

	private static void buildHeader(HuffmanNode huffmanTreeRoot, BinaryStreamOut bos) throws IOException {
		if (huffmanTreeRoot.isLeaf()) {
			bos.write(true);
			String s = getByteBinaryString(huffmanTreeRoot.getVal());
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '1')
					bos.write(true);

				else
					bos.write(false);

			}
			return;

		}

		bos.write(false);
		buildHeader(huffmanTreeRoot.getLeft(), bos);
		buildHeader(huffmanTreeRoot.getRight(), bos);
	}

	private static void getHeaderLength(HuffmanNode huffmanTreeRoot) {
		if (huffmanTreeRoot == null)
			return;
		if (huffmanTreeRoot.isLeaf()) {
			headLength += 9;
			return;
		} else {
			headLength++;
			getHeaderLength(huffmanTreeRoot.getLeft());
			getHeaderLength(huffmanTreeRoot.getRight());
		}

	}

	private static String peek(int i) {
		if (i > s.length())
			return "";
		String r = s.substring(0, i);
		s = s.substring(i);
		return r;
	}

	private static void readFile(File uncompressedFile) throws IOException {

		bytes = new byte[(int) uncompressedFile.length()];
		// read file as bytes
		try (FileInputStream inputStream = new FileInputStream(uncompressedFile)) {// fileinputStream
			inputStream.read(bytes);
		}

		frq = new int[256];

		for (int i = 0; i < bytes.length; i++)
			frq[bytes[i] + 128]++;

		for (int i = 0; i < frq.length; i++)
			if (frq[i] > 0)
				numOfChar++;

	}

	private static void initializePriorityQueue() {
		heap = new PriorityQueue<>();

		for (int i = 0; i < frq.length; i++)
			if (frq[i] > 0)
				heap.add(new HuffmanNode(frq[i], (byte) i, true));

	}

	private static void buildHuffmanTree() {
		while (heap.size() > 1) {
			HuffmanNode node = new HuffmanNode(0);

			HuffmanNode left = (HuffmanNode) heap.poll();
			HuffmanNode right = (HuffmanNode) heap.poll();

			node.addLift(left);

			node.addRight(right);

			heap.add(node);

		}

		huffmanTreeRoot = (HuffmanNode) heap.peek();

	}
	

	public static void main(String[] args) throws Exception {
		launch(args);
	}

}
