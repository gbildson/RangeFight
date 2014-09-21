package com.cardfight.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import com.cardfight.client.img.GWTImageBundle;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DialogBox;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RangeFight implements EntryPoint, ChangeListener, ClickListener, FocusListener {
	private static final int NUM_PLAYERS = 9;
	private static String blank = "________";
	private static String rank[] = {"A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"};
	private static String suit[] = {"s", "h", "d", "c"};
	
	private boolean mouseDown = false;
	private HashMap<Element, ToggleButton>  pbElements  = new HashMap<Element, ToggleButton>();
	private HashMap<String, ToggleButton>   pbByName    = new HashMap<String, ToggleButton>();
	private HashMap<String, PlayerInfo>     playerByPos = new HashMap<String, PlayerInfo>();
    private HashMap<PushButton, String>     deck        = new HashMap <PushButton,String>();
    private HashMap<String,PushButton>      namedDeck   = new HashMap <String,PushButton>();
    private HashMap<String, String>         dealtValues = new HashMap <String, String>();
    private HashMap<String, PushButton>     hiddenCards = new HashMap <String, PushButton>();
    private HashMap<PushButton, String>     dealt       = new HashMap <PushButton,String>();
    private HashMap<String, Image>          dealtImages = new HashMap <String, Image>();
    private HashSet<String>                 usedCards   = new HashSet<String>();
    private int[]                           resultLocs  = new int[NUM_PLAYERS];

	TextBox   rangeText;
	Button    selectRange;
	Button    clearRange;
	Button    cancelRange;
    SliderBar slider;
    TextBox   startBox;
    Button    startUp;
    Button    startDown;
    TextBox   endBox;
    Button    endUp;
    Button    endDown;
    Grid      status;
    TextBox   calcType;
    TextBox   calcNum;
    TextBox   calcTime;
    double    initialStart = 10.0;
    double    initialEnd   = 30.0;

    ArrayList<HandRank> list = new ArrayList<HandRank>();
    DialogBox dialogBox;
    DialogBox deckDialogBox;
    PlayerInfo activeInfo = null;
    Button     doneCardSelection;
    Button     clearCardSelection;
    Button     cancelCardSelection;
    TextBox    deckSelectionText;
	Button     calc;
	Button     clearAll;
	TextBox    commonCards;
	TextBox    deadCards;
	String     priorText;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    //Image img = new Image("http://code.google.com/webtoolkit/logo-185x175.png");
    //Button button = new Button("Click me");
    
    // We can add style names
    //button.addStyleName("pc-template-btn");
    // or we can set an id on a specific element for styling
    //img.getElement().setId("pc-template-img");
    

	// Create 5 player inputs
	Grid            pgrid = new Grid(13, 4);
	pgrid.setText(0, 0, "Location");
	pgrid.setText(0, 1, "Hand Range");
	pgrid.setText(0, 2, "Equity");
	//VerticalPanel   vp = new VerticalPanel();
	//HorizontalPanel hp;
	Button          playerLabel;
	TextBox         playerRange;
	Label           playerEquity;
	Button          playerClear;
	for (int i = 0; i < NUM_PLAYERS; i++) {
		int row = i + 1;
		//hp = new HorizontalPanel();
		String pname = "Player "+(i+1);
		playerLabel = new Button(pname);
		playerRange = new TextBox();
		playerRange.setVisibleLength(42);
		playerEquity = new Label(blank);
		playerClear = new Button("Clear");
	    playerLabel.addClickListener(this);
	    playerClear.addClickListener(this);

	    PlayerInfo info = new PlayerInfo();
	    info.playerLabel  = playerLabel;
	    info.playerRange  = playerRange;
	    info.playerClear  = playerClear;
	    info.playerEquity = playerEquity;
	    pname = "P"+(i+1);
	    playerByPos.put(pname, info);
	    playerLabel.setTitle(pname+": Select Range");
	    playerRange.setTitle(pname+": Enter Range");
	    playerClear.setTitle(pname+": Clear Range");

		pgrid.setWidget(row, 0, playerLabel);
		pgrid.setWidget(row, 1, playerRange);
		pgrid.setWidget(row, 2, playerEquity);
		pgrid.setWidget(row, 3, playerClear);
	}
	
	// Build board and discard inputs
	//HorizontalPanel common  = new HorizontalPanel();
	Button          commonLabel = new Button("Common");
	commonCards = new TextBox();
	commonCards.addChangeListener(this);
	commonCards.addFocusListener(this);
	commonCards.setVisibleLength(42);
	Button          commonClear   = new Button("Clear");
    //common.add(commonLabel);
    //common.add(commonCards);
    //common.add(commonClear);
	pgrid.setWidget(NUM_PLAYERS+1, 0, commonLabel);
	pgrid.setWidget(NUM_PLAYERS+1, 1, commonCards);
	pgrid.setWidget(NUM_PLAYERS+1, 3, commonClear);
    PlayerInfo info = new PlayerInfo();
    info.playerLabel = commonLabel;
    info.playerRange = commonCards;
    info.playerClear = commonClear;
    String pname = "Common Cards ";
    playerByPos.put(pname, info);
    commonLabel.setTitle(pname+": Select Cards");
    commonCards.setTitle(pname+": Enter Cards");
    commonClear.setTitle(pname+": Clear Cards");
    info.playerLabel.addClickListener(this);
    info.playerClear.addClickListener(this);
	
	
	HorizontalPanel dead  = new HorizontalPanel();
	Button          deadLabel = new Button("Dead");
	deadCards = new TextBox();
	deadCards.addChangeListener(this);
	deadCards.addFocusListener(this);
	deadCards.setVisibleLength(42);
	Button          deadClear   = new Button("Clear");
    //dead.add(deadLabel);
    //dead.add(deadCards);
    //dead.add(deadClear);
	pgrid.setWidget(NUM_PLAYERS+2, 0, deadLabel);
	pgrid.setWidget(NUM_PLAYERS+2, 1, deadCards);
	pgrid.setWidget(NUM_PLAYERS+2, 3, deadClear);
    info = new PlayerInfo();
    info.playerLabel = deadLabel;
    info.playerRange = deadCards;
    info.playerClear = deadClear;
    pname = "Dead Cards ";
    playerByPos.put(pname, info);
    deadLabel.setTitle(pname+": Select Cards");
    deadCards.setTitle(pname+": Enter Cards");
    deadClear.setTitle(pname+": Clear Cards");
    info.playerLabel.addClickListener(this);
    info.playerClear.addClickListener(this);

	// Build calculation interface
	HorizontalPanel control  = new HorizontalPanel();
	calc     = new Button("Calculate");
	clearAll = new Button("Clear All");
	calc.addClickListener(this);
	clearAll.addClickListener(this);
	control.add(calc);
	control.add(clearAll);
	
	status = new Grid(4, 3);
	status.setWidget( 1,0, new Label("METHOD:  ") );
	status.setWidget( 1,1, calcType = new TextBox() );
	status.setWidget( 2,0, new Label("CALCULATIONS:  ") );
	status.setWidget( 2,1, calcNum = new TextBox() );
	status.setWidget( 3,0, new Label("TIME:  ") );
	status.setWidget( 3,1, calcTime = new TextBox() ); 
	status.setWidget( 3,2, new Label(" milliseconds.") );
	calcType.setVisibleLength(14);
	calcNum.setVisibleLength(14);
	calcTime.setVisibleLength(14);
	status.setVisible(false);
	
    VerticalPanel top = new VerticalPanel();
    top.setWidth("100%");
    top.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
    top.add(pgrid);
    //top.add(common);
    //top.add(dead);
    top.add(control);
    top.add(status);  

	/*
 	Slider2Bar.LabelFormatter labelFormater	= new Slider2Bar.LabelFormatter() {
 		public String formatLabel(Slider2Bar slider, double value) {
 		    return Double.toString(value);
 		}
 	    };
 
 	//Slider2Bar slider2			= new Slider2Bar(0, 24, .5, 320, 12, 24, true, labelFormater);
 	//slider2.addChangeListener(		this);
 	//slider2.setNotifyOnMouseUp(		true);
 	//slider2.setNotifyOnMouseMove(		false);
 	//top.add(slider2);
 	
 	Slider2Bar slider3 = new Slider2Bar(0, 100, 5, true);
 	slider3.setNumTicks(10); 
 	slider3.setNumLabels(5);
 	slider3.setCurrent1Value(25.0,true); 
 	slider3.setCurrent2Value(75.0,true);
 	top.add(slider3);
 	*/
 
	
    // Add image and button to the RootPanel
    //RootPanel.get().add(top);
    RootPanel content = RootPanel.get("content");
    if (content != null) {
        content.add(top);
    }
    
    // Create the dialog box
    //final DialogBox dialogBox = new DialogBox();
    //dialogBox.setText("Welcome to GWT!");
    //dialogBox.setAnimationEnabled(true);
    //Button closeButton = new Button("close");
    //VerticalPanel dialogVPanel = new VerticalPanel();
    //dialogVPanel.setWidth("100%");
    //dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
    //dialogVPanel.add(closeButton);

    //closeButton.addClickListener(new ClickListener() {
    //  public void onClick(Widget sender) {
    //    dialogBox.hide();
    //  }
    //});

    // Set the contents of the Widget
    //dialogBox.setWidget(dialogVPanel);
    
    //button.addClickListener(new ClickListener() {
      //public void onClick(Widget sender) {
        //dialogBox.center();
        //dialogBox.show();
      //}
    //});
  }
  
  private void createDialog() {
	  ToggleButton pb;
	  String       name;
	  
	  // Create the dialog box
	  if (dialogBox == null) {
		  dialogBox = new GBDialogBox();
		  
		  VerticalPanel dialogVPanel = new VerticalPanel();
		  dialogVPanel.setWidth("100%");
		  dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		  //MainEventPreviewer  previewer = new MainEventPreviewer (dialogVPanel); 

			// Select Range
		    //
		    
		    // Build Hand table grid
		    VerticalPanel vPanel = new VerticalPanel();
		    vPanel.setWidth("100%");
		    vPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		    //FocusPanel fPanel = new FocusPanel(vPanel);
		    //fPanel.addMouseListener(new GBMouseListener());
		    //MainEventPreviewer  previewer = new MainEventPreviewer (vPanel); 
		    //vPanel.add(img);
		    //vPanel.add(button);

		    String       style;
		    Grid g = new Grid(rank.length, rank.length);
		    //VerticalPanel v = new VerticalPanel();
			g.addStyleName("pb-grid");
		    for ( int i = 0; i < rank.length; i++ ) {
		    	//HorizontalPanel h = new HorizontalPanel();
		    	//v.add(h);
		    	for ( int j = 0; j < rank.length; j++ ) {
		    		if ( i == j ) {
		    			name = rank[i] + rank[j];
		    			style = "pb-pair";
		    		} else if ( i < j ) {
		    			name = rank[i] + rank[j] + "s";
		    			style = "pb-sooted";
		    		} else { // if ( i > j ) 
		    			name = rank[j] + rank[i] + "o";
		    			style = "pb-unsuited";
		    		}
		    		pb = new ToggleButton(name);
					pb.setStylePrimaryName(style);
					Element key = pb.getElement();
					pbElements.put(key, pb);
					pbByName.put(name, pb);
		    		g.setWidget(i,j, pb);
		    		pb.addClickListener(this);
					//h.add(pb);
		        }
		    }
			vPanel.add(g);
			
			// Build range selection label and display
			dialogVPanel.add(new Label("Range Selection"));
			dialogVPanel.add(vPanel);
		    
		    slider = new SliderBar(0.0,100.0);
		    slider.setStepSize(1.0);
		    slider.setCurrentValue(initialStart);
		    slider.setCurrentValue2(initialEnd);
		    slider.setNumTicks(10);
		    slider.setNumLabels(5);
		    slider.addChangeListener(this);
		    HorizontalPanel sliderInputs = new HorizontalPanel();
		    startBox  = new TextBox();
		    endBox    = new TextBox();
		    startBox.setVisibleLength(5);
		    startBox.setText(""+initialStart);
		    endBox.setVisibleLength(6);
		    endBox.setText(""+initialEnd);
		    startUp   = new Button("+");
		    startDown = new Button("-");
		    endUp     = new Button("+");
		    endDown   = new Button("-");
		    startUp.addClickListener(this);
		    startDown.addClickListener(this);
		    endUp.addClickListener(this);
		    endDown.addClickListener(this);
		    sliderInputs.add(new Label(" Start  "));
		    sliderInputs.add(startBox);
		    sliderInputs.add(startUp);
		    sliderInputs.add(startDown);
		    sliderInputs.add(new Label("   End  "));
		    sliderInputs.add(endBox);
		    sliderInputs.add(endUp);
		    sliderInputs.add(endDown);
		    dialogVPanel.add(slider);
		    dialogVPanel.add(sliderInputs);
			buildPokerStoveList();

			HorizontalPanel rangeDisplay = new HorizontalPanel();
			rangeDisplay.add(new Label("Range"));
			rangeText = new TextBox();
			rangeText.setVisibleLength(40);
			rangeDisplay.add(rangeText);
			dialogVPanel.add(rangeDisplay);
			
			HorizontalPanel rangeSelection = new HorizontalPanel();
			selectRange = new Button("Done");
		    selectRange.addClickListener(this);
			rangeSelection.add(selectRange);
			clearRange  = new Button("Clear");
		    clearRange.addClickListener(this);
			rangeSelection.add(clearRange);
			cancelRange  = new Button("Cancel");
		    cancelRange.addClickListener(this);
			rangeSelection.add(cancelRange);
			dialogVPanel.add(rangeSelection);
		  
		  //dialogBox.setText("Welcome to GWT!");
		  //dialogBox.setAnimationEnabled(true);
		  //Button closeButton = new Button("close");

		  //dialogVPanel.add(closeButton);

		  // Set the contents of the Widget
		  dialogBox.setWidget(dialogVPanel);
		  dialogBox.setWidth("550px");
	  }
	  //System.out.println("before ... ");
	  HashSet<String> hands = RangeUtils.parseRange(activeInfo.playerRange.getText());
	  //System.out.println("after ... ");
	  //System.out.println("Hand ranges: "+hands);
	  if ( hands != null ) {;
		  for ( String hand : hands ) {
			  pb = pbByName.get(hand);
			  //System.out.println("Setting Hand: "+hand);
			  if ( pb == null ) {
				  //System.out.println("Null: "+hand);
			  } else 
				  pb.setDown(true);
		  }
		  setRangeFromTable();
	  }
	  
	  dialogBox.center();
	  dialogBox.show();
  }
  
  private void createDeckSelectDialog() {
	  // Create the dialog box
	  if (deckDialogBox == null) {
		  deckDialogBox = new DialogBox();
		  
		  VerticalPanel dialogVPanel = new VerticalPanel();
		  dialogVPanel.setWidth("100%");
		  dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		  
		  
		    // Deck selection
		    Grid g = new Grid(2, 2);
		    g.addStyleName("pc2-color-grid");
			final Image[][] imgSmallCards = {
					{new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(), new Image(),new Image(),new Image(),new Image()}, 
					{new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(), new Image(),new Image(),new Image(),new Image()}, 
					{new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(), new Image(),new Image(),new Image(),new Image()}, 
					{new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(),new Image(), new Image(),new Image(),new Image(),new Image()} 
			};

			for( int j=0; j<4; j++ ) {
				for( int k=0; k<13; k++ ) {
			        //imgSmallCards[j][k].setUrl( "img/"+rank[k]+suit[j]+"50.gif" );
					imgSmallCards[j][k] = GWTImageBundle.getImage(rank[k]+suit[j]+"50");
					//GWTImageBundle.getProto(rank[k]+suit[j]+"50").applyTo(imgSmallCards[j][k]);
			        //imgSmallCards[j][k].setVisibleRect(1, 2, 12, 33);
				}
			}
			//HorizontalPanel[] vp = new HorizontalPanel[4];
		    HorizontalPanel hPanel = null;
			
			for( int j=0; j<4; j++ ){
				for( int k=0; k<13; k++ ) {
					int nj = j / 2;
					int nk = ((j*13 + k) / 13) % 2;
					if ( k % 13 == 0 ){ 
					    hPanel = new HorizontalPanel();
					    hPanel.setWidth("100%");
					    hPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
						hPanel.addStyleName("pc2-template-grid");
					    g.setWidget( nj, nk,  hPanel);
					}
					PushButton pb = new PushButton(imgSmallCards[j][k]);
					pb.addStyleName("pc2-template-btn");
				    pb.addClickListener(this);
				    deck.put(pb, rank[k]+suit[j]);
				    namedDeck.put(rank[k]+suit[j], pb);
				    if ( usedCards.contains(rank[k]+suit[j]) )
				    	pb.setEnabled(false); 

				    hPanel.add(pb);
				}
			}
		    HorizontalPanel numPlayerPanel = new HorizontalPanel();
			numPlayerPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);

		    doneCardSelection = new Button("Done");
		    doneCardSelection.addClickListener(this);
		    numPlayerPanel.add( doneCardSelection );
		    clearCardSelection = new Button("Clear");
		    clearCardSelection.addClickListener(this);
		    numPlayerPanel.add( new Label("    ") );
		    numPlayerPanel.add( clearCardSelection );
		    cancelCardSelection = new Button("Cancel");
		    cancelCardSelection.addClickListener(this);
		    numPlayerPanel.add( cancelCardSelection );
		    numPlayerPanel.add( new Label("    ") );
		    numPlayerPanel.setStyleName("inp-pnl");
		    
		    DockPanel dock = new DockPanel();

		    dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		    // Add text all around
		    Label selectCardLabel = new Label("Select Cards");
		    selectCardLabel.setStyleName("ccl");
		    dock.add(selectCardLabel, DockPanel.NORTH);
		    dock.add(numPlayerPanel, DockPanel.SOUTH);
		    //dock.add(new HTML(constants.cwDockPanelEast()), DockPanel.EAST);
		    //dock.add(new HTML(constants.cwDockPanelWest()), DockPanel.WEST);
		    //dock.add(new HTML(constants.cwDockPanelNorth2()), DockPanel.NORTH);
		    dock.add(g, DockPanel.SOUTH);
		    dock.addStyleName("pc2-color-dock");
		    FlowPanel flow = new FlowPanel();
		    flow.add(dock);
		    flow.addStyleName("pc2-color-tp");
		    
		  //deckSelectionText = new TextBox();
		  //dialogVPanel.add(deckSelectionText);
		  HorizontalPanel selectedCards = new HorizontalPanel();
		  for (int i = 0; i < 5; i++) {
			  Image  cardImg1 = new Image(); 
			  GWTImageBundle.getProto("blank").applyTo(cardImg1);
			  PushButton cardButton1 = new PushButton(cardImg1);
			  cardButton1.addStyleName("pc2-template-btn");
			  cardButton1.addClickListener(this);
			  selectedCards.add(cardButton1);

			  String name = "S"+i;
			  dealt.put(cardButton1, name);
			  dealtImages.put(name, cardImg1);

	      }
		  dialogVPanel.add(selectedCards);

		  dialogVPanel.add(flow);

		  // Set the contents of the Widget
		  deckDialogBox.setWidget(dialogVPanel);
		  deckDialogBox.setWidth("550px");
	  }
	  
	  for( int j=0; j<4; j++ ){
		  for( int k=0; k<13; k++ ) {
			  int nj = j / 2;
			  int nk = ((j*13 + k) / 13) % 2;
			  PushButton pb = namedDeck.get(rank[k]+suit[j]);
			  if ( usedCards.contains(rank[k]+suit[j]) ) 
				  pb.setEnabled(false); 
			  else
				  pb.setEnabled(true); 
		  }
	  }
	  
	  ArrayList<String> cards = RangeUtils.parseCards(activeInfo.playerRange.getText());
	  for (int i = 0; cards != null && i < Math.min(cards.size(), 5); i++) {
		  String card = cards.get(i);

		  String name = "S"+i;
		  PushButton pb = namedDeck.get(card);

		  hiddenCards.put(card, pb);
		  usedCards.add(card);
		  pb.setEnabled(false); 
		  dealtValues.put(name, card);
		  Image dealtImg = dealtImages.get(name);
		  GWTImageBundle.getProto(card+"50").applyTo(dealtImg);
      }
	  deckDialogBox.center();
	  deckDialogBox.show();
  }
  
  
  
  public void onChange(Widget sender) {
	  
	  if ( sender == slider ) {
		  //System.out.println( "v1: " +slider.getCurrentValue() + " v2: "+slider.getCurrentValue2() );
		  startBox.setText(""+slider.getCurrentValue());
		  endBox.setText(""+slider.getCurrentValue2());
		  selectHandRanks();
		  setRangeFromTable();
	  } else if ( sender == commonCards ) {
		  System.out.println(" commonCards: "+commonCards.getText()+ " prior:"+priorText);
		  resetUsedCards(priorText, commonCards.getText());
	  } else if ( sender == deadCards ) {
		  System.out.println(" deadCards: "+deadCards.getText()+ " prior:"+priorText);
		  resetUsedCards(priorText, deadCards.getText());
	  }
  }
  
  private void setRangeFromTable() {
	  String selection = determineRanges();
	  rangeText.setText(selection);
  }
  
  private void resetUsedCards(String before, String after) {
	  clearUsedCards(before);
	  ArrayList<String> cards = RangeUtils.parseCards(after);
	  for (String card: cards)
		  usedCards.add(card);
  }
  
  public void onFocus(Widget sender) {
      if ( sender == commonCards ) {
    	  priorText = commonCards.getText();
    	  //System.out.println("Prior Text: "+priorText);
      } else if ( sender == deadCards ) {
    	  priorText = deadCards.getText();
    	  //System.out.println("Prior Text: "+priorText);
      }
  }
  
  public void onLostFocus(Widget sender) { }

  public void onClick(Widget sender) {
	  String lookupVal;
	  if (sender == startUp) {
		  double val = Double.valueOf(startBox.getText()).doubleValue();
		  val = (val *10 + 1)/10;
		  startBox.setText(""+val);
		  slider.setCurrentValue(val, false);
		  selectHandRanks();
		  setRangeFromTable();
	  } else if (sender == startDown) {
		  double val = Double.valueOf(startBox.getText()).doubleValue();
		  val = (val *10 - 1)/10;
		  startBox.setText(""+val);
		  slider.setCurrentValue(val, false);
		  selectHandRanks();
		  setRangeFromTable();
	  } else if (sender == endUp) {
		  double val = Double.valueOf(endBox.getText()).doubleValue();
		  val = (val *10 + 1)/10;
		  endBox.setText(""+val);
		  slider.setCurrentValue2(val, false);
		  selectHandRanks();
		  setRangeFromTable();
	  } else if (sender == endDown) {
		  double val = Double.valueOf(endBox.getText()).doubleValue();
		  val = (val *10 - 1)/10;
		  endBox.setText(""+val);
		  slider.setCurrentValue2(val, false);
		  selectHandRanks();
		  setRangeFromTable();
	  } else if (sender instanceof ToggleButton) {
		  setRangeFromTable();
	  } else if (sender == selectRange) {
		  String selection = rangeText.getText();
		  activeInfo.playerRange.setText(selection);
		  clearHandRanks();
		  dialogBox.hide();
	  } else if (sender == clearRange) {
		  clearHandRanks();
		  setRangeFromTable();
	  } else if (sender == cancelRange) {
		  clearHandRanks();
		  dialogBox.hide();
	  } else if (sender == clearAll)  {
		  clearPlayerStats();
		  Set<String> keys = playerByPos.keySet();
		  for (String key: keys) {
			  PlayerInfo info = playerByPos.get(key);
			  info.playerRange.setText("");
		  }
		  usedCards   = new HashSet<String>();

	  } else if ( (lookupVal = deck.get(sender)) != null ) {
		  String name = findOpenCardSlot();
		  if ( name != null ) {
			  System.out.println("lv:"+lookupVal+" name:"+name);
			  hiddenCards.put(lookupVal, ((PushButton)sender));
			  usedCards.add(lookupVal);
			  ((PushButton)sender).setEnabled(false); 
			  dealtValues.put(name, lookupVal);
			  Image dealtImg = dealtImages.get(name);
			  GWTImageBundle.getProto(lookupVal+"50").applyTo(dealtImg);
		  }
	  } else if ( (lookupVal = dealt.get(sender)) != null ) {
		  System.out.println("Dealt card :"+lookupVal);
		  returnToDeck(lookupVal, true);
	  } else if (sender == clearCardSelection) {
		  returnAllToDeck(true);
	  } else if (sender == doneCardSelection) {
		  String cards = getSelectedCards();
		  activeInfo.playerRange.setText(cards);
		  deckDialogBox.hide();
		  returnAllToDeck(false);
	  } else if (sender == cancelCardSelection) {
		  deckDialogBox.hide();
		  returnAllToDeck(true);
	  } else if (sender == calc) {
		  doCalculate();
	  } else {
		  String pname = sender.getTitle();
		  if ( pname != null && pname.length() > 2 ) {
			  pname = pname.substring(0,pname.indexOf(":"));
			  PlayerInfo info = playerByPos.get(pname);
			  if ( info != null ) {
				  activeInfo = info;
				  if ( info.playerLabel == sender ) {
					  System.out.println("Selected :"+pname);
					  if ( pname.startsWith("P") )
						  createDialog();
					  else
						  createDeckSelectDialog();
				  } else if ( info.playerClear == sender ) {
					  clearPlayerStats();
					  System.out.println("Clear :"+pname);
					  if ( !pname.startsWith("P") )
						  clearUsedCards(info.playerRange.getText());
					  info.playerRange.setText("");
				  }
			  }
		  }
	  }
  }
  
  private String findOpenCardSlot() {
	  String val;
	  for (int i = 0; i < 5; i++) {
		String name = "S"+i;
		val = dealtValues.get(name);
		if ( val == null ) 
			return name;
	  }

	  return null;
  }
  
  private String getSelectedCards() {
	  String val;
	  String result = "";
	  for (int i = 0; i < 5; i++) {
		String name = "S"+i;
		val = dealtValues.get(name);
		if ( val != null ) 
			result += val;
	  }

	  return result;
  }
  
  
  private boolean returnToDeck(String name, boolean doUsed) {
	  String val = dealtValues.get(name);
	  if ( val != null ) {
		  PushButton pb = hiddenCards.remove(val);
		  if ( doUsed )
			  usedCards.remove(val);
		  pb.setEnabled(true);
		  dealtValues.remove(name);
		  Image dealtImg = dealtImages.get(name);
		  //dealtImg.setUrl("img/blank_card.gif");
		  GWTImageBundle.getProto("blank").applyTo(dealtImg);
		  return true;
	  }
	  return false;
  }
  
  private void returnAllToDeck(boolean doUsed) {
	  String name;
	  for (int i = 0; i < 5; i++) {
		  name = "S"+i;
		  returnToDeck(name, doUsed);
	  }
  }


  
  private void selectHandRanks() {
	  ToggleButton tb;
	  double startRange = Double.valueOf(startBox.getText()).doubleValue();
	  double endRange   = Double.valueOf(endBox.getText()).doubleValue();
	  
	  for (HandRank rank : list) {
	      tb = pbByName.get(rank.name);
	      if (tb == null) {
	    	  System.out.println(rank.name + " - null");
	      }
	      if (rank.rank >= startRange && rank.rank <= endRange) {
	    	  tb.setDown(true);
	      } else {
	    	  tb.setDown(false);
	      }
	  }
  }
  
  private void clearHandRanks() {
	  ToggleButton tb;
	  
	  for (HandRank rank : list) {
	      tb = pbByName.get(rank.name);
    	  tb.setDown(false);
	  }
	  rangeText.setText("");
  }
  
  private String determineRanges() {
	  String res = "";
	  ToggleButton tb;
	  String       name;

	  // Get pairs
	  boolean active         = false;
	  String  activeName     = null;
	  String  lastActiveName = null;
	  boolean endSignal      = false;
	  for ( int i = 0; i < rank.length; i++ ) {
		  name = rank[i] + rank[i];
		  tb = pbByName.get(name);
		  if ( tb.isDown() ) {
			  if (!active) {
				  active = true;
				  activeName = name;
			  } 
			  lastActiveName = name;
		  } else {
			  endSignal = true;
		  }
		  if ( i == rank.length-1 )
			  endSignal = true;

		  if (endSignal && active) { 
			  if ( activeName.equals(lastActiveName) ) {
				  res += "," + activeName;
			  } else if ( activeName.equals("AA")) {
				  res += "," + lastActiveName + "+";
			  } else {
				  res += "," + activeName + "-" + lastActiveName;
			  }
			  active         = false;
			  activeName     = null;
			  lastActiveName = null;
		  }
		  endSignal      = false;
	  }

	  // Get suited connectors
	  active         = false;
	  activeName     = null;
	  lastActiveName = null;
	  endSignal      = false;
	  for ( int i = 0; i < rank.length; i++ ) {
		  for ( int j = i+1; j < rank.length; j++ ) {
			  name = rank[i] + rank[j] + "s";
			  tb = pbByName.get(name);
			  if ( tb.isDown() ) {
				  if (!active) {
					  active = true;
					  activeName = name;
				  } 
				  lastActiveName = name;
			  } else {
				  endSignal = true;
			  }
			  if ( j == rank.length-1 && active )
				  endSignal = true;

			  if (endSignal && active) { 
				  //System.out.println(" aN:" +activeName + " lAN:"+lastActiveName+ " i:"+i + " j:"+j);
				  if ( activeName.equals(lastActiveName) ) {
					  res += "," + activeName;
				  } else if ( activeName.equals(rank[i] + rank[i+1] + "s")) {
					  res += "," + lastActiveName + "+";
				  } else {
					  res += "," + activeName + "-" + lastActiveName;
				  }
				  active         = false;
				  activeName     = null;
				  lastActiveName = null;
			  }
			  endSignal      = false;
		  }
	  }

	  // Get unsuited connectors
	  active         = false;
	  activeName     = null;
	  lastActiveName = null;
	  endSignal      = false;
	  for ( int i = 0; i < rank.length; i++ ) {
		  for ( int j = i+1; j < rank.length; j++ ) {
			  name = rank[i] + rank[j] + "o";
			  tb = pbByName.get(name);
			  if ( tb.isDown() ) {
				  if (!active) {
					  active = true;
					  activeName = name;
				  } 
				  lastActiveName = name;
			  } else {
				  endSignal = true;
			  }
			  if ( j == rank.length-1 && active )
				  endSignal = true;

			  if (endSignal && active) {
				  if ( activeName.equals(lastActiveName) ) {
					  res += "," + activeName;
				  } else if ( activeName.equals(rank[i] + rank[i+1] + "o")) {
					  res += "," + lastActiveName + "+";
				  } else {
					  res += "," + activeName + "-" + lastActiveName;
				  }
				  active         = false;
				  activeName     = null;
				  lastActiveName = null;
			  }
			  endSignal      = false;
		  }
	  }
	  if ( res.length() > 1 )
		  res = res.substring(1);
	  return res;
  }
  
 
  
  private void clearUsedCards(String cards) {
	  ArrayList<String> cardList = RangeUtils.parseCards(cards);
	  for (String card : cardList) 
		  usedCards.remove(card);
  }



  
  /*
  class GBMouseListener extends MouseListenerAdapter {
	  public void onMouseDown(Widget sender, int x, int y) {
		  mouseDown = true;
	  }

	  public void onMouseUp(Widget sender, int x, int y) {
		  mouseDown = false;
	  }
  }
  class  GBToggleButton extends ToggleButton {
	  public GBToggleButton(String name) {
		  super(name);
	  }

	  public void onBrowserEvent(Event event) {
		  super.onBrowserEvent(event);
		  //DOM.eventGetType(event);
		  //System.out.println("mouseDown: "+mouseDown + " Evt:"+DOM.eventGetTypeString(event));
		  if ( mouseDown && DOM.eventGetType(event) == Event.ONMOUSEOVER ) {
			  setDown(true);
		  }
	  }
  }*/
  
  class HandRank {
	  public String name;
	  public double rank;
	  
	  public HandRank(String name, double rank) {
		  if ( name.length() == 2 && !name.substring(0,1).equals(name.substring(1,2)) )
			  name = name + "o";
		  this.name = name;
		  this.rank = rank;
	  }
  }
  
  class PlayerInfo {
		Button          playerLabel;
		TextBox         playerRange;
		Label           playerEquity;
		Button          playerClear;
  }

  class GBDialogBox extends DialogBox implements EventPreview {
	    private boolean ib_lbutton_down = false;


	    /**
	     * Preview all browser events before they are passed on to their respective widgets here. As long as there is no PopupPanel or DialogBox currently showing, this
	     * preview code will be at the top of the browser stack and hence helps avoid things like default text selection when DRAGGING or doing SHIFT-CLICK to highlight multiple objects
	     * on a list control for instance.
	     *
	     * @param event Browser event to be previewed
	     * @return TRUE if the event must be propagated or FALSE to block event
	     *
	     */
	    public boolean onEventPreview(Event event) {
	    	//System.out.println("previewing event");
	        int type = DOM.eventGetType(event);
	        switch (type) {
	            /*case Event.ONKEYDOWN:
	            case Event.ONKEYUP:
	            case Event.ONKEYPRESS:
	            	System.out.println("keypress 1");
	                if (DOM.eventGetShiftKey(event) || DOM.eventGetCtrlKey(event)) {
	                    DOM.eventPreventDefault(event);
	                    System.out.println("keypress 2");
	                }
	                break;*/
	            case Event.ONMOUSEUP:
	                ib_lbutton_down = false;
	                setTextSelectionEnabled(true);//for Chrome and Safari browsers use the JSNI method
                    //System.out.println("up");
	          		mouseDown = false;
	                break;
	            /*case Event.ONCONTEXTMENU:
	                DOM.eventPreventDefault(event);
	                DOM.eventCancelBubble(event, true);
	                System.out.println("cancel");
	                return false;//donmt allow to bubble break;*/
	            case Event.ONMOUSEDOWN:
	                if (DOM.eventGetButton(event) == Event.BUTTON_LEFT) {
	                	//System.out.println("l down");
	                    ib_lbutton_down = true;
	          		    mouseDown = true;
	                    /*if (DOM.eventGetShiftKey(event) || DOM.eventGetCtrlKey(event)) {
	                        DOM.eventPreventDefault(event);
		                	System.out.println("l down2");
	                    }*/
	                }
	                break;
	            case Event.ONMOUSEMOVE:
	                if (ib_lbutton_down || DOM.eventGetShiftKey(event)) {
	                    DOM.eventPreventDefault(event);

	                    //for Chrome and Safari do not respect eventPreventdefault so we have used a JSNI method here
	                    //to prevent text selection and default dragging
	                    setTextSelectionEnabled(false);
	                	//System.out.println("move");
	                }
	                break;
	            //case Event.ONMOUSEOUT:
	                //System.out.println("out");
	                //break;
	            /*case Event.ONBLUR:
              	System.out.println("blur");
              	break;
	            case Event.ONLOSECAPTURE:
              	System.out.println("lose");
              	break;*/
	            case Event.ONMOUSEOVER:
	            	if (mouseDown) {
	            		Element target = DOM.eventGetTarget(event);
	            		//System.out.println("over : "+target);
	            		ToggleButton widget = pbElements.get(target);
	            		widget.setDown(true);
	          		    setRangeFromTable();
	            	}
                  break;
	            }
	        

	        //DO allow the event to fire.
	        return true;
	    }
	}  
  
  /**
   * Needed for Chrome and Safari browsers.
   *
   * Remember to call this function with ab_enable = TRUE to clear when the widget on which it is called is unattached. This avoids
   *  memory leaks
   *
   * @param ab_enable If true then the onselectstart events will be nullified, else they will be made to
   *                  return false so that default browser selection does not occur.
   */
  public native static void setTextSelectionEnabled(boolean ab_enable)/*-{
    if (!ab_enable) {
        $doc.onselectstart = function () { return false; };
    } else {
        $doc.onselectstart = null;
    }
  }-*/; 
  
  public void buildPokerStoveList() {
	  list.add(new HandRank("AA", 45/100.0));
	  list.add(new HandRank("KK", 90/100.0));
	  list.add(new HandRank("QQ", 135/100.0));
	  list.add(new HandRank("JJ", 180/100.0));
	  list.add(new HandRank("TT", 226/100.0));
	  list.add(new HandRank("AKs", 256/100.0));
	  list.add(new HandRank("99", 301/100.0));
	  list.add(new HandRank("AQs", 331/100.0));
	  list.add(new HandRank("AK", 422/100.0));
	  list.add(new HandRank("AJs", 452/100.0));
	  list.add(new HandRank("KQs", 482/100.0));
	  list.add(new HandRank("88", 527/100.0));
	  list.add(new HandRank("ATs", 558/100.0));
	  list.add(new HandRank("AQ", 648/100.0));
	  list.add(new HandRank("KJs", 678/100.0));
	  list.add(new HandRank("QJs", 708/100.0));
	  list.add(new HandRank("KTs", 739/100.0));
	  list.add(new HandRank("AJ", 829/100.0));
	  list.add(new HandRank("KQ", 920/100.0));
	  list.add(new HandRank("QTs", 950/100.0));
	  list.add(new HandRank("A9s", 980/100.0));
	  list.add(new HandRank("77", 1025/100.0));
	  list.add(new HandRank("AT", 1116/100.0));
	  list.add(new HandRank("JTs", 1146/100.0));
	  list.add(new HandRank("KJ", 1236/100.0));
	  list.add(new HandRank("A8s", 1266/100.0));
	  list.add(new HandRank("K9s", 1297/100.0));
	  list.add(new HandRank("QJ", 1387/100.0));
	  list.add(new HandRank("A7s", 1417/100.0));
	  list.add(new HandRank("KT", 1508/100.0));
	  list.add(new HandRank("Q9s", 1538/100.0));
	  list.add(new HandRank("A5s", 1568/100.0));
	  list.add(new HandRank("66", 1613/100.0));
	  list.add(new HandRank("QT", 1704/100.0));
	  list.add(new HandRank("A6s", 1734/100.0));
	  list.add(new HandRank("J9s", 1764/100.0));
	  list.add(new HandRank("A9", 1855/100.0));
	  list.add(new HandRank("A4s", 1885/100.0));
	  list.add(new HandRank("T9s", 1915/100.0));
	  list.add(new HandRank("K8s", 1945/100.0));
	  list.add(new HandRank("JT", 2036/100.0));
	  list.add(new HandRank("A3s", 2066/100.0));
	  list.add(new HandRank("K7s", 2096/100.0));
	  list.add(new HandRank("A8", 2187/100.0));
	  list.add(new HandRank("Q8s", 2217/100.0));
	  list.add(new HandRank("K9", 2307/100.0));
	  list.add(new HandRank("A2s", 2337/100.0));
	  list.add(new HandRank("J8s", 2368/100.0));
	  list.add(new HandRank("K6s", 2398/100.0));
	  list.add(new HandRank("55", 2443/100.0));
	  list.add(new HandRank("T8s", 2473/100.0));
	  list.add(new HandRank("A7", 2564/100.0));
	  list.add(new HandRank("98s", 2594/100.0));
	  list.add(new HandRank("Q9", 2684/100.0));
	  list.add(new HandRank("K5s", 2714/100.0));
	  list.add(new HandRank("A5", 2805/100.0));
	  list.add(new HandRank("J9", 2895/100.0));
	  list.add(new HandRank("Q7s", 2926/100.0));
	  list.add(new HandRank("T9", 3016/100.0));
	  list.add(new HandRank("A6", 3107/100.0));
	  list.add(new HandRank("K4s", 3137/100.0));
	  list.add(new HandRank("K8", 3227/100.0));
	  list.add(new HandRank("A4", 3318/100.0));
	  list.add(new HandRank("J7s", 3348/100.0));
	  list.add(new HandRank("Q6s", 3378/100.0));
	  list.add(new HandRank("T7s", 3408/100.0));
	  list.add(new HandRank("K3s", 3438/100.0));
	  list.add(new HandRank("97s", 3469/100.0));
	  list.add(new HandRank("87s", 3499/100.0));
	  list.add(new HandRank("A3", 3589/100.0));
	  list.add(new HandRank("44", 3634/100.0));
	  list.add(new HandRank("Q5s", 3665/100.0));
	  list.add(new HandRank("K7", 3755/100.0));
	  list.add(new HandRank("K2s", 3785/100.0));
	  list.add(new HandRank("Q8", 3876/100.0));
	  list.add(new HandRank("J8", 3966/100.0));
	  list.add(new HandRank("Q4s", 3996/100.0));
	  list.add(new HandRank("A2", 4087/100.0));
	  list.add(new HandRank("T8", 4177/100.0));
	  list.add(new HandRank("K6", 4268/100.0));
	  list.add(new HandRank("J6s", 4298/100.0));
	  list.add(new HandRank("76s", 4328/100.0));
	  list.add(new HandRank("T6s", 4358/100.0));
	  list.add(new HandRank("98", 4449/100.0));
	  list.add(new HandRank("86s", 4479/100.0));
	  list.add(new HandRank("Q3s", 4509/100.0));
	  list.add(new HandRank("96s", 4539/100.0));
	  list.add(new HandRank("J5s", 4570/100.0));
	  list.add(new HandRank("K5", 4660/100.0));
	  list.add(new HandRank("Q2s", 4690/100.0));
	  list.add(new HandRank("J4s", 4720/100.0));
	  list.add(new HandRank("33", 4766/100.0));
	  list.add(new HandRank("Q7", 4856/100.0));
	  list.add(new HandRank("65s", 4886/100.0));
	  list.add(new HandRank("K4", 4977/100.0));
	  list.add(new HandRank("75s", 5007/100.0));
	  list.add(new HandRank("J7", 5098/100.0));
	  list.add(new HandRank("J3s", 5128/100.0));
	  list.add(new HandRank("T7", 5218/100.0));
	  list.add(new HandRank("T5s", 5248/100.0));
	  list.add(new HandRank("85s", 5279/100.0));
	  list.add(new HandRank("Q6", 5369/100.0));
	  list.add(new HandRank("87", 5460/100.0));
	  list.add(new HandRank("95s", 5490/100.0));
	  list.add(new HandRank("97", 5580/100.0));
	  list.add(new HandRank("K3", 5671/100.0));
	  list.add(new HandRank("T4s", 5701/100.0));
	  list.add(new HandRank("J2s", 5731/100.0));
	  list.add(new HandRank("54s", 5761/100.0));
	  list.add(new HandRank("Q5", 5852/100.0));
	  list.add(new HandRank("64s", 5882/100.0));
	  list.add(new HandRank("T3s", 5912/100.0));
	  list.add(new HandRank("K2", 6003/100.0));
	  list.add(new HandRank("22", 6048/100.0));
	  list.add(new HandRank("74s", 6078/100.0));
	  list.add(new HandRank("Q4", 6168/100.0));
	  list.add(new HandRank("T2s", 6199/100.0));
	  list.add(new HandRank("84s", 6229/100.0));
	  list.add(new HandRank("76", 6319/100.0));
	  list.add(new HandRank("J6", 6410/100.0));
	  list.add(new HandRank("94s", 6440/100.0));
	  list.add(new HandRank("86", 6530/100.0));
	  list.add(new HandRank("T6", 6621/100.0));
	  list.add(new HandRank("53s", 6651/100.0));
	  list.add(new HandRank("96", 6742/100.0));
	  list.add(new HandRank("93s", 6772/100.0));
	  list.add(new HandRank("Q3", 6862/100.0));
	  list.add(new HandRank("J5", 6953/100.0));
	  list.add(new HandRank("63s", 6983/100.0));
	  list.add(new HandRank("43s", 7013/100.0));
	  list.add(new HandRank("92s", 7043/100.0));
	  list.add(new HandRank("Q2", 7134/100.0));
	  list.add(new HandRank("73s", 7164/100.0));
	  list.add(new HandRank("J4", 7254/100.0));
	  list.add(new HandRank("65", 7345/100.0));
	  list.add(new HandRank("83s", 7375/100.0));
	  list.add(new HandRank("75", 7466/100.0));
	  list.add(new HandRank("52s", 7496/100.0));
	  list.add(new HandRank("82s", 7526/100.0));
	  list.add(new HandRank("85", 7616/100.0));
	  list.add(new HandRank("T5", 7707/100.0));
	  list.add(new HandRank("J3", 7797/100.0));
	  list.add(new HandRank("95", 7888/100.0));
	  list.add(new HandRank("42s", 7918/100.0));
	  list.add(new HandRank("54", 8009/100.0));
	  list.add(new HandRank("62s", 8039/100.0));
	  list.add(new HandRank("T4", 8129/100.0));
	  list.add(new HandRank("J2", 8220/100.0));
	  list.add(new HandRank("72s", 8250/100.0));
	  list.add(new HandRank("64", 8340/100.0));
	  list.add(new HandRank("32s", 8371/100.0));
	  list.add(new HandRank("T3", 8461/100.0));
	  list.add(new HandRank("74", 8552/100.0));
	  list.add(new HandRank("84", 8642/100.0));
	  list.add(new HandRank("T2", 8733/100.0));
	  list.add(new HandRank("94", 8823/100.0));
	  list.add(new HandRank("53", 8914/100.0));
	  list.add(new HandRank("93", 9004/100.0));
	  list.add(new HandRank("63", 9095/100.0));
	  list.add(new HandRank("43", 9185/100.0));
	  list.add(new HandRank("92", 9276/100.0));
	  list.add(new HandRank("73", 9366/100.0));
	  list.add(new HandRank("83", 9457/100.0));
	  list.add(new HandRank("52", 9547/100.0));
	  list.add(new HandRank("82", 9638/100.0));
	  list.add(new HandRank("42", 9728/100.0));
	  list.add(new HandRank("62", 9819/100.0));
	  list.add(new HandRank("72", 9909/100.0));
	  list.add(new HandRank("32", 10000/100.0));
  }
  
  private ArrayList<String> getPlayerHands() {
	  ArrayList<String> results = new ArrayList<String>();
	  System.out.println("Results: "+results);
	  int resCnt = 0;
	  
	  for (int i = 0; i < NUM_PLAYERS; i++) {
		  resultLocs[i] = -1; // Blank out result location initially
		  PlayerInfo info;
		  String pname = "P"+(i+1);
		  info = playerByPos.get(pname);
		  String range = info.playerRange.getText();
		  if ( range != null && range.length() > 0 ) {
			  System.out.println("Range: "+range);
			  HashSet<String> ranges = RangeUtils.parseRange(range);
			  if ( ranges == null ) {
				  ArrayList<String> cards = RangeUtils.parseCards(range);
				  if (cards.size() == 2) {
				      results.add(range);
				      resultLocs[i] = resCnt++;
				  }
			  } else {
				  System.out.println("Ranges : "+ranges);
				  if ( ranges != null && ranges.size() > 0 ) {
					  results.add(range);
					  resultLocs[i] = resCnt++;  // Record result location
				  }
			  }
		  }
	  }
	  return results;
  }
  
  private String getCommonCards() {
	  PlayerInfo info;
	  String pname = "Common Cards ";
	  info = playerByPos.get(pname);
	  return (info.playerRange.getText());
  }
  
  private String getDeadCards() {
	  PlayerInfo info;
	  String pname = "Dead Cards ";
	  info = playerByPos.get(pname);
	  return (info.playerRange.getText());
  }
  
  private String insertCommas(String str) {
	  if(str.length() < 4)
		  return str;
	  return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3, str.length());
  }
  
  private void setPlayerStats(CardFightSummary summary) {
	  status.setVisible(true);
	  calcType.setText(summary.getResultTypeString());
	  calcNum.setText(insertCommas(""+summary.getCount()));
	  calcTime.setText(insertCommas(""+summary.getTime()));
	  
	  ArrayList<CardFightResult> results = summary.getResults();
	  for (int i = 0; i < results.size(); i++) {
		  CardFightResult result = results.get(i);
		  PlayerInfo info;
		  int loc = -1;
		  for (int j = 0; j < NUM_PLAYERS; j++) {
			  if ( resultLocs[j] == i ) {
				  loc = j;
				  break;
			  }
		  }
		  if ( loc == -1 ) continue;
		  String pname = "P"+(loc+1);
		  info = playerByPos.get(pname);
		  info.playerEquity.setText(" "+ result.equity+ "% ");
	  }
  }
  
  private void clearPlayerStats() {
	  status.setVisible(false);
	  PlayerInfo info;
	  for (int j = 0; j < NUM_PLAYERS; j++) {
		  String pname = "P"+(j+1);
		  info = playerByPos.get(pname);
		  info.playerEquity.setText(blank);
	  }
  }
  
  
  public void doCalculate() {
	  clearPlayerStats();
	  
	  // (1) Create the client proxy. Note that although you are creating the
	  // service interface proper, you cast the result to the asynchronous
	  // version of the interface. The cast is always safe because the 
	  // generated proxy implements the asynchronous interface automatically.
	  //
	  CardFightServiceAsync cardFightService = (CardFightServiceAsync) GWT.create(CardFightService.class);

	  // (2) Create an asynchronous callback to handle the result.
	  //
	  AsyncCallback<CardFightSummary> callback = new AsyncCallback<CardFightSummary>() {
	    public void onSuccess(CardFightSummary summary) {
	      // do some UI stuff to show success
	        //System.out.println("res: "+result);
	        setPlayerStats(summary);
	        //dirty = false;
	    }

	    public void onFailure(Throwable caught) {
	      // do some UI stuff to show failure
	        // Convenient way to find out which exception was thrown.
	        try {
	            throw caught;
	        } catch (IncompatibleRemoteServiceException e) {
	            // this client is not compatible with the server; cleanup and refresh the 
	            // browser
	            System.out.println("Not Good :" +e);
	        } catch (InvocationException e) {
	            // the call didn't complete cleanly
		        System.out.println("Not Good :" +e);
	        } catch (Throwable e) {
	            // last resort -- a very unexpected exception
			    System.out.println("Not Good :" +e);
	        }
	    }
	  };

	  // (3) Make the call. Control flow will continue immediately and later
	  // 'callback' will be invoked when the RPC completes.
	  //
	  //ArrayList<CardFightResult> calculate(ArrayList<String> playersHands, String board, String discard)
	  ArrayList<String> playerHands = getPlayerHands();
	  if ( playerHands == null ) {
		  System.out.println("Implement player card error");
		  createErrorMsg("Implement player card error");
		  return;
	  }
	  String commonCards = getCommonCards();
	  if ( commonCards.length() > 0 && commonCards.length() < 6 ){
		  //System.out.println("Implement partial flop error");
	  }
	  String discard = "R:" + getDeadCards();
	  cardFightService.calculate(playerHands, commonCards, discard, callback);
	}
  
    private void createErrorMsg(String msg) {
        // Create the dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Input Error");
        //dialogBox.setStyleName("error-window");
        dialogBox.setAnimationEnabled(true);
        Button closeButton = new Button("close");
        closeButton.setStyleName("error-but");
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.setWidth("100%");
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        Label msgLabel = new Label(msg);
  	    msgLabel.addStyleName("ccl");
  	    dialogVPanel.add(new Label("    "));
        dialogVPanel.add(msgLabel);
        dialogVPanel.add(new Label("    "));
        dialogVPanel.add(closeButton);
        
        dialogBox.center();
        dialogBox.show();

        closeButton.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
            dialogBox.hide();
          }
        });

        // Set the contents of the Widget
        dialogBox.setWidget(dialogVPanel);
        
        //button.addClickListener(new ClickListener() {
         // public void onClick(Widget sender) {
            //dialogBox.center();
            //dialogBox.show();
          //}
        //});
    }
  
}
