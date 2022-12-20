/*
 Copyright (C) 2012 Albert Steiner
 Copyright (C) 2022 Albert Steiner

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

 /*
 * StarTrader.java
 *
 * Created on Dec 15, 2011, 9:51:37 PM
 * the game has up to 5 players, some of which are robots, using preset values.
 * the object of the game is to have something to tinker with, and apply different
 * schemes for growing star planets, and spaceships that carry tradable things.
 * Ships travel between stars with cargo of the 7 resources and guests (colonists)
 * In addition both planets and ships use resources, some of which are not available
 * on the given planet.  Ships must grow by profits in transportation, birth rate is usually
 * lower than death rate so that they must accept colonists as staff.
 *
 * The planets and ships are created with varying difficulty.  Difficulty is overcome by
 * staff creating and using knowledge.  So colonist become engineer which require also
 * faculty and researchers.  Only researchers create knowledge, although ships carry
 * knowledge between planet, and planets can buy knowledge.  Each player has sets of
 * slides to adjust the choices made by stars and ships, players do not make detailed
 * moves, only adjust slides and than run from 1 to 5 years to see how well planets
 * and ships are doing in terms of health, wealth, worth, staff, knowledge.  For example,
 * if population outstrips resources than the health of the planet falls, more things
 * wear out, and more colonists die.  Resources get used up, and only new knowledge can
 * help increase efficiency and thus keep growing resources.
 * Normall
 */
package trade;

// import java.desktop/javax.swing.plaf.synth.SynthGraphicsUtils.paintText;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Error;
import java.lang.Exception;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import static trade.EM.addlErr;
import static trade.EM.isEmpty;
import static trade.EM.prevLine;
import static trade.EM.twh1;
import static trade.EM.twh2;
import static trade.EM.twh3;
import static trade.EM.twh4;
import static trade.EM.twh5;
import static trade.EM.twh6;
import static trade.EM.twh7;
import static trade.EM.twh8;
import static trade.EM.wasHere;
import static trade.EM.wasHere2;
import static trade.EM.wasHere3;
import static trade.EM.wasHere4;
import static trade.EM.wasHere5;
import static trade.EM.wasHere6;
import static trade.EM.wasHere7;
import static trade.EM.wasHere8;

/**
 * see if these imports stay here // import
 * java.desktop/javax.swing.plaf.synth.SynthGraphicsUtils.paintText; import
 * import java.awt.*; import java.io.File; import java.io.FileNotFoundException;
 * import java.io.IOException; import java.io.PrintStream; import
 * java.lang.reflect.InvocationTargetException; import java.text.NumberFormat;
 * import java.util.ArrayList; import java.util.Date; import java.util.Locale;
 * import java.util.Random; import javax.swing.DefaultListModel; import
 * javax.swing.JLabel; import javax.swing.JPanel; import javax.swing.JSlider;
 * import javax.swing.JSpinner; import javax.swing.JTabbedPane; import
 * javax.swing.JTextField; import javax.swing.ListSelectionModel; import
 * javax.swing.SpinnerModel; import javax.swing.SpinnerNumberModel; import
 * javax.swing.SwingUtilities; import javax.swing.SwingWorker; import
 * javax.swing.event.ListSelectionEvent; import
 * javax.swing.event.ListSelectionListener;
 */
/**
 *
 * @author albert steiner
 */
public class StarTrader extends javax.swing.JFrame {

  static boolean run1 = false;
  static boolean run2 = false;
  static boolean run5 = false;
  static boolean run10 = false;  // run 10 years of dorun
// There are two contexts

  static protected enum ContextNames {

    PLANET, SHIP, COUNT
  }
  static final protected int P = ContextNames.PLANET.ordinal();
  static final protected int S = ContextNames.SHIP.ordinal();
  static protected String[] contextName = {"planet", "ship"};
  static String stringTemp = "";
  // static int contextV= 0;
  // there are 7 resources and 5 groups
  //static protected int[] logLevel={5,5};
  /**
   * pointers into the hist table initially
   */
  //static protected int[] logStartMVal={0,0};
  /**
   * pointers into the hist table after a display
   */
  //static protected int[] logStartM={0,0};
  static final protected int[] logLengthDisplay = {60, 25, 50};
  static final protected int logRowCount = 50;
  static protected int logSelectedRow = 5;
  static protected Color bg1 = new java.awt.Color(140, 255, 140);

  /**
   * for each row in the display, index to a row in the hist table
   */
  static protected int[] logRowToM;
  /**
   * a pointer to the logHistory table being displayed
   */
  static protected ArrayList<History> logHistoryHist;
  /**
   * a list of the last m(hist table index)
   */
  static protected ArrayList<Integer> logMHist = new ArrayList<Integer>();
  /**
   * pointer to the display table in the log tab
   */
  static protected javax.swing.JTable pLogDisplayTable;
  static protected ArrayList<History> logHist1;
  static protected int[] displayHistoryRowToM;
  static protected ArrayList<Integer> displayHistoryMHist = new ArrayList<Integer>();
  /**
   * the last value for m(the hist table index)
   */
  static protected int[] logLastM = {0, 0};
  /**
   * the direction of movement through the hist table
   */
  static protected int[] logDirection = {1, 1};
  static final protected String[] groupNames = {"red", "orange", "yellow", "green", "blue"};
  static final protected int red = 0;
  static final protected int orange = 1;
  static final protected int yellow = 2;
  static final protected int green = 3;
  static final protected int blue = 4;
  static protected E eE;
  static EM eM;
  Econ ec;
  int nn = 0; // needed for event processors
  static protected ArrayList<String> planetsDisplay;
  static protected ArrayList<String> starsDisplay;
  static final int yearsL = 20;
  static int theYear[] = new int[yearsL];
  static int yearSecs[] = new int[yearsL];
  static int yearEcons[] = new int[yearsL];
  static int yearPlanets[] = new int[yearsL];
  static int yearShips[] = new int[yearsL];
  static double yearTW[] = new double[yearsL]; // total worth max for a year
  static double gameTW[] = new double[yearsL]; // total worth max for the game
  static int gamePlanets[] = new int[yearsL];
  static int gameShips[] = new int[yearsL];
  static int gameEcons[] = new int[yearsL];
  int yearSecPerEcon[] = new int[yearsL];

  static public int namesListRow = 0;
  /**
   * StarTrader E EM contain the used set of stats descriptors
   *
   */
  static final public String statsButton0Tip = "0: Cum Game Worths,";
  static final public String statsButton1Tip = "1: cum Favors and trade effects";
  static final public String statsButton2Tip = "2: catastrophes, deaths, randoms, forwardfund";
  static final public String statsButton3Tip = "3: deaths. trades acc";
  static final public String statsButton4Tip = "4: deaths, Rej misd Trades";
  static final public String statsButton5Tip = "5: trades accepted, rejected, missed ";
  static final public String statsButton6Tip = "6: forwardFunds, deaths";
  static final public String statsButton7Tip = "7: Resource, staff, knowledge values";
  static final public String statsButton8Tip = "8: creates. growth, forwardFunds and costs details";
  static final public String statsButton9Tip = "9: Catastrophes, Fertility, health and effects";
  static final public String statsButton10Tip = "10: list by ages deaths with trades missed, rejected, lost";
  static final public String statsButton11Tip = "11: list by ages deaths with trades accepted ";
  static final public String statsButton12Tip = "12: list by ages deaths with negative prospects";
  static final public String statsButton13Tip = "13: list by ages affects with growths depreciation";
  static final public String statsButton14Tip = "14: list by ages affects with catastrophies, forwardFunds ";
  static final public String statsButton15Tip = "15: list by ages live trades";
  static final public String statsButton16Tip = "16: list by ages worths, work,faculty,research interns";
  static final public String statsButton17Tip = "17: list by ages helps, creations ";
  static final public String statsButton18Tip = "18: Swaps years xfer skips, redos and dos";
  static final public String statsButton19Tip = "19: Swaps years Forward Fund imbalance or save";
  static final public String statsButton20Tip = "20: TB assigned";
  static final public String statsButton21Tip = "21: TB assigned";
  static final public String statsButton22Tip = "22: TB assigned";
  static final public String statsButton23Tip = "23: display table";

  static final public String gameTextFieldText = "This is to be filled with descriptions of the field over which the mouse hovers";
  /* 0:worths,1:trade favor,2:random,crisis,deaths,forward,3:deaths,4:trades,5:creates,6:forwardFund,7:resource,staff,knowledge,8:growth,costs,9:Fertility,health,effects,10 11 12 13 14 1years,15:swaps,16:swapincr,17:swapdecr,18:xfer, 19:swap forwardFund balance orsave, 20:Swaps cum 
   */

  static final public String[] statsButtonsTips = {statsButton0Tip, statsButton1Tip, statsButton2Tip, statsButton3Tip, statsButton4Tip, statsButton5Tip, statsButton6Tip, statsButton7Tip, statsButton8Tip, statsButton9Tip, statsButton10Tip, statsButton11Tip, statsButton12Tip, statsButton13Tip, statsButton14Tip, statsButton15Tip, statsButton16Tip, statsButton17Tip, statsButton18Tip, statsButton19Tip, statsButton20Tip, statsButton21Tip, statsButton22Tip, statsButton23Tip, gameTextFieldText};
  static final public String versionText = "19.40";
  static final public String storyText
          = "StarTrader       Version 19.44\n"
          + "\n"
          + "“Star Trader”; “Save the Planets”; “Trade, trade, trade”;  “Cooperating together with trades”: is a mini strategic economics game emphasizing cooperation over competition.  Each economy has assets of resources, staff and knowledge in 7 financial sectors.  Each year each financial sector has costs using up some resources and some staff.  \n"
          + "\n"
          + "Costs decrease in percentage as a sector’s efficiency increases because its knowledge has increased.  Each year after annual costs, the financial sectors with some surplus assets can grow resources and staff, find new knowledge and researchers can convert manuals into knowledge.  \n"
          + "\n"
          + "Each economy has 2 weak financial sectors and 2 strong financial sectors.  After possible trades, an economy may swap resources and staff to ensure that each financial sector has  enough resources and staff for the year.  Economies without enough resources or staff in any financial sector cannot survive to the next year, that planet or ship dies.\n"
          + "\n"
          + "Trading allows a pair of ships and planets to trade goods from strong sectors for needed assets in weak financial sectors.  In each trade some manuals are traded, allowing knowledge to be moved between economies.  Generally older economies can trade more manuals to new economies than newer economies can trade.  The trade process consists of each economy generating an offer that is evaluated by the other economy.  Each economy calculates a strategic value for each financial sector, deciding how much surplus to trade for a need.  Rejected offers are refined until one is accepted or there are too many barters and the trade fails.  Ships try to predict how well a trade with a given planet will help it when they choose the next planet for a trade.\n"
          + "\n"
          + "This is a strategy game for one to five players. The game-master sets the nature of the game as well as how the winner is picked.  The clan-masters set the very flexible economic policies for the ships and the planets in their clan.  \n"
          + "\n"
          + "Clan-masters set the favor their clan has for another clan. The higher the favor the more discount will be given that clan in trade, the more likely the trade will succeed.\n"
          + "\n"
          + "See the instructions about downloading and running this game at the end of this README.\n"
          + "\n"
          + "Stats or statistics, are reports on the life of each clan.  There are 21 different pages of statistics.  The first 3 pages are the most interesting and useful. \n"
          + "\n"
          + "There are 5 clans each including both planets and ships.  Clan-masters manage the robots that run the 5 clans by changing settings for their clan.  Clan-masters change the base settings before they are changed by the game's random multipliers.  Each year each economy experiences different random changes to their effective settings. The clans are  named the colors: red, orange, yellow, green, blue.  Eventually one clan with  the highest score is declared the winner and the stats(statistics) background is set to the color of the winning clan.  You may continue to play after a winner is selected and the winner may change after the next round of years.  Also the game-master may change the settings changing which stats do the most to select the winner. \n"
          + "\n"
          + "A winner is declared only after enough years have passed and one  clan has a score sufficiently higher than all the other clans.  Clan-masters can look in the stats tab clicking on the page numbers for different pages to try to see how to raise their score.  Their score is based on the sum of multiple scores for their clan’s ships and planets.  Clicking on the title of a row in a page gives a more detailed description of the meaning of the numbers in this row.  In a “sum” row the values for ships and planets are summed together in the ship columns, otherwise there are different sums for planets and for ships.\n"
          + "\n"
          + "The game-master has seven score settings that influence the scoring, and may change the winner.  You can change your settings and continue the game even if it has picked a winner.  After more years there could be a different winner. The game master should then communicate the changes of settings to the clan-masters so they can change their clan settings to try to improve their score based on the changed master settings.\n"
          + "\n"
          + "Clan-masters can go to the settings tab and click on the link of the color of their clan, then they click on up or down or one of the ten settings being displayed for their clan.  They change their clan’s settings to try to raise their score during the next round of years, and/or try to increase the number of their planets and ships that survive.  After all clan-masters are finished, the game-master starts another round of 1, 5, 10, or 20 years.  \n"
          + "\n"
          + "The default settings selects the clan that gave the most help in trading the last year of the round.  Of course the clan-master must balance growth and help for each year.  The robots run the game for those years without any possible changes by the game-manager or the clan-manager.\n"
          + "\n"
          + "This game is being developed on a Windows laptop with a screen about 1920 pixels wide and 1080 pixels deep.  It will run on smaller screens, but that may move parts of the scenes in funny ways.  Since it is written in Java, it can be run on systems other than windows with large enough screens and a java JRE (Java Runtime Environment) which allows the file ending with .jar to run on this host operating system.  \n"
          + "\n"
          + "The most important settings instructions are placed early in the settings.  Additional explanations of settings are later in this README.  Because this is a strategy game, it is useful to understand the guts of the game so that clan-masters can understand the possible changes to the strategy of their clan to try to win under the priorities set by the game-master.  Of course there are always some unintended results for any change to settings.\n"
          + "\n"
          + "The “keep” function, described in more detail later, enables you to keep changes, comments and sample results for later games, so that clan-masters can develop plans and strategies for exploring finances and have them preset into their next games.  One person can run several or all clans, trying different strategies for the different clans.\n"
          + "\n"
          + "You can notice that planets and ships are dying and if too many die the game will create more economies.  These deaths affect your clan’s scoring.  On the first page of settings, the game-master can change the difficulty which increases or decreases the number of economy deaths each year.  If there are more economies the years take longer.  \n"
          + "\n"
          + "The following explanation of how the game works, and the tables of numbers, stats, can help you understand what is happening to each clan.  You can change the clan settings to try to better your score, but as in any economics, sometimes the changes help, sometimes they don’t, and it is even harder to be sure which change helped if you change multiple settings at the same time.  Changed settings remain changed for the rest of the game, and you can use “keep” to keep settings for following games.  Have fun!!\n"
          + "\n"
          + "Each year some planets or ships die because they have insufficient infrastructure (short on food, or short on buildings to survive the “winter”, etc.).  If they have good health the planets will also grow with added resources and added staff.   Ships trade “goods” from planets with a surplus of certain goods to keep for the next year to trade with another planet with insufficient amounts of those goods, enabling that planet to survive; unfortunately, the ship may decide incorrectly about whether the next planet picked by the ship needs those traded goods.  Ships do not mine or grow resources and staff; to survive they must get all staff and resources from planets by trading.  And each year they need to add size to keep up with the growing size of the planet trading partners.  Ships and planets may sometimes survive one or two years without a successful trade. There are some statistics which show how planets and ships without trades grow and survive.\n"
          + "\n"
          + "The planets and ships are economies with mostly similar rules and some very different priority settings. Each economy has 2 stressed financial sectors, 2 very successful financial sectors, and 3 other ok financial sectors. Of course each unit represents a large amount of the item.  \n"
          + "\n"
          + "Each economy has assets resources: working SubAsset Resource and reserved SubAsset Cargo, assets staff: working SubAsset.Staff and reserved SubAsset Guests, in addition each financial sector has knowledge: commonKnowledges (everyone may have it), newKnowledge (found by staff in the researchEquivalent), manuals (about parts of common knowledge).\n"
          + "\n"
          + "Each year ships select a relatively close planet to trade excess resources and staff for needed resources and staff.  At the end of each year all of the costs are calculated.  Costs from each financial sector are subtracted from the balance of each sector.    Costs are increased by poor health .  Health is poor if there is a limited amount of assets available at one or more financial sectors. Each year there are required infrastructure growth and maintenance costs necessary to survive the year but do not decrease the resources and staff. Then the yearly costs for maintenance and travel are calculated and subtracted.  Any remaining assets can be used for sector growth.  \n"
          + "\n"
          + "New staff are added during growth to the lowest staff grade while many of the staff move up one or more grades, and new knowledge is discovered by the research equivalent staff.  Engineer equivalent staff perform the work that is needed to increase resources.  Faculty equivalent staff are needed for staff to advance grades.  Researcher equivalent staff discover new knowledge which makes years more efficient, decreasing the percentage costs of maintenance, travel and growth.\n"
          + "\n"
          + "After a set of years are finished, the display switches to the “stats” tab.  Each row has 11 columns, a longer column with a short title of the line, than either 5 or 10 values.  If you click on the title, a longer description of the column will appear in the opening above the table columns.  If only the right 5 columns are numbers, each number represents the sum of the ship and planet values for that clan.  If the numbers are too large to fit in the column, they are reduced by some number of tens, and the title will say how many zeros to add before the decimal point.  If the title ends in “thisYr”, the values are for the current year.  If the title ends in “cur/1” the values are for the current year, “cur/2” is for the previous year.  If the title ends in “cum”, this is a cumulative value, the sum of all the years.  If the year value has a “U” appended, then the numbers are the number of times the value was saved in this year or since starting the game with “cum”.  If the title year value has “ave” appended then values are the average, the values divided by the number of times added.  If % is appended to the title with “ave” or just the title, it means the numbers are a percent.  If neither “ave” or “U” is appended, the number is the sum of saved values for the year.  If you put a reason in box to the right of the remember button, then click the remember button, that row, the description and the reason will be added to the “keep” file.\n"
          + "\n"
          + "There are 21 buttons with numbers. If you hover over a number a popup will list the description of the results for the number.  Click the number to fill the result table with results for that description.  Some of the pages have results grouped for different ages so you can see if values change as planets and ships age.\n"
          + "\n"
          + "There are instructions at the end of this document to describe how to get the files you need to run this game and even how you can use a program called “apache-netbeans” to edit source files and change the guts of the game yourself and build new versions of the game to run.\n"
          + "\n"
          + "The game-master changes overall game settings, setting rules for all ships and planets, thus the game-master can set up many different games of economic strategy.   The game-master sets the difficulty of the game, and the amount of random increases or decreases to the priorities,  costs, growth, etc. There are five clans each with a clan-master.  One or more players can divide up the roles of game-master and clan-masters.  Clans without a clan-master run with the preset settings for that clan.  The clan-masters can alter any of the settings and leave unchanged the rest of the clan settings for the robots which calculate the moves for each clan planet and ship.\n"
          + "\n"
          + "You need to download the StarTrader19.40.jar file to a folder such as myGame where you can run it following the instructions near the end of this README.  Your virus protection will possibly say that this is a bad file, or a potentially bad file, or some similar warning.  You need to keep assuring it several times that you trust the file and wish to download and run the file.  The first tab labeled “story” is a copy of this README, click the next tab “Settings” to manage the settings.\n"
          + "\n"
          + "The instructions about the functioning of the game are long and complicated because all of the action is done by robots.  To instruct the robots, you need to understand their activity and the kind of decisions they are making.  Clan-masters change the values about how robots make choices, but neither the game-master or the clan-masters change rules, they only change the values about how decisions are made by the robots.\n"
          + "\n"
          + "Directly under the “story” tab is a button called “master”, this is the tab for the game-master, and the settings shown under it are the settings for the game as a whole.  After each setting name there are one or two sliders for “planets” then “ships.”   If there is only one slider, this setting applies for both planets and ships.  As you run your mouse pointer over each name, the description in the green window below the sliders describes that setting.  The down button takes you to the next group of settings.\n"
          + "\n"
          + "Beside the gray master button are 5 colored tabs named “red”, “orange”, “yellow”, “green”, “blue” for the five clans.  Click one of those tabs, such as “orange”.  The area around the settings turns orange, the settings change to those for the “orange” clan, any changes apply only to the orange clan.\n"
          + "\n"
          + "If settings for one or more of the clans are unchanged, the game will run with the existing settings.  After finishing changes for the game and all clans, the game master can click either the “1 yr” or “5 yr” button to run the game for 1 or 5 years.  Sometimes you need to click again in the middle of the button to get it to take effect.\n"
          + "\n"
          + "While running the game will change to the “display” tab and show some lines that change as the game runs year by year.  The color of the screen becomes the color of that clan of the current ship or planet unless the “haveColors” setting is set to less than 50.  Elapsed time is shown in milliseconds since the start of the game, start of the year, or the start of using an econ.  Each econ name starts with a “P” if a planet or with “S” if a ship,  the letters are followed by 4 digits.  The digits are the number of the created econ.  The word or words at the top left are the name of the year’s state.  A series of lines display the counts of interesting facts about the game for as the game progresses through the years.  Facts such as the number or creations, the number of trades, the number of deaths, the number of current ships and current planets.  The screen is updated around 60 times a second, a single planet or ship econ may be current for up to several seconds.\n"
          + "\n"
          + "The first state is the “future fund create”, planets or ships (economies) are created from funds put in the future fund each year by each clan economy.  Only planets are created until there are enough planets by a game rule and two clan rules, then a clan ship can be created.  By default, clan planets can only trade with as many ships as they allow clan ships.  Putting resources into ships limits the growth in worth of the clan, but it provides the infrastructure to protect planets. \n"
          + "\n"
          + "The next state is “game create”: each year the game creates enough economies to bring the number of planets and ships up to the minimum for the year.  The default number of economies grows for the first six years, then it drops to a low number, new planets or ships will be created by the game in any year where the number of economies falls below the default number..\n"
          + "\n"
          + "The next very quick state is “year start”, ships and planets are readied  for another year.  The state will not usually appear in the display.  This is when catastrophes occur.  They destroy much of the staff of a sector and resources of a sector, but this is also where econs find new resources to replace resources that have been mined.  Every year’s mined resources depreciates the amount of resources that can be mined the next year until no more resources can be mined.  Catastrophes help planets find additional resources, and help ships to develop new knowledge.\n"
          + "\n"
          + "Since ships carry relatively large units of resources compared to planets, assume the ships are of a size like the moon, and perhaps they travel faster than light by jumping between high stress points below the surface of stars.  Ships require large staff to operate, expand and repair the ships because of the stress of the way they travel.\n"
          + "\n"
          + "The next state is “search”.  A limited number of planets that are close enough to the ship are chosen.  Planets that have already traded are eliminated unless there is a surplus of ships for planets of this clan.  Each planet and ship keeps a trade history; these histories are updated at each trade so that the planets can be selected by their search history.  The assumption is that ships cannot have real time access to the current trade possibilities of any of the trading candidates.\n"
          + "\n"
          + "The next state is a trade.  These are “potlatch” trades since the ship and planet do not have a common currency.  They need to evaluate each of their financial sectors of resources and staff to determine what they need the most, or what sectors have the highest strategic value, and which have the lowest strategic value.  Each partner tries to trade low strategic value goods for high strategic value goods.   Of course the trading partner may need some of your high value goods, not some of your low value goods, so at each turn the offers are changed to satisfy your own needs with goods you hope the partner will accept.  Each partner gets up to nine turns; a partner may reject a trade if the offers are too unsatisfactory by changing the turn number to -1.  A trade is accepted if both partners can accept an offer without trying to change the offer, the turn number is set to zero, the goods (cargo and guests) in the offer are actually moved between economies, then the trade is recorded as accepted.  Trades can also be “rejected” by one partner, and is then “lost” by the other partner.  If there are more ships than planets, multiple ships can attempt to trade with a given planet.  Multiple ships on a planet can also attempt to trade with each other.  \n"
          + "\n"
          + "Planets and ships start trading with a profit goal.  The profit goals for a clan's ships and planets can be changed by the clan-master.  These goals are later changed by the “favor” of the trading partner, and the history of trades, rejects and lost trades experienced by the clan.\n"
          + "\n"
          + "After all the ships had an opportunity to trade, the next state is “endYear”.  Since endYear’s do not involve any other economy, multiple endYears can run at once.  Initially multiple cpus can run multiple threads to do endYears.  The number of threads can be changed in the settings. \n"
          + "\n"
          + "During the endYear resources and staff may be swapped between working and reserved, reserved cost less, but do not provide any work.  In addition resources and staff may be repurposed, that is they are moved to a different financial sector.  This is a very costly operation that is only used if trading does not supply some of the critically needed resources or staff.  After each swap, a test is done to see if it generated an overall benefit, if not the swap may be redone several times.  During the swaps, emergency actions may donate to the “future funds” some resources or staff from high cost sectors which have too many units in relation to the other sectors.  This reduces the costs for the sectors with few units.\n"
          + "\n"
          + "Each financial sector incurs costs from each of the other sectors.  If at the end of the swaps, one or more of the sectors cannot pay the yearly costs or has insufficient infrastructure to survive the whole year then the economy of a ship or planet dies.  All of its staff and resources are lost.  Otherwise at the end of the year, when enough resources and staff are available, growth is applied to resources, staff and knowledge.  \n"
          + "\n"
          + "As knowledge increases, years become more efficient and costs decrease.  Each year the research equivalent set of staff find new knowledge, they also convert manuals received in trades into more common knowledge.  After a year, new knowledge becomes common knowledge.\n"
          + "\n"
          + "After each run a large set of statistics is available to be viewed about the planets and ships,  At some later time another ship or planet will be established at the same location. By default, between 10% to 20% of planet and ships die each year.\n"
          + "\n"
          + "At the end of the years the window should change to the “stats” tab.  There are 21 buttons for 21 different views of the statistics about what happened with the clan finances.  There are also buttons to run the game for 1 or 5 or 10 or 20 years.  When they are clicked the screen goes back to the display tab.\n"
          + "\n"
          + "After you have become familiar with the game by running it several times you can “keep” some of the settings you change, so that they will be automatically set to the kept value in the following games.  You can also write comments to indicate why you made the changes.  You keep settings by clicking the “keep” button, then any changes you have made on the current settings page will be kept when you leave that page.  These kept values are in a file called “keep”.  You can find the “keep” file in the same folder into which you copied the java .jar or .exe file.  You can also use the “remember” button on the statistics pages to remember a line whose title you click.  \n"
          + "Runs can be 1 year, 5 years, 10 or 20 years.  Initial difficulty settings make it so that between 7% to 20% of the economies die each year.  Statistics after each run help show the problems the ship and planets failed and may give some ideas about changing clan priorities to increase planet and ship survival. \n"
          + "\n"
          + "If too many or not enough economies fail each year, the game master can change a game difficulty setting to alter results. After viewing their statistics each clan-master can change a few settings, then the game-master starts another run.  It is a good strategy to only change a few settings at each new game, keep the settings and a comment about why you changed the setting, you may also want to “remember” some results that suggested the change in settings.\n"
          + "\n"
          + "The game-master changes settings about how all planets and ships survive and grow.  Also the game-master can adjust how the winning score is calculated and when the score is good enough to win the game.  “Save the planets”  increases the score of a clan based on the number of planets and ships that the clan's barters have helped, and how much the barters helped.  The game-master can change settings so that  “Highest worth wins”, or “most planets wins” or many other games.  You can play the game for as many runs as you choose. \n"
          + "\n"
          + "At the start of the year each economy projects what its resources will be at the end of the year. Each resource and staff sector is given a strategic value  related to how much more is needed for a good year.  In addition, if a planet or ship might not survive the year with the current resources and staff, an SOS flag is set.  Each year each ship tries to find a planet for a good trade in a way that both of them will be more able to survive and have good growth.  The game-master can adjust how much the ship knows about planets. Ships may know nothing and just make a random choice.  Ships may be able to use a history that is updated every time they trade with a planet,  Ships may be able to get direct knowledge about what planets have to make the best trade, combined with the cost of travel to that planet.\n"
          + "\n"
          + "Clan-masters choose a friendship level with each of the other clans.  The higher the friendship, the better trade will be given the ship and planet.  Ships can trade with each other, if more than one ship is trading at a given planet.  \n"
          + "\n"
          + "Each year there are costs for simply living: “maintenance”, communicating and moving between parts of the economy or between planets: “travel”, and hopefully increasing resources, staff and knowledge: “growth”. When a planet or ship has enough to survive the year,  the additional resources determine the health of the planet or ship.  Planets and ships with poor health are less efficient in doing the required work for that year.  This means that life and growth take more resources and staff and accomplishes less.  The work becomes more efficient for each sector each year as the knowledge for that sector increases.  \n"
          + "\n"
          + "When there are more resources and/or staff than needed for maintenance and travel, those resources and work can be applied to growth in that sector.  Each of the required  or infrastructure Maintenance and required or infrastructure Growth require a combination of resources and staff, just as growth requires a combination of resources and staff.  Every year each financial sector will only be able to do the amount of work enabled by the required combination of resources and staff, there will be some resources or staff for each sector which cannot be used.\n"
          + "\n"
          + "The resources and staff subAssets for each sector are working resources and staff.   Cargo and guest subAssets are the reserved resources and staff, they do no work and their yearly costs are reduced, they are available to convert to working assets or to be traded.\n"
          + "\n"
          + "Each year during the Cash Flow activity, the potential costs for each financial sector are calculated.  Any sectors with insufficient prospective resources or work to meet the required costs, must move any available reserves to working status.  If that is not enough assets from other sectors may be repurposed.  Of course the cost of such repurposing is quite high, taking well over 10 times the resulting increase in assets for the needy sector.\n"
          + "\n"
          + "The game-master sets a number of priorities and values over the game for all the clans.  The game-master adjusts the difficulty of the game and other options that significantly change the nature of challenges in the game.  A normal goal is to have no more than 10% of ships and 10% of planets die in a single year of the game.  Catastrophes can occur at the year start before it is time to trade.  A catastrophe can destroy a large fraction of the resources for one or two financial sectors, and the staff for a financial sector, but they also discover additional resources for one or more sectors.  These additional resources replace the decay of resources as they are mined\n"
          + "\n"
          + "The game-master chooses which results are most important for deciding the winner in the game.  The winner may be the clan that gave the most help to clans, or that helped the most planets or the most planets and ships, or had the highest worth, or had the most planets, etc.  The winner may change after each run of one or more years.  \n"
          + "\n"
          + "After players have set clan priorities and other levels, the game-master can run the game for 1,5,10, or 20 years.  When the years are finished, results are available, there are 20 different pages showing different results and showing some of the same results in different ways.  Pages 0,1,2 list the most important results.  Any page with the score at the top will be set to the background color of the clan with the highest score, the winner when the score gets good enough to win.\n"
          + "\n"
          + "Each planet and ship have seven financial sectors.  Each sector has resources, cargo(resource but in reserve not working), staff, guests(staff but in reserve not working), and knowledge of 3 kinds (common knowledge, new knowledge, and manuals(researchers work to get new knowledge or common knowledge from manuals), but trades can only trade manuals from ships).\n"
          + "\n"
          + "The game is available in a folder at: \n"
          + "https://drive.google.com/drive/folders/1P-hw8Wk9BcwEdHSS8CdAbDDjtjyEQTF_?usp=sharing.  The folder contains a pdf of this README and the file StarTrader.jar.  \n"
          + "\n"
          + "You need to download the StarTraderMaven-19.40.jar file to a place where you can run it following the instructions near the end of this README. First make a new folder in the downloads folder, with the name myGame.  Download the StarTraderMaven-19.40.jar to the folder downloads\\myGame.  You must download to a folder that is not part of a streaming memory such as GoogleDrive, the jar file gets stuck trying to run on a streaming memory.  Double click on the StarTraderMaven-19.40.jar file in myGame to run the game.  Check for new subversions of the game at least once a month.  Each copy of  the newly downloaded file has a version.subversion added to the name.  Delete unwanted files by right clicking the file and choosing the delete option. The .jar file will only run if you have a java jre 1.8 installed.  Use https://www.java.com/download/ie_manual.jsp to download the latest java jre (java runtime environment).  This decodes the java.jar file into instructions that run within windows. s You will need to accept a license saying that you are not a commercial company.\n"
          + "StarTrader is set up to run on a Windows 10 machine with at least an 11 or 12 inch screen.  It will probably run on other desktop or laptops, if they will also have a current Java.  Instructions for running the game are given much earlier in the README.\n"
          + "\n"
          + "Ignore the following instructions unless you want to try to change the guts of the game.\n"
          + "The source of the game is in the folder you downloaded, go to src.java.trade. (all the source files)\n"
          + "I use Apache Netbeans, the latest version, and the latest java 1.8 version of java.  The java being automatically distributed to Windows 11 machines by Oracle is the latest version of java.1.8 jre.  You can download a corresponding JDK from Oracle after signing their license. \n"
          + "Here are many of the settings you will need  in Apache NetBeans currently version 12.4.  You must be logged in as a windows administrator to have the windows permissions to install NetBeans.  \n"
          + "\n"
          + "Once you have installed NetBeans and downloaded the source files:\n"
          + "1.Left Click the NetBeans Tools menu and select Java Platforms, click \"Add Platform...\" browse to C:\\Program Files\\Java and choose the latest installed jdk1.8.0_xxx \n"
          + "2.Open NetBeans IDE and create a new ant project with the existing source\n"
          + "3.Create a source folder such as C:\\Users\\Public\\netbeans\\Trader19.xx\n"
          + "4.Right click the project name, at versioning create a git repository in Trader19.xx\n"
          + "5.Right click the project name, at git select pull and fill out the form as requested\n"
          + "6.Right click the project name, select properties, select formatting, select project specific options, choose all languages, choose Tabs And Indents, Enable Indentation, Expand Tabs to Spaces, Number of Spaces per indent=2, tab Size = 2, Right Margin=80,Line Wrap After words\n"
          + "7.Download the latest Java 8 JDK after signing the license, install it into the Java folder under C:\\Program Files\n"
          + "8.Right click the project name, select properties, and under “Source Packages” select the package “trade” to get a list of the Java Classes.  The StarTrader.java source contains the ‘main’ method with the user interface logic.  Classes E.java and EM.java contain lots of data tables needed for the user interface, E.java contains most of the fixed data, EM.java contains data that can change from the user settings changes and the statistics of the current run of the game, EM.java also contains methods for processing settings and statistics."
          + "";

  static int iii = 0;
  // The following is a list of states
  static final int CONSTRUCTING = 0;
  static final String sn0 = "Constructing";
  static final int CONSTRUCTED = 1;
  static final String sn1 = "Constructed";
  static final int RUNNING = 2; // start list of constants
  static final String sn2 = "Running";
  static final int STARTING = 3; // start list of constants
  static final String sn3 = "Starting";
  static final int CREATING = 4;
  static final String sn4 = "Creating";
  static final int FUTUREFUNDCREATE = 5;
  static final String sn5 = "CreatingFutureF";
  static final int STARTYR = 6;
  static final String sn6 = "StartYear";
  static final int SEARCH = 7;
  static final String sn7 = "Search to Trade";
  static final int TRADING = 8;
  static final String sn8 = "Trading";
  static final int DOYEAREND = 9;
  static final String sn9 = "Doing year end";
  static final int WAITING = 10;
  static final String sn10 = "Waiting for action";
  static final int SWAPS = 11;
  static final String sn11 = "Swaping";
  static final int ENDYR = 12;
  static final String sn12 = "End of doYear";
  static final int STATS = 13;
  static final String sn13 = "Stats";
  static final int STOPPED = 14;
  static final String sn14 = "Stopped";
  static final int RUNSDONE = 15;
  static final String sn15 = "Runs Done";
  static final int FATALERR = 16;
  static final String sn16 = "Fatal Error";

  static final String[] stateStringNames = {sn0, sn1, sn2, sn3, sn4, sn5, sn6, sn7, sn8, sn9, sn10, sn11, sn12, sn13, sn14, sn15, sn16};
  static volatile int stateConst = CONSTRUCTING;  // constant set to stated
  static volatile int prevState = CONSTRUCTING;
  static volatile Econ curEc = EM.curEcon;
  static volatile String curStateName = stateStringNames[0];
  static volatile String prevEconName = "no name";
  static volatile String curEconName = "no econ";
  static boolean doStop = false; // set by game or stats stop execution
  static boolean fatalError = false;
  static volatile int stateCnt = -11;
  static volatile int yearsToRun = -22;
  // static int econCnt = -5;
  static volatile int sameEconState = -21;
  static int blip = 1000 / 60;  // shortest animation interval 60/ second
  static int blip2 = blip * 2;  //  30/second
  static int blip5 = blip * 5;
  static int blip10 = blip * 10; // 6/second
  static int blip30 = blip * 30; // 2/second
  static volatile public long startTime = (new Date()).getTime();
  static volatile public long startYear = startTime;
  static volatile public long startRY2 = startTime;
  static volatile public long startRY3 = startTime;
  static volatile public long startEconState = startTime;
  static volatile public long aTime = startTime; // runYears2 before for stateCnt
  static volatile public long aaTime = startTime; // runBackGroundYears4 at start runYear
  static volatile public long totMem, freeMem, usedMem;
  static public String stNi = "stNi";
  static public String ecNi = "ecNi";
  static public String asNi = "asNi";
  static public String cfNi = "cfNi";  // CashFlow method
  static public String cfNi2 = "cfNi2"; // within CashFlow method

  // public Star(int group,int contextV,String xname, int xpos, int ypos, int wealth, int colonists, double difficulty,
  // static protected Econ env  = new Econ("able",0,0,5,5,5,1000,1000,100,50.,
  //   "struct",30.,"energy",25.,"life",5.,"defense",10.,"colonist",25.);
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    logButtonGroup1or2 = new javax.swing.ButtonGroup();
    initButtonGroupPorS = new javax.swing.ButtonGroup();
    clanButtonGroupActiveClan = new javax.swing.ButtonGroup();
    statsButtonGroupReportNumber = new javax.swing.ButtonGroup();
    statsButtonGroupClans = new javax.swing.ButtonGroup();
    logBGactions = new javax.swing.ButtonGroup();
    gameButtonGroup = new javax.swing.ButtonGroup();
    gameButtonUp = new java.awt.Button();
    statsField2 = new javax.swing.JTextField();
    controlPanels = new javax.swing.JTabbedPane();
    story = new javax.swing.JPanel();
    storyTextPane = new javax.swing.JScrollPane();
    storyTextField1 = new javax.swing.JTextArea();
    game = new javax.swing.JPanel();
    gameMaster = new javax.swing.JRadioButton();
    clanRed = new javax.swing.JRadioButton();
    clanOrange = new javax.swing.JRadioButton();
    clanYellow = new javax.swing.JRadioButton();
    clanGreen = new javax.swing.JRadioButton();
    clanBlue = new javax.swing.JRadioButton();
    gameXtraPanel1 = new javax.swing.JPanel();
    gameCtlButtonRun1Year1 = new javax.swing.JButton();
    gameCtlButtonRun5Years1 = new javax.swing.JButton();
    settingsKeep = new javax.swing.JButton();
    settingsStop = new javax.swing.JButton();
    gameToLabelPlanet = new javax.swing.JLabel();
    gameTopLabelShip = new javax.swing.JLabel();
    gameTopRightFill = new javax.swing.JTextField();
    gamePanel0 = new javax.swing.JPanel();
    gameTextField0 = new javax.swing.JTextField();
    gameSliderP0 = new javax.swing.JSlider();
    jSeparator1 = new javax.swing.JSeparator();
    gameSliderS0 = new javax.swing.JSlider();
    gameButtonUp1 = new java.awt.Button();
    gamePanel1 = new javax.swing.JPanel();
    gameTextField1 = new javax.swing.JTextField();
    gameSliderP1 = new javax.swing.JSlider();
    jSeparator2 = new javax.swing.JSeparator();
    gameSliderS1 = new javax.swing.JSlider();
    gamePanel2 = new javax.swing.JPanel();
    gameTextField2 = new javax.swing.JTextField();
    gameSliderP2 = new javax.swing.JSlider();
    jSeparator3 = new javax.swing.JSeparator();
    gameSliderS2 = new javax.swing.JSlider();
    gamePanel3 = new javax.swing.JPanel();
    gameTextField3 = new javax.swing.JTextField();
    gameSliderP3 = new javax.swing.JSlider();
    jSeparator4 = new javax.swing.JSeparator();
    gameSliderS3 = new javax.swing.JSlider();
    gamePanel4 = new javax.swing.JPanel();
    gameTextField4 = new javax.swing.JTextField();
    gameSliderP4 = new javax.swing.JSlider();
    jSeparator5 = new javax.swing.JSeparator();
    gameSliderS4 = new javax.swing.JSlider();
    gamePanel5 = new javax.swing.JPanel();
    gameTextField5 = new javax.swing.JTextField();
    gameSliderP5 = new javax.swing.JSlider();
    jSeparator11 = new javax.swing.JSeparator();
    gameSliderS5 = new javax.swing.JSlider();
    gamePanel6 = new javax.swing.JPanel();
    gameTextField6 = new javax.swing.JTextField();
    gameSliderP6 = new javax.swing.JSlider();
    jSeparator13 = new javax.swing.JSeparator();
    gameSliderS6 = new javax.swing.JSlider();
    gamePanel7 = new javax.swing.JPanel();
    gameTextField7 = new javax.swing.JTextField();
    gameSliderP7 = new javax.swing.JSlider();
    jSeparator14 = new javax.swing.JSeparator();
    gameSliderS7 = new javax.swing.JSlider();
    gamePanel8 = new javax.swing.JPanel();
    gameTextField8 = new javax.swing.JTextField();
    gameSliderP8 = new javax.swing.JSlider();
    jSeparator15 = new javax.swing.JSeparator();
    gameSliderS8 = new javax.swing.JSlider();
    gamePanel9 = new javax.swing.JPanel();
    gameTextField9 = new javax.swing.JTextField();
    gameSliderP9 = new javax.swing.JSlider();
    jSeparator16 = new javax.swing.JSeparator();
    gameSliderS9 = new javax.swing.JSlider();
    gamePanelBottomPanel = new javax.swing.JPanel();
    gameTextPane = new javax.swing.JScrollPane();
    gameTextField = new javax.swing.JTextArea();
    jScrollPane3 = new javax.swing.JScrollPane();
    settingsComment = new javax.swing.JTextArea();
    gameButtonDown = new java.awt.Button();
    clan = new javax.swing.JPanel();
    clanTextPane = new javax.swing.JScrollPane();
    clanTextField = new javax.swing.JTextArea();
    clanPanel0 = new javax.swing.JPanel();
    clanTextField0 = new javax.swing.JTextField();
    gameLabelP5 = new javax.swing.JLabel();
    clanSliderP0 = new javax.swing.JSlider();
    jSeparator6 = new javax.swing.JSeparator();
    gameLabelS5 = new javax.swing.JLabel();
    clanSliderS0 = new javax.swing.JSlider();
    clanPanel1 = new javax.swing.JPanel();
    clanTextField1 = new javax.swing.JTextField();
    gameLabelP6 = new javax.swing.JLabel();
    clanSliderP1 = new javax.swing.JSlider();
    jSeparator7 = new javax.swing.JSeparator();
    gameLabelS6 = new javax.swing.JLabel();
    clanSliderS1 = new javax.swing.JSlider();
    clanPanel2 = new javax.swing.JPanel();
    clanTextField2 = new javax.swing.JTextField();
    gameLabelP7 = new javax.swing.JLabel();
    clanSliderP2 = new javax.swing.JSlider();
    jSeparator8 = new javax.swing.JSeparator();
    gameLabelS7 = new javax.swing.JLabel();
    clanSliderS2 = new javax.swing.JSlider();
    clanPanel3 = new javax.swing.JPanel();
    clanTextField3 = new javax.swing.JTextField();
    clanLabelP3 = new javax.swing.JLabel();
    clanSliderP3 = new javax.swing.JSlider();
    jSeparator9 = new javax.swing.JSeparator();
    gameLabelS8 = new javax.swing.JLabel();
    clanSliderS3 = new javax.swing.JSlider();
    clanPanel4 = new javax.swing.JPanel();
    clanTextField4 = new javax.swing.JTextField();
    clanLabelP4 = new javax.swing.JLabel();
    clanSliderP4 = new javax.swing.JSlider();
    jSeparator10 = new javax.swing.JSeparator();
    clanLabelS4 = new javax.swing.JLabel();
    clanSliderS4 = new javax.swing.JSlider();
    display = new javax.swing.JPanel();
    displayPanel0 = new javax.swing.JPanel();
    displayPanel0Text = new javax.swing.JTextArea();
    displayPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jTable1 = new javax.swing.JTable();
    stats = new javax.swing.JPanel();
    topPane = new javax.swing.JPanel();
    rbuttons12 = new javax.swing.JPanel();
    rbuttons1 = new javax.swing.JPanel();
    statsButton0 = new javax.swing.JRadioButton();
    statsButton1 = new javax.swing.JRadioButton();
    statsButton2 = new javax.swing.JRadioButton();
    statsButton3 = new javax.swing.JRadioButton();
    statsButton4 = new javax.swing.JRadioButton();
    statsButton5 = new javax.swing.JRadioButton();
    statsButton6 = new javax.swing.JRadioButton();
    statsButton7 = new javax.swing.JRadioButton();
    statsButton8 = new javax.swing.JRadioButton();
    statsButton9 = new javax.swing.JRadioButton();
    statsButton10 = new javax.swing.JRadioButton();
    rbuttons2 = new javax.swing.JPanel();
    statsButton11 = new javax.swing.JRadioButton();
    statsButton12 = new javax.swing.JRadioButton();
    statsButton13 = new javax.swing.JRadioButton();
    statsButton14 = new javax.swing.JRadioButton();
    statsButton15 = new javax.swing.JRadioButton();
    statsButton16 = new javax.swing.JRadioButton();
    statsButton17 = new javax.swing.JRadioButton();
    statsButton18 = new javax.swing.JRadioButton();
    statsButton19 = new javax.swing.JRadioButton();
    statsButton20 = new javax.swing.JRadioButton();
    ybuttons = new javax.swing.JPanel();
    statsCtlButtonRun1Yr = new javax.swing.JButton();
    statsCtlButtonRun10Yr = new javax.swing.JButton();
    statsCtlButtonRun5Yr = new javax.swing.JButton();
    statsCtlButtonRun20Yr = new javax.swing.JButton();
    bigPanel = new javax.swing.JPanel();
    statsStop = new javax.swing.JButton();
    remember = new javax.swing.JButton();
    statsCommentPane = new javax.swing.JScrollPane();
    statsRememberWhy = new javax.swing.JTextArea();
    statsField = new javax.swing.JTextField();
    statsScrollPane2 = new javax.swing.JScrollPane();
    statsTable1 = new javax.swing.JTable();
    javax.swing.JPanel log = new javax.swing.JPanel();
    logTableScrollPanel = new javax.swing.JScrollPane();
    logDisplayTable = new javax.swing.JTable();
    logDlevel2 = new javax.swing.JLabel();
    LogDlen1Slider = new javax.swing.JSlider();
    logDlen1 = new javax.swing.JLabel();
    logDLevel1Slider = new javax.swing.JSlider();
    SpinnerModel startModel1 = new SpinnerNumberModel(10,
      0, //min
      2000000, //max
      10);
    logM1Spinner = new javax.swing.JSpinner(startModel1);
    LogDLen2Slider = new javax.swing.JSlider();
    logDLevel2Slider = new javax.swing.JSlider();
    logDlen2 = new javax.swing.JLabel();
    logDlevel1 = new javax.swing.JLabel();
    SpinnerModel startModel2 = new SpinnerNumberModel(10,
      0, //min
      2000000, //max
      10);
    logM2Spinner = new javax.swing.JSpinner(startModel2);
    logNamesScrollPanel = new javax.swing.JScrollPane();
    namesList = new DefaultListModel();
    logEnvirnNamesList = new javax.swing.JList(namesList);
    Start1Name = new javax.swing.JLabel();
    Start2Name = new javax.swing.JLabel();
    logRadioButtonStart1 = new javax.swing.JRadioButton();
    logRadioButtonStart2 = new javax.swing.JRadioButton();
    logActionJump = new javax.swing.JRadioButton();
    logActionAdd = new javax.swing.JRadioButton();
    logActionDel = new javax.swing.JRadioButton();

    gameButtonUp.setLabel("up");
    gameButtonUp.setMaximumSize(new java.awt.Dimension(70, 55));
    gameButtonUp.setMinimumSize(new java.awt.Dimension(30, 45));

    statsField2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsField2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    statsField2.setText("jTextField1");
    statsField2.setToolTipText("");
    statsField2.setMinimumSize(new java.awt.Dimension(200, 30));
    statsField2.setPreferredSize(new java.awt.Dimension(400, 30));
    statsField2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsField2ActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setAlwaysOnTop(true);
    setBackground(new java.awt.Color(240, 180, 240));
    setBounds(new java.awt.Rectangle(0, 0, 1250, 1250));
    setMaximizedBounds(new java.awt.Rectangle(0, 0, 1, 0));
    setMinimumSize(new java.awt.Dimension(800, 600));
    setResizable(false);
    addInputMethodListener(new java.awt.event.InputMethodListener() {
      public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
        formCaretPositionChanged(evt);
      }
      public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        formInputMethodTextChanged(evt);
      }
    });
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        formWindowClosed(evt);
      }
    });
    getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    controlPanels.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
    controlPanels.setAutoscrolls(true);
    controlPanels.setDebugGraphicsOptions(javax.swing.DebugGraphics.BUFFERED_OPTION);
    controlPanels.setDoubleBuffered(true);
    controlPanels.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    controlPanels.setMaximumSize(new java.awt.Dimension(1800, 1200));
    controlPanels.setMinimumSize(new java.awt.Dimension(500, 450));
    controlPanels.setOpaque(true);
    controlPanels.setPreferredSize(new java.awt.Dimension(1200, 800));
    controlPanels.setBackground(bg1);
    controlPanels.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        controlPanelsStateChanged(evt);
      }
    });
    controlPanels.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        controlPanelsPropertyChange(evt);
      }
    });

    story.setAlignmentX(0.0F);
    story.setAlignmentY(0.0F);
    story.setAutoscrolls(true);
    story.setMaximumSize(new java.awt.Dimension(1200, 1500));
    story.setMinimumSize(new java.awt.Dimension(600, 400));
    story.setName(""); // NOI18N
    story.setPreferredSize(new java.awt.Dimension(1000, 700));

    storyTextPane.setAutoscrolls(true);
    storyTextPane.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
    storyTextPane.setMaximumSize(new java.awt.Dimension(1200, 1800));
    storyTextPane.setMinimumSize(new java.awt.Dimension(400, 500));
    storyTextPane.setPreferredSize(new java.awt.Dimension(1200, 800));

    storyTextField1.setEditable(false);
    storyTextField1.setColumns(2000);
    storyTextField1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
    storyTextField1.setLineWrap(true);
    storyTextField1.setRows(30);
    storyTextField1.setWrapStyleWord(true);
    storyTextField1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    storyTextField1.setMargin(new java.awt.Insets(4, 5, 4, 4));
    storyTextField1.setMaximumSize(new java.awt.Dimension(1200, 1600));
    storyTextField1.setMinimumSize(new java.awt.Dimension(600, 400));
    storyTextField1.setRequestFocusEnabled(false);
    storyTextPane.setViewportView(storyTextField1);
    storyTextField1.getAccessibleContext().setAccessibleParent(storyTextPane);

    javax.swing.GroupLayout storyLayout = new javax.swing.GroupLayout(story);
    story.setLayout(storyLayout);
    storyLayout.setHorizontalGroup(
      storyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(storyTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );
    storyLayout.setVerticalGroup(
      storyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(storyTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    controlPanels.addTab("story", story);

    game.setBackground(new java.awt.Color(255, 255, 255));
    game.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    game.setAlignmentX(0.0F);
    game.setAlignmentY(0.0F);
    game.setAutoscrolls(true);
    game.setMaximumSize(new java.awt.Dimension(1200, 1200));
    game.setMinimumSize(new java.awt.Dimension(800, 600));
    game.setName("Settings"); // NOI18N
    game.setPreferredSize(new java.awt.Dimension(900, 900));
    game.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        gameComponentShown(evt);
      }
    });
    game.setLayout(new java.awt.GridBagLayout());

    gameMaster.setBackground(new java.awt.Color(204, 204, 204));
    gameButtonGroup.add(gameMaster);
    gameMaster.setForeground(new java.awt.Color(102, 102, 102));
    gameMaster.setText("master");
    gameMaster.setToolTipText("");
    gameMaster.setAlignmentY(0.0F);
    gameMaster.setIconTextGap(1);
    gameMaster.setMargin(new java.awt.Insets(1, 1, 12, 1));
    gameMaster.setMaximumSize(new java.awt.Dimension(150, 25));
    gameMaster.setMinimumSize(new java.awt.Dimension(30, 20));
    gameMaster.setPreferredSize(new java.awt.Dimension(65, 21));
    gameMaster.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        gameMasterItemStateChanged(evt);
      }
    });
    gameMaster.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameMasterMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameMasterMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gameMaster, gridBagConstraints);

    clanRed.setBackground(new java.awt.Color(255, 153, 153));
    gameButtonGroup.add(clanRed);
    clanRed.setForeground(new java.awt.Color(153, 0, 0));
    clanRed.setText("red");
    clanRed.setAlignmentY(0.0F);
    clanRed.setIconTextGap(1);
    clanRed.setMargin(new java.awt.Insets(1, 1, 1, 1));
    clanRed.setMaximumSize(new java.awt.Dimension(70, 50));
    clanRed.setMinimumSize(new java.awt.Dimension(30, 21));
    clanRed.setPreferredSize(new java.awt.Dimension(50, 23));
    clanRed.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    clanRed.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    clanRed.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanRedItemStateChanged(evt);
      }
    });
    clanRed.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanRedMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanRedMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    game.add(clanRed, gridBagConstraints);

    clanOrange.setBackground(new java.awt.Color(204, 153, 0));
    gameButtonGroup.add(clanOrange);
    clanOrange.setForeground(new java.awt.Color(102, 51, 0));
    clanOrange.setText("orange");
    clanOrange.setAlignmentY(0.0F);
    clanOrange.setIconTextGap(1);
    clanOrange.setMargin(new java.awt.Insets(1, 1, 1, 1));
    clanOrange.setMaximumSize(new java.awt.Dimension(120, 25));
    clanOrange.setMinimumSize(new java.awt.Dimension(30, 21));
    clanOrange.setPreferredSize(new java.awt.Dimension(65, 21));
    clanOrange.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    clanOrange.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    clanOrange.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanOrangeItemStateChanged(evt);
      }
    });
    clanOrange.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanOrangeMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanOrangeMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    game.add(clanOrange, gridBagConstraints);

    clanYellow.setBackground(new java.awt.Color(255, 255, 51));
    gameButtonGroup.add(clanYellow);
    clanYellow.setText("yellow");
    clanYellow.setAlignmentY(0.0F);
    clanYellow.setIconTextGap(1);
    clanYellow.setMargin(new java.awt.Insets(1, 1, 1, 1));
    clanYellow.setMaximumSize(new java.awt.Dimension(100, 25));
    clanYellow.setPreferredSize(new java.awt.Dimension(65, 23));
    clanYellow.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    clanYellow.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    clanYellow.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanYellowItemStateChanged(evt);
      }
    });
    clanYellow.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanYellowMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanYellowMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(clanYellow, gridBagConstraints);

    clanGreen.setBackground(new java.awt.Color(0, 255, 0));
    gameButtonGroup.add(clanGreen);
    clanGreen.setText("green");
    clanGreen.setIconTextGap(1);
    clanGreen.setMargin(new java.awt.Insets(1, 1, 1, 1));
    clanGreen.setMaximumSize(new java.awt.Dimension(90, 23));
    clanGreen.setMinimumSize(new java.awt.Dimension(30, 23));
    clanGreen.setPreferredSize(new java.awt.Dimension(65, 23));
    clanGreen.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    clanGreen.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    clanGreen.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanGreenItemStateChanged(evt);
      }
    });
    clanGreen.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanGreenMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanGreenMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    game.add(clanGreen, gridBagConstraints);

    clanBlue.setBackground(new java.awt.Color(153, 153, 255));
    gameButtonGroup.add(clanBlue);
    clanBlue.setText("blue");
    clanBlue.setAlignmentY(0.0F);
    clanBlue.setIconTextGap(1);
    clanBlue.setMargin(new java.awt.Insets(1, 1, 1, 1));
    clanBlue.setMaximumSize(new java.awt.Dimension(80, 23));
    clanBlue.setMinimumSize(new java.awt.Dimension(43, 20));
    clanBlue.setPreferredSize(new java.awt.Dimension(55, 23));
    clanBlue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    clanBlue.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    clanBlue.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanBlueItemStateChanged(evt);
      }
    });
    clanBlue.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanBlueMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanBlueMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    game.add(clanBlue, gridBagConstraints);

    gameXtraPanel1.setBackground(new java.awt.Color(153, 255, 255));
    gameXtraPanel1.setMaximumSize(new java.awt.Dimension(55, 21));
    gameXtraPanel1.setMinimumSize(new java.awt.Dimension(0, 21));
    gameXtraPanel1.setPreferredSize(new java.awt.Dimension(0, 21));

    javax.swing.GroupLayout gameXtraPanel1Layout = new javax.swing.GroupLayout(gameXtraPanel1);
    gameXtraPanel1.setLayout(gameXtraPanel1Layout);
    gameXtraPanel1Layout.setHorizontalGroup(
      gameXtraPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    gameXtraPanel1Layout.setVerticalGroup(
      gameXtraPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 21, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    game.add(gameXtraPanel1, gridBagConstraints);

    gameCtlButtonRun1Year1.setText(" 1 yr");
    gameCtlButtonRun1Year1.setAlignmentY(0.0F);
    gameCtlButtonRun1Year1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.black, java.awt.Color.black, null, null));
    gameCtlButtonRun1Year1.setContentAreaFilled(false);
    gameCtlButtonRun1Year1.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
    gameCtlButtonRun1Year1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    gameCtlButtonRun1Year1.setIconTextGap(0);
    gameCtlButtonRun1Year1.setMargin(new java.awt.Insets(1, 1, 1, 1));
    gameCtlButtonRun1Year1.setMaximumSize(new java.awt.Dimension(100, 25));
    gameCtlButtonRun1Year1.setMinimumSize(new java.awt.Dimension(30, 21));
    gameCtlButtonRun1Year1.setName("  1 Yr"); // NOI18N
    gameCtlButtonRun1Year1.setPreferredSize(new java.awt.Dimension(35, 23));
    gameCtlButtonRun1Year1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    gameCtlButtonRun1Year1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    gameCtlButtonRun1Year1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun1Year1MouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun1Year1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun1Year1MouseExited(evt);
      }
    });
    gameCtlButtonRun1Year1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        gameCtlButtonRun1Year1ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
    game.add(gameCtlButtonRun1Year1, gridBagConstraints);

    gameCtlButtonRun5Years1.setText(" 5 yr");
    gameCtlButtonRun5Years1.setAlignmentY(0.0F);
    gameCtlButtonRun5Years1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.red, java.awt.Color.black, null, null));
    gameCtlButtonRun5Years1.setContentAreaFilled(false);
    gameCtlButtonRun5Years1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    gameCtlButtonRun5Years1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    gameCtlButtonRun5Years1.setMargin(new java.awt.Insets(0, 3, 0, 3));
    gameCtlButtonRun5Years1.setMaximumSize(new java.awt.Dimension(100, 25));
    gameCtlButtonRun5Years1.setMinimumSize(new java.awt.Dimension(30, 22));
    gameCtlButtonRun5Years1.setPreferredSize(new java.awt.Dimension(35, 22));
    gameCtlButtonRun5Years1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    gameCtlButtonRun5Years1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun5Years1MouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun5Years1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun5Years1MouseExited(evt);
      }
    });
    game.add(gameCtlButtonRun5Years1, new java.awt.GridBagConstraints());

    settingsKeep.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    settingsKeep.setText("keep");
    settingsKeep.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 102, 51), 2));
    settingsKeep.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
    settingsKeep.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    settingsKeep.setIconTextGap(0);
    settingsKeep.setInheritsPopupMenu(true);
    settingsKeep.setMargin(new java.awt.Insets(0, 0, 0, 0));
    settingsKeep.setMaximumSize(new java.awt.Dimension(100, 25));
    settingsKeep.setPreferredSize(new java.awt.Dimension(50, 23));
    settingsKeep.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        settingsKeepMouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        settingsKeepMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        settingsKeepMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    game.add(settingsKeep, gridBagConstraints);

    settingsStop.setBackground(new java.awt.Color(255, 51, 51));
    settingsStop.setText("stop");
    settingsStop.setAlignmentY(0.0F);
    settingsStop.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
    settingsStop.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    settingsStop.setMargin(new java.awt.Insets(0, 0, 0, 0));
    settingsStop.setMaximumSize(new java.awt.Dimension(100, 25));
    settingsStop.setPreferredSize(new java.awt.Dimension(55, 23));
    settingsStop.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        settingsStopMouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        settingsStopMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        settingsStopMouseExited(evt);
      }
    });
    game.add(settingsStop, new java.awt.GridBagConstraints());

    gameToLabelPlanet.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
    gameToLabelPlanet.setText("Planet");
    gameToLabelPlanet.setMaximumSize(new java.awt.Dimension(500, 40));
    gameToLabelPlanet.setMinimumSize(new java.awt.Dimension(200, 20));
    gameToLabelPlanet.setPreferredSize(new java.awt.Dimension(250, 21));
    game.add(gameToLabelPlanet, new java.awt.GridBagConstraints());

    gameTopLabelShip.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
    gameTopLabelShip.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gameTopLabelShip.setText("ship");
    gameTopLabelShip.setMaximumSize(new java.awt.Dimension(500, 40));
    gameTopLabelShip.setMinimumSize(new java.awt.Dimension(300, 15));
    gameTopLabelShip.setPreferredSize(new java.awt.Dimension(350, 25));
    game.add(gameTopLabelShip, new java.awt.GridBagConstraints());

    gameTopRightFill.setEditable(false);
    gameTopRightFill.setBackground(new java.awt.Color(255, 255, 153));
    gameTopRightFill.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    gameTopRightFill.setAlignmentX(0.0F);
    gameTopRightFill.setAlignmentY(0.0F);
    gameTopRightFill.setMaximumSize(new java.awt.Dimension(600, 25));
    gameTopRightFill.setMinimumSize(new java.awt.Dimension(60, 20));
    gameTopRightFill.setName("gameTopMt"); // NOI18N
    gameTopRightFill.setPreferredSize(new java.awt.Dimension(200, 20));
    gameTopRightFill.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        gameTopRightFillActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    game.add(gameTopRightFill, gridBagConstraints);

    gamePanel0.setAlignmentX(0.1F);
    gamePanel0.setAlignmentY(0.1F);
    gamePanel0.setMaximumSize(new java.awt.Dimension(800, 65));
    gamePanel0.setMinimumSize(new java.awt.Dimension(700, 45));
    gamePanel0.setPreferredSize(new java.awt.Dimension(700, 55));
    gamePanel0.setRequestFocusEnabled(false);
    gamePanel0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel0MouseExited(evt);
      }
    });
    gamePanel0.setLayout(new javax.swing.BoxLayout(gamePanel0, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField0.setEditable(false);
    gameTextField0.setText("tb set");
    gameTextField0.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField0.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField0.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField0.setRequestFocusEnabled(false);
    gameTextField0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField0MouseExited(evt);
      }
    });
    gamePanel0.add(gameTextField0);

    gameSliderP0.setMajorTickSpacing(10);
    gameSliderP0.setMinorTickSpacing(5);
    gameSliderP0.setPaintLabels(true);
    gameSliderP0.setPaintTicks(true);
    gameSliderP0.setSnapToTicks(true);
    gameSliderP0.setToolTipText("Slider1");
    gameSliderP0.setMaximumSize(new java.awt.Dimension(400, 55));
    gameSliderP0.setMinimumSize(new java.awt.Dimension(150, 35));
    gameSliderP0.setName("Slider1"); // NOI18N
    gameSliderP0.setPreferredSize(new java.awt.Dimension(300, 35));
    gameSliderP0.setValueIsAdjusting(true);
    gameSliderP0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP0MouseExited(evt);
      }
    });
    gamePanel0.add(gameSliderP0);
    gameSliderP0.getAccessibleContext().setAccessibleName("Slider1");

    jSeparator1.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator1.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator1.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel0.add(jSeparator1);

    gameSliderS0.setMajorTickSpacing(10);
    gameSliderS0.setMinorTickSpacing(5);
    gameSliderS0.setPaintLabels(true);
    gameSliderS0.setPaintTicks(true);
    gameSliderS0.setSnapToTicks(true);
    gameSliderS0.setToolTipText("hello1");
    gameSliderS0.setMaximumSize(new java.awt.Dimension(450, 45));
    gameSliderS0.setMinimumSize(new java.awt.Dimension(150, 45));
    gameSliderS0.setOpaque(false);
    gameSliderS0.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS0.setValueIsAdjusting(true);
    gameSliderS0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS0MouseExited(evt);
      }
    });
    gamePanel0.add(gameSliderS0);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel0, gridBagConstraints);

    gameButtonUp1.setActionCommand("up");
    gameButtonUp1.setLabel("up");
    gameButtonUp1.setMaximumSize(new java.awt.Dimension(60, 55));
    gameButtonUp1.setMinimumSize(new java.awt.Dimension(30, 45));
    gameButtonUp1.setPreferredSize(new java.awt.Dimension(50, 55));
    gameButtonUp1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameButtonUp1MouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gameButtonUp1, gridBagConstraints);

    gamePanel1.setAlignmentX(0.1F);
    gamePanel1.setAlignmentY(0.1F);
    gamePanel1.setMaximumSize(new java.awt.Dimension(800, 65));
    gamePanel1.setMinimumSize(new java.awt.Dimension(100, 35));
    gamePanel1.setPreferredSize(new java.awt.Dimension(700, 55));
    gamePanel1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel1MouseExited(evt);
      }
    });
    gamePanel1.setLayout(new javax.swing.BoxLayout(gamePanel1, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField1.setEditable(false);
    gameTextField1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    gameTextField1.setText("tb set");
    gameTextField1.setMargin(new java.awt.Insets(0, 0, 0, 0));
    gameTextField1.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField1.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField1.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField1MouseExited(evt);
      }
    });
    gameTextField1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        gameTextField1ActionPerformed(evt);
      }
    });
    gamePanel1.add(gameTextField1);

    gameSliderP1.setMajorTickSpacing(10);
    gameSliderP1.setMinorTickSpacing(5);
    gameSliderP1.setPaintLabels(true);
    gameSliderP1.setPaintTicks(true);
    gameSliderP1.setSnapToTicks(true);
    gameSliderP1.setToolTipText("Slider1");
    gameSliderP1.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP1.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP1.setName("Slider1"); // NOI18N
    gameSliderP1.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP1.setValueIsAdjusting(true);
    gameSliderP1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP1MouseExited(evt);
      }
    });
    gamePanel1.add(gameSliderP1);

    jSeparator2.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator2.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator2.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel1.add(jSeparator2);

    gameSliderS1.setMajorTickSpacing(10);
    gameSliderS1.setMinorTickSpacing(5);
    gameSliderS1.setPaintLabels(true);
    gameSliderS1.setPaintTicks(true);
    gameSliderS1.setSnapToTicks(true);
    gameSliderS1.setToolTipText("hello1");
    gameSliderS1.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS1.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS1.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS1.setValueIsAdjusting(true);
    gameSliderS1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS1MouseExited(evt);
      }
    });
    gamePanel1.add(gameSliderS1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel1, gridBagConstraints);

    gamePanel2.setMaximumSize(new java.awt.Dimension(800, 65));
    gamePanel2.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel2.setPreferredSize(new java.awt.Dimension(750, 55));
    gamePanel2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel2MouseExited(evt);
      }
    });
    gamePanel2.setLayout(new javax.swing.BoxLayout(gamePanel2, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField2.setEditable(false);
    gameTextField2.setText("tb set");
    gameTextField2.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField2.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField2.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField2MouseExited(evt);
      }
    });
    gamePanel2.add(gameTextField2);

    gameSliderP2.setMajorTickSpacing(10);
    gameSliderP2.setMinorTickSpacing(5);
    gameSliderP2.setPaintLabels(true);
    gameSliderP2.setPaintTicks(true);
    gameSliderP2.setSnapToTicks(true);
    gameSliderP2.setToolTipText("Slider1");
    gameSliderP2.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP2.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP2.setName("Slider1"); // NOI18N
    gameSliderP2.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP2.setValueIsAdjusting(true);
    gameSliderP2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP2MouseExited(evt);
      }
    });
    gamePanel2.add(gameSliderP2);

    jSeparator3.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator3.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator3.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel2.add(jSeparator3);

    gameSliderS2.setMajorTickSpacing(10);
    gameSliderS2.setMinorTickSpacing(5);
    gameSliderS2.setPaintLabels(true);
    gameSliderS2.setPaintTicks(true);
    gameSliderS2.setSnapToTicks(true);
    gameSliderS2.setToolTipText("hello1");
    gameSliderS2.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS2.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS2.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS2.setValueIsAdjusting(true);
    gameSliderS2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS2MouseExited(evt);
      }
    });
    gamePanel2.add(gameSliderS2);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel2, gridBagConstraints);

    gamePanel3.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel3.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel3.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel3.setLayout(new javax.swing.BoxLayout(gamePanel3, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField3.setEditable(false);
    gameTextField3.setText("tb set");
    gameTextField3.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField3.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField3.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField3MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField3MouseExited(evt);
      }
    });
    gamePanel3.add(gameTextField3);

    gameSliderP3.setMajorTickSpacing(10);
    gameSliderP3.setMinorTickSpacing(5);
    gameSliderP3.setPaintLabels(true);
    gameSliderP3.setPaintTicks(true);
    gameSliderP3.setSnapToTicks(true);
    gameSliderP3.setToolTipText("Slider1");
    gameSliderP3.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP3.setMinimumSize(new java.awt.Dimension(150, 45));
    gameSliderP3.setName("Slider1"); // NOI18N
    gameSliderP3.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP3.setValueIsAdjusting(true);
    gameSliderP3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP3MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP3MouseExited(evt);
      }
    });
    gamePanel3.add(gameSliderP3);

    jSeparator4.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator4.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator4.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel3.add(jSeparator4);

    gameSliderS3.setMajorTickSpacing(10);
    gameSliderS3.setMinorTickSpacing(5);
    gameSliderS3.setPaintLabels(true);
    gameSliderS3.setPaintTicks(true);
    gameSliderS3.setSnapToTicks(true);
    gameSliderS3.setToolTipText("hello1");
    gameSliderS3.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS3.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS3.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS3.setValueIsAdjusting(true);
    gameSliderS3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS3MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS3MouseExited(evt);
      }
    });
    gamePanel3.add(gameSliderS3);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel3, gridBagConstraints);

    gamePanel4.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel4.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel4.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel4MouseExited(evt);
      }
    });
    gamePanel4.setLayout(new javax.swing.BoxLayout(gamePanel4, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField4.setEditable(false);
    gameTextField4.setText("tb set");
    gameTextField4.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField4.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField4.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField4MouseExited(evt);
      }
    });
    gamePanel4.add(gameTextField4);

    gameSliderP4.setMajorTickSpacing(10);
    gameSliderP4.setMinorTickSpacing(5);
    gameSliderP4.setPaintLabels(true);
    gameSliderP4.setPaintTicks(true);
    gameSliderP4.setSnapToTicks(true);
    gameSliderP4.setToolTipText("Slider1");
    gameSliderP4.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP4.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP4.setName("Slider1"); // NOI18N
    gameSliderP4.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP4.setValueIsAdjusting(true);
    gameSliderP4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP4MouseExited(evt);
      }
    });
    gamePanel4.add(gameSliderP4);

    jSeparator5.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator5.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator5.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel4.add(jSeparator5);

    gameSliderS4.setMajorTickSpacing(10);
    gameSliderS4.setMinorTickSpacing(5);
    gameSliderS4.setPaintLabels(true);
    gameSliderS4.setPaintTicks(true);
    gameSliderS4.setSnapToTicks(true);
    gameSliderS4.setToolTipText("hello1");
    gameSliderS4.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS4.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS4.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS4.setValueIsAdjusting(true);
    gameSliderS4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS4MouseExited(evt);
      }
    });
    gamePanel4.add(gameSliderS4);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel4, gridBagConstraints);

    gamePanel5.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel5.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel5.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel5MouseExited(evt);
      }
    });
    gamePanel5.setLayout(new javax.swing.BoxLayout(gamePanel5, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField5.setEditable(false);
    gameTextField5.setText("tb set");
    gameTextField5.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField5.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField5.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField5MouseExited(evt);
      }
    });
    gamePanel5.add(gameTextField5);

    gameSliderP5.setMajorTickSpacing(10);
    gameSliderP5.setMinorTickSpacing(5);
    gameSliderP5.setPaintLabels(true);
    gameSliderP5.setPaintTicks(true);
    gameSliderP5.setSnapToTicks(true);
    gameSliderP5.setToolTipText("Slider1");
    gameSliderP5.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP5.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP5.setName("Slider1"); // NOI18N
    gameSliderP5.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP5.setValueIsAdjusting(true);
    gameSliderP5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP5MouseExited(evt);
      }
    });
    gamePanel5.add(gameSliderP5);

    jSeparator11.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator11.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator11.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel5.add(jSeparator11);

    gameSliderS5.setMajorTickSpacing(10);
    gameSliderS5.setMinorTickSpacing(5);
    gameSliderS5.setPaintLabels(true);
    gameSliderS5.setPaintTicks(true);
    gameSliderS5.setSnapToTicks(true);
    gameSliderS5.setToolTipText("hello1");
    gameSliderS5.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS5.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS5.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS5.setValueIsAdjusting(true);
    gameSliderS5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS5MouseExited(evt);
      }
    });
    gamePanel5.add(gameSliderS5);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel5, gridBagConstraints);

    gamePanel6.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel6.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel6.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel6MouseExited(evt);
      }
    });
    gamePanel6.setLayout(new javax.swing.BoxLayout(gamePanel6, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField6.setEditable(false);
    gameTextField6.setText("tb set");
    gameTextField6.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField6.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField6.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField6MouseExited(evt);
      }
    });
    gamePanel6.add(gameTextField6);

    gameSliderP6.setMajorTickSpacing(10);
    gameSliderP6.setMinorTickSpacing(5);
    gameSliderP6.setPaintLabels(true);
    gameSliderP6.setPaintTicks(true);
    gameSliderP6.setSnapToTicks(true);
    gameSliderP6.setToolTipText("Slider1");
    gameSliderP6.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP6.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP6.setName("Slider1"); // NOI18N
    gameSliderP6.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP6.setValueIsAdjusting(true);
    gameSliderP6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP6MouseExited(evt);
      }
    });
    gamePanel6.add(gameSliderP6);

    jSeparator13.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator13.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator13.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel6.add(jSeparator13);

    gameSliderS6.setMajorTickSpacing(10);
    gameSliderS6.setMinorTickSpacing(5);
    gameSliderS6.setPaintLabels(true);
    gameSliderS6.setPaintTicks(true);
    gameSliderS6.setSnapToTicks(true);
    gameSliderS6.setToolTipText("hello1");
    gameSliderS6.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS6.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS6.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS6.setValueIsAdjusting(true);
    gameSliderS6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS6MouseExited(evt);
      }
    });
    gamePanel6.add(gameSliderS6);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel6, gridBagConstraints);

    gamePanel7.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel7.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel7.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel7.setLayout(new javax.swing.BoxLayout(gamePanel7, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField7.setEditable(false);
    gameTextField7.setText("tb set");
    gameTextField7.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField7.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField7.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField7MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField7MouseExited(evt);
      }
    });
    gamePanel7.add(gameTextField7);

    gameSliderP7.setMajorTickSpacing(10);
    gameSliderP7.setMinorTickSpacing(5);
    gameSliderP7.setPaintLabels(true);
    gameSliderP7.setPaintTicks(true);
    gameSliderP7.setSnapToTicks(true);
    gameSliderP7.setToolTipText("Slider1");
    gameSliderP7.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP7.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP7.setName("Slider1"); // NOI18N
    gameSliderP7.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP7.setValueIsAdjusting(true);
    gameSliderP7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP7MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP7MouseExited(evt);
      }
    });
    gamePanel7.add(gameSliderP7);

    jSeparator14.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator14.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator14.setPreferredSize(new java.awt.Dimension(20, 40));
    jSeparator14.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jSeparator14MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        jSeparator14MouseExited(evt);
      }
    });
    gamePanel7.add(jSeparator14);

    gameSliderS7.setMajorTickSpacing(10);
    gameSliderS7.setMinorTickSpacing(5);
    gameSliderS7.setPaintLabels(true);
    gameSliderS7.setPaintTicks(true);
    gameSliderS7.setSnapToTicks(true);
    gameSliderS7.setToolTipText("hello1");
    gameSliderS7.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS7.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS7.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS7.setValueIsAdjusting(true);
    gameSliderS7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS7MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS7MouseExited(evt);
      }
    });
    gamePanel7.add(gameSliderS7);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel7, gridBagConstraints);

    gamePanel8.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel8.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel8.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel8.setLayout(new javax.swing.BoxLayout(gamePanel8, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField8.setEditable(false);
    gameTextField8.setText("tb set");
    gameTextField8.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField8.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField8.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField8MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField8MouseExited(evt);
      }
    });
    gamePanel8.add(gameTextField8);

    gameSliderP8.setMajorTickSpacing(10);
    gameSliderP8.setMinorTickSpacing(5);
    gameSliderP8.setPaintLabels(true);
    gameSliderP8.setPaintTicks(true);
    gameSliderP8.setSnapToTicks(true);
    gameSliderP8.setToolTipText("Slider1");
    gameSliderP8.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP8.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP8.setName("Slider1"); // NOI18N
    gameSliderP8.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP8.setValueIsAdjusting(true);
    gameSliderP8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP8MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP8MouseExited(evt);
      }
    });
    gamePanel8.add(gameSliderP8);

    jSeparator15.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator15.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator15.setPreferredSize(new java.awt.Dimension(20, 40));
    jSeparator15.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jSeparator15MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        jSeparator15MouseExited(evt);
      }
    });
    gamePanel8.add(jSeparator15);

    gameSliderS8.setMajorTickSpacing(10);
    gameSliderS8.setMinorTickSpacing(5);
    gameSliderS8.setPaintLabels(true);
    gameSliderS8.setPaintTicks(true);
    gameSliderS8.setSnapToTicks(true);
    gameSliderS8.setToolTipText("hello1");
    gameSliderS8.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS8.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS8.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS8.setValueIsAdjusting(true);
    gameSliderS8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS8MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS8MouseExited(evt);
      }
    });
    gamePanel8.add(gameSliderS8);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel8, gridBagConstraints);

    gamePanel9.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel9.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel9.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel9.setLayout(new javax.swing.BoxLayout(gamePanel9, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField9.setEditable(false);
    gameTextField9.setText("tb set");
    gameTextField9.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField9.setMinimumSize(new java.awt.Dimension(100, 35));
    gameTextField9.setPreferredSize(new java.awt.Dimension(150, 35));
    gameTextField9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField9MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField9MouseExited(evt);
      }
    });
    gamePanel9.add(gameTextField9);

    gameSliderP9.setMajorTickSpacing(10);
    gameSliderP9.setMinorTickSpacing(5);
    gameSliderP9.setPaintLabels(true);
    gameSliderP9.setPaintTicks(true);
    gameSliderP9.setSnapToTicks(true);
    gameSliderP9.setToolTipText("Slider1");
    gameSliderP9.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP9.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP9.setName("Slider1"); // NOI18N
    gameSliderP9.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP9.setValueIsAdjusting(true);
    gameSliderP9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP9MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP9MouseExited(evt);
      }
    });
    gamePanel9.add(gameSliderP9);

    jSeparator16.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator16.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator16.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel9.add(jSeparator16);

    gameSliderS9.setMajorTickSpacing(10);
    gameSliderS9.setMinorTickSpacing(5);
    gameSliderS9.setPaintLabels(true);
    gameSliderS9.setPaintTicks(true);
    gameSliderS9.setSnapToTicks(true);
    gameSliderS9.setToolTipText("hello1");
    gameSliderS9.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS9.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS9.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS9.setValueIsAdjusting(true);
    gameSliderS9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS9MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS9MouseExited(evt);
      }
    });
    gamePanel9.add(gameSliderS9);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel9, gridBagConstraints);

    gamePanelBottomPanel.setMinimumSize(new java.awt.Dimension(100, 100));
    gamePanelBottomPanel.setPreferredSize(new java.awt.Dimension(95, 300));

    gameTextPane.setBackground(new java.awt.Color(255, 204, 204));
    gameTextPane.setAlignmentX(0.0F);
    gameTextPane.setAlignmentY(0.0F);
    gameTextPane.setAutoscrolls(true);
    gameTextPane.setMaximumSize(new java.awt.Dimension(500, 200));
    gameTextPane.setMinimumSize(new java.awt.Dimension(90, 200));
    gameTextPane.setPreferredSize(new java.awt.Dimension(400, 200));

    gameTextField.setBackground(new java.awt.Color(153, 255, 153));
    gameTextField.setColumns(20);
    gameTextField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
    gameTextField.setLineWrap(true);
    gameTextField.setRows(5);
    gameTextField.setText("This is to be filled with descriptions of the field over which the mouse hovers");
    gameTextField.setWrapStyleWord(true);
    gameTextField.setMargin(new java.awt.Insets(0, 0, 0, 0));
    gameTextField.setMinimumSize(new java.awt.Dimension(75, 100));
    gameTextField.setPreferredSize(new java.awt.Dimension(300, 200));
    gameTextPane.setViewportView(gameTextField);

    settingsComment.setColumns(70);
    settingsComment.setRows(5);
    settingsComment.setText("put your comments for keep here");
    settingsComment.setAlignmentX(0.0F);
    settingsComment.setAlignmentY(0.0F);
    jScrollPane3.setViewportView(settingsComment);

    javax.swing.GroupLayout gamePanelBottomPanelLayout = new javax.swing.GroupLayout(gamePanelBottomPanel);
    gamePanelBottomPanel.setLayout(gamePanelBottomPanelLayout);
    gamePanelBottomPanelLayout.setHorizontalGroup(
      gamePanelBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(gamePanelBottomPanelLayout.createSequentialGroup()
        .addGap(19, 19, 19)
        .addComponent(gameTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(85, Short.MAX_VALUE))
    );
    gamePanelBottomPanelLayout.setVerticalGroup(
      gamePanelBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(gamePanelBottomPanelLayout.createSequentialGroup()
        .addGroup(gamePanelBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(gameTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(197, Short.MAX_VALUE))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.5;
    game.add(gamePanelBottomPanel, gridBagConstraints);

    gameButtonDown.setLabel("down");
    gameButtonDown.setMaximumSize(new java.awt.Dimension(120, 110));
    gameButtonDown.setMinimumSize(new java.awt.Dimension(30, 45));
    gameButtonDown.setPreferredSize(new java.awt.Dimension(46, 55));
    gameButtonDown.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameButtonDownMouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameButtonDownMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameButtonDownMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    game.add(gameButtonDown, gridBagConstraints);

    controlPanels.addTab("Settings", game);

    clan.setAutoscrolls(true);
    clan.setMaximumSize(new java.awt.Dimension(1000, 800));
    clan.setMinimumSize(new java.awt.Dimension(800, 700));
    clan.setName(""); // NOI18N
    clan.setPreferredSize(new java.awt.Dimension(800, 700));
    clan.setLayout(new java.awt.GridBagLayout());

    clanTextField.setColumns(20);
    clanTextField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
    clanTextField.setRows(5);
    clanTextField.setMargin(new java.awt.Insets(0, 0, 0, 0));
    clanTextField.setMinimumSize(new java.awt.Dimension(50, 100));
    clanTextField.setPreferredSize(new java.awt.Dimension(75, 150));
    clanTextPane.setViewportView(clanTextField);

    clan.add(clanTextPane, new java.awt.GridBagConstraints());

    clanPanel0.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel0.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel0.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel0.setLayout(new javax.swing.BoxLayout(clanPanel0, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField0.setEditable(false);
    clanTextField0.setText("tb set");
    clanTextField0.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField0.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField0.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel0.add(clanTextField0);

    gameLabelP5.setText("P");
    clanPanel0.add(gameLabelP5);

    clanSliderP0.setMajorTickSpacing(10);
    clanSliderP0.setMinorTickSpacing(5);
    clanSliderP0.setPaintLabels(true);
    clanSliderP0.setPaintTicks(true);
    clanSliderP0.setSnapToTicks(true);
    clanSliderP0.setToolTipText("Slider1");
    clanSliderP0.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP0.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP0.setName("Slider1"); // NOI18N
    clanSliderP0.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP0.setValueIsAdjusting(true);
    clanPanel0.add(clanSliderP0);

    jSeparator6.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator6.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator6.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel0.add(jSeparator6);

    gameLabelS5.setText("S");
    clanPanel0.add(gameLabelS5);

    clanSliderS0.setMajorTickSpacing(10);
    clanSliderS0.setMinorTickSpacing(5);
    clanSliderS0.setPaintLabels(true);
    clanSliderS0.setPaintTicks(true);
    clanSliderS0.setSnapToTicks(true);
    clanSliderS0.setToolTipText("hello1");
    clanSliderS0.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS0.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS0.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS0.setValueIsAdjusting(true);
    clanPanel0.add(clanSliderS0);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    clan.add(clanPanel0, gridBagConstraints);

    clanPanel1.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel1.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel1.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel1.setLayout(new javax.swing.BoxLayout(clanPanel1, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField1.setEditable(false);
    clanTextField1.setText("tb set");
    clanTextField1.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField1.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField1.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel1.add(clanTextField1);

    gameLabelP6.setText("P");
    clanPanel1.add(gameLabelP6);

    clanSliderP1.setMajorTickSpacing(10);
    clanSliderP1.setMinorTickSpacing(5);
    clanSliderP1.setPaintLabels(true);
    clanSliderP1.setPaintTicks(true);
    clanSliderP1.setSnapToTicks(true);
    clanSliderP1.setToolTipText("Slider1");
    clanSliderP1.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP1.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP1.setName("Slider1"); // NOI18N
    clanSliderP1.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP1.setValueIsAdjusting(true);
    clanPanel1.add(clanSliderP1);

    jSeparator7.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator7.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator7.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel1.add(jSeparator7);

    gameLabelS6.setText("S");
    clanPanel1.add(gameLabelS6);

    clanSliderS1.setMajorTickSpacing(10);
    clanSliderS1.setMinorTickSpacing(5);
    clanSliderS1.setPaintLabels(true);
    clanSliderS1.setPaintTicks(true);
    clanSliderS1.setSnapToTicks(true);
    clanSliderS1.setToolTipText("hello1");
    clanSliderS1.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS1.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS1.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS1.setValueIsAdjusting(true);
    clanPanel1.add(clanSliderS1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    clan.add(clanPanel1, gridBagConstraints);

    clanPanel2.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel2.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel2.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel2.setLayout(new javax.swing.BoxLayout(clanPanel2, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField2.setEditable(false);
    clanTextField2.setText("tb set");
    clanTextField2.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField2.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField2.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel2.add(clanTextField2);

    gameLabelP7.setText("P");
    clanPanel2.add(gameLabelP7);

    clanSliderP2.setMajorTickSpacing(10);
    clanSliderP2.setMinorTickSpacing(5);
    clanSliderP2.setPaintLabels(true);
    clanSliderP2.setPaintTicks(true);
    clanSliderP2.setSnapToTicks(true);
    clanSliderP2.setToolTipText("Slider1");
    clanSliderP2.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP2.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP2.setName("Slider1"); // NOI18N
    clanSliderP2.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP2.setValueIsAdjusting(true);
    clanPanel2.add(clanSliderP2);

    jSeparator8.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator8.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator8.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel2.add(jSeparator8);

    gameLabelS7.setText("S");
    clanPanel2.add(gameLabelS7);

    clanSliderS2.setMajorTickSpacing(10);
    clanSliderS2.setMinorTickSpacing(5);
    clanSliderS2.setPaintLabels(true);
    clanSliderS2.setPaintTicks(true);
    clanSliderS2.setSnapToTicks(true);
    clanSliderS2.setToolTipText("hello1");
    clanSliderS2.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS2.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS2.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS2.setValueIsAdjusting(true);
    clanPanel2.add(clanSliderS2);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    clan.add(clanPanel2, gridBagConstraints);

    clanPanel3.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel3.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel3.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel3.setLayout(new javax.swing.BoxLayout(clanPanel3, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField3.setEditable(false);
    clanTextField3.setText("tb set");
    clanTextField3.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField3.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField3.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel3.add(clanTextField3);

    clanLabelP3.setText("P");
    clanPanel3.add(clanLabelP3);

    clanSliderP3.setMajorTickSpacing(10);
    clanSliderP3.setMinorTickSpacing(5);
    clanSliderP3.setPaintLabels(true);
    clanSliderP3.setPaintTicks(true);
    clanSliderP3.setSnapToTicks(true);
    clanSliderP3.setToolTipText("Slider1");
    clanSliderP3.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP3.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP3.setName("Slider1"); // NOI18N
    clanSliderP3.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP3.setValueIsAdjusting(true);
    clanPanel3.add(clanSliderP3);

    jSeparator9.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator9.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator9.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel3.add(jSeparator9);

    gameLabelS8.setText("S");
    clanPanel3.add(gameLabelS8);

    clanSliderS3.setMajorTickSpacing(10);
    clanSliderS3.setMinorTickSpacing(5);
    clanSliderS3.setPaintLabels(true);
    clanSliderS3.setPaintTicks(true);
    clanSliderS3.setSnapToTicks(true);
    clanSliderS3.setToolTipText("hello1");
    clanSliderS3.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS3.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS3.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS3.setValueIsAdjusting(true);
    clanPanel3.add(clanSliderS3);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    clan.add(clanPanel3, gridBagConstraints);

    clanPanel4.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel4.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel4.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel4.setLayout(new javax.swing.BoxLayout(clanPanel4, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField4.setEditable(false);
    clanTextField4.setText("tb set");
    clanTextField4.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField4.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField4.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel4.add(clanTextField4);

    clanLabelP4.setText("P");
    clanPanel4.add(clanLabelP4);

    clanSliderP4.setMajorTickSpacing(10);
    clanSliderP4.setMinorTickSpacing(5);
    clanSliderP4.setPaintLabels(true);
    clanSliderP4.setPaintTicks(true);
    clanSliderP4.setSnapToTicks(true);
    clanSliderP4.setToolTipText("Slider1");
    clanSliderP4.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP4.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP4.setName("Slider1"); // NOI18N
    clanSliderP4.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP4.setValueIsAdjusting(true);
    clanPanel4.add(clanSliderP4);

    jSeparator10.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator10.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator10.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel4.add(jSeparator10);

    clanLabelS4.setText("S");
    clanPanel4.add(clanLabelS4);

    clanSliderS4.setMajorTickSpacing(10);
    clanSliderS4.setMinorTickSpacing(5);
    clanSliderS4.setPaintLabels(true);
    clanSliderS4.setPaintTicks(true);
    clanSliderS4.setSnapToTicks(true);
    clanSliderS4.setToolTipText("hello1");
    clanSliderS4.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS4.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS4.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS4.setValueIsAdjusting(true);
    clanPanel4.add(clanSliderS4);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 19;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    clan.add(clanPanel4, gridBagConstraints);

    controlPanels.addTab("clan", clan);

    display.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    display.setAlignmentY(300.0F);
    display.setMaximumSize(new java.awt.Dimension(1500, 1200));
    display.setMinimumSize(new java.awt.Dimension(300, 400));
    display.setPreferredSize(new java.awt.Dimension(1200, 700));
    display.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    displayPanel0.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    displayPanel0.setToolTipText("primary display");
    displayPanel0.setMaximumSize(new java.awt.Dimension(1500, 1200));
    displayPanel0.setMinimumSize(new java.awt.Dimension(400, 200));
    displayPanel0.setPreferredSize(new java.awt.Dimension(1200, 700));
    displayPanel0.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    displayPanel0Text.setColumns(70);
    displayPanel0Text.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
    displayPanel0Text.setLineWrap(true);
    displayPanel0Text.setRows(20);
    displayPanel0Text.setWrapStyleWord(true);
    displayPanel0Text.setAlignmentX(0.0F);
    displayPanel0Text.setAlignmentY(0.0F);
    displayPanel0Text.setBorder(null);
    displayPanel0Text.setMaximumSize(new java.awt.Dimension(1400, 1200));
    displayPanel0Text.setMinimumSize(new java.awt.Dimension(800, 300));
    displayPanel0Text.setName(""); // NOI18N
    displayPanel0Text.setOpaque(false);
    displayPanel0Text.setPreferredSize(new java.awt.Dimension(1260, 600));
    displayPanel0.add(displayPanel0Text);

    display.add(displayPanel0);

    displayPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
    displayPanel1.setMaximumSize(new java.awt.Dimension(1200, 600));
    displayPanel1.setMinimumSize(new java.awt.Dimension(800, 20));
    displayPanel1.setName("displayTPanel"); // NOI18N
    displayPanel1.setPreferredSize(new java.awt.Dimension(1200, 600));
    displayPanel1.setRequestFocusEnabled(false);

    jScrollPane1.setPreferredSize(new java.awt.Dimension(1200, 600));

    jTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null, null, null},
        {null, null, null, null, null, null},
        {null, null, null, null, null, null},
        {null, null, null, null, null, null},
        {null, null, null, null, null, null},
        {null, null, null, null, null, null},
        {null, null, null, null, null, null},
        {null, null, null, null, null, null}
      },
      new String [] {
        "Titles", "Red", "Orange", "Yellow", "Green", "Blue"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    jTable1.setColumnSelectionAllowed(true);
    jTable1.setPreferredSize(new java.awt.Dimension(1175, 500));
    jTable1.getTableHeader().setReorderingAllowed(false);
    jScrollPane1.setViewportView(jTable1);
    jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (jTable1.getColumnModel().getColumnCount() > 0) {
      jTable1.getColumnModel().getColumn(0).setResizable(false);
      jTable1.getColumnModel().getColumn(0).setPreferredWidth(300);
      jTable1.getColumnModel().getColumn(1).setResizable(false);
      jTable1.getColumnModel().getColumn(1).setPreferredWidth(175);
      jTable1.getColumnModel().getColumn(2).setResizable(false);
      jTable1.getColumnModel().getColumn(2).setPreferredWidth(175);
      jTable1.getColumnModel().getColumn(3).setResizable(false);
      jTable1.getColumnModel().getColumn(3).setPreferredWidth(175);
      jTable1.getColumnModel().getColumn(4).setResizable(false);
      jTable1.getColumnModel().getColumn(4).setPreferredWidth(175);
      jTable1.getColumnModel().getColumn(5).setResizable(false);
      jTable1.getColumnModel().getColumn(5).setPreferredWidth(175);
    }

    javax.swing.GroupLayout displayPanel1Layout = new javax.swing.GroupLayout(displayPanel1);
    displayPanel1.setLayout(displayPanel1Layout);
    displayPanel1Layout.setHorizontalGroup(
      displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 1194, Short.MAX_VALUE)
      .addGroup(displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(displayPanel1Layout.createSequentialGroup()
          .addGap(0, 0, Short.MAX_VALUE)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGap(0, 0, Short.MAX_VALUE)))
    );
    displayPanel1Layout.setVerticalGroup(
      displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 600, Short.MAX_VALUE)
      .addGroup(displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(displayPanel1Layout.createSequentialGroup()
          .addGap(0, 0, Short.MAX_VALUE)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGap(0, 0, Short.MAX_VALUE)))
    );

    display.add(displayPanel1);

    controlPanels.addTab("display", display);

    stats.setAutoscrolls(true);
    stats.setMaximumSize(new java.awt.Dimension(1800, 1200));
    stats.setMinimumSize(new java.awt.Dimension(500, 4500));
    stats.setPreferredSize(new java.awt.Dimension(1250, 1000));
    stats.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 0, 0));

    topPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 255, 153), 2));
    topPane.setAlignmentX(0.0F);
    topPane.setAlignmentY(0.0F);
    topPane.setMaximumSize(new java.awt.Dimension(1259, 100));
    topPane.setMinimumSize(new java.awt.Dimension(800, 50));
    topPane.setNextFocusableComponent(statsTable1);
    topPane.setPreferredSize(new java.awt.Dimension(1250, 60));

    rbuttons12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 51), 2));
    rbuttons12.setAlignmentX(0.0F);
    rbuttons12.setAlignmentY(0.0F);
    rbuttons12.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    rbuttons12.setMinimumSize(new java.awt.Dimension(375, 27));
    rbuttons12.setName(""); // NOI18N
    rbuttons12.setPreferredSize(new java.awt.Dimension(450, 60));

    rbuttons1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255), 2));
    rbuttons1.setAlignmentX(0.0F);
    rbuttons1.setAlignmentY(0.0F);
    rbuttons1.setMinimumSize(new java.awt.Dimension(415, 23));
    rbuttons1.setPreferredSize(new java.awt.Dimension(420, 23));
    rbuttons1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    statsButtonGroupReportNumber.add(statsButton0);
    statsButton0.setText("0");
    statsButton0.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton0ItemStateChanged(evt);
      }
    });
    statsButton0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton0MouseEntered(evt);
      }
    });
    statsButton0.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton0ActionPerformed(evt);
      }
    });
    rbuttons1.add(statsButton0);

    statsButtonGroupReportNumber.add(statsButton1);
    statsButton1.setText("1");
    statsButton1.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton1ItemStateChanged(evt);
      }
    });
    statsButton1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton1MouseEntered(evt);
      }
    });
    statsButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton1ActionPerformed(evt);
      }
    });
    rbuttons1.add(statsButton1);

    statsButtonGroupReportNumber.add(statsButton2);
    statsButton2.setText("2");
    statsButton2.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton2ItemStateChanged(evt);
      }
    });
    statsButton2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton2MouseEntered(evt);
      }
    });
    rbuttons1.add(statsButton2);

    statsButtonGroupReportNumber.add(statsButton3);
    statsButton3.setText("3");
    statsButton3.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton3ItemStateChanged(evt);
      }
    });
    statsButton3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton3MouseEntered(evt);
      }
    });
    statsButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton3ActionPerformed(evt);
      }
    });
    rbuttons1.add(statsButton3);

    statsButtonGroupReportNumber.add(statsButton4);
    statsButton4.setText("4");
    statsButton4.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton4ItemStateChanged(evt);
      }
    });
    statsButton4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton4MouseEntered(evt);
      }
    });
    rbuttons1.add(statsButton4);

    statsButtonGroupReportNumber.add(statsButton5);
    statsButton5.setText("5");
    statsButton5.setToolTipText("GameMaster");
    statsButton5.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton5ItemStateChanged(evt);
      }
    });
    statsButton5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton5MouseEntered(evt);
      }
    });
    statsButton5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton5ActionPerformed(evt);
      }
    });
    rbuttons1.add(statsButton5);

    statsButtonGroupReportNumber.add(statsButton6);
    statsButton6.setText(" 6");
    statsButton6.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton6ItemStateChanged(evt);
      }
    });
    statsButton6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton6MouseEntered(evt);
      }
    });
    rbuttons1.add(statsButton6);

    statsButtonGroupReportNumber.add(statsButton7);
    statsButton7.setText("7");
    statsButton7.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton7ItemStateChanged(evt);
      }
    });
    statsButton7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton7MouseEntered(evt);
      }
    });
    rbuttons1.add(statsButton7);

    statsButtonGroupReportNumber.add(statsButton8);
    statsButton8.setText("8");
    statsButton8.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton8ItemStateChanged(evt);
      }
    });
    statsButton8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton8MouseEntered(evt);
      }
    });
    rbuttons1.add(statsButton8);

    statsButtonGroupReportNumber.add(statsButton9);
    statsButton9.setText("9");
    statsButton9.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton9ItemStateChanged(evt);
      }
    });
    statsButton9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton9MouseEntered(evt);
      }
    });
    rbuttons1.add(statsButton9);

    statsButtonGroupReportNumber.add(statsButton10);
    statsButton10.setText("10");
    statsButton10.setMaximumSize(new java.awt.Dimension(47, 23));
    statsButton10.setPreferredSize(new java.awt.Dimension(45, 23));
    statsButton10.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton10ItemStateChanged(evt);
      }
    });
    statsButton10.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton10MouseEntered(evt);
      }
    });
    statsButton10.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton10ActionPerformed(evt);
      }
    });
    rbuttons1.add(statsButton10);

    rbuttons2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 153), 2));
    rbuttons2.setAlignmentX(0.0F);
    rbuttons2.setAlignmentY(0.0F);
    rbuttons2.setMinimumSize(new java.awt.Dimension(429, 23));
    rbuttons2.setPreferredSize(new java.awt.Dimension(438, 24));
    rbuttons2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    statsButtonGroupReportNumber.add(statsButton11);
    statsButton11.setText("11");
    statsButton11.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton11ItemStateChanged(evt);
      }
    });
    statsButton11.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton11MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton11);

    statsButtonGroupReportNumber.add(statsButton12);
    statsButton12.setText("12");
    statsButton12.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton12ItemStateChanged(evt);
      }
    });
    statsButton12.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton12MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton12);

    statsButtonGroupReportNumber.add(statsButton13);
    statsButton13.setText("13");
    statsButton13.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton13ItemStateChanged(evt);
      }
    });
    statsButton13.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton13MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton13);

    statsButtonGroupReportNumber.add(statsButton14);
    statsButton14.setText("14");
    statsButton14.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton14ItemStateChanged(evt);
      }
    });
    statsButton14.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton14MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton14);

    statsButtonGroupReportNumber.add(statsButton15);
    statsButton15.setText("15");
    statsButton15.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton15ItemStateChanged(evt);
      }
    });
    statsButton15.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton15MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton15);

    statsButtonGroupReportNumber.add(statsButton16);
    statsButton16.setText("16");
    statsButton16.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton16ItemStateChanged(evt);
      }
    });
    statsButton16.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton16MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton16);

    statsButtonGroupReportNumber.add(statsButton17);
    statsButton17.setText("17");
    statsButton17.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton17ItemStateChanged(evt);
      }
    });
    statsButton17.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton17MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton17);

    statsButtonGroupReportNumber.add(statsButton18);
    statsButton18.setText("18");
    statsButton18.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton18ItemStateChanged(evt);
      }
    });
    statsButton18.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton18MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton18);

    statsButtonGroupReportNumber.add(statsButton19);
    statsButton19.setText("19");
    statsButton19.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton19ItemStateChanged(evt);
      }
    });
    statsButton19.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton19MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton19);

    statsButtonGroupReportNumber.add(statsButton20);
    statsButton20.setText("20");
    statsButton20.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton20ItemStateChanged(evt);
      }
    });
    statsButton20.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton20MouseEntered(evt);
      }
    });
    rbuttons2.add(statsButton20);

    javax.swing.GroupLayout rbuttons12Layout = new javax.swing.GroupLayout(rbuttons12);
    rbuttons12.setLayout(rbuttons12Layout);
    rbuttons12Layout.setHorizontalGroup(
      rbuttons12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(rbuttons1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(rbuttons2, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
    );
    rbuttons12Layout.setVerticalGroup(
      rbuttons12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(rbuttons12Layout.createSequentialGroup()
        .addComponent(rbuttons1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, 0)
        .addComponent(rbuttons2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    ybuttons.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204), 2));
    ybuttons.setAlignmentX(450.0F);
    ybuttons.setAlignmentY(0.0F);
    ybuttons.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    ybuttons.setMinimumSize(new java.awt.Dimension(80, 26));
    ybuttons.setPreferredSize(new java.awt.Dimension(100, 50));
    java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1);
    flowLayout1.setAlignOnBaseline(true);
    ybuttons.setLayout(flowLayout1);

    statsCtlButtonRun1Yr.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsCtlButtonRun1Yr.setText("1 yr");
    statsCtlButtonRun1Yr.setActionCommand("1");
    statsCtlButtonRun1Yr.setAlignmentY(0.0F);
    statsCtlButtonRun1Yr.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.red, java.awt.Color.black, null, null));
    statsCtlButtonRun1Yr.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    statsCtlButtonRun1Yr.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    statsCtlButtonRun1Yr.setMargin(new java.awt.Insets(2, 0, 2, 0));
    statsCtlButtonRun1Yr.setMaximumSize(new java.awt.Dimension(40, 40));
    statsCtlButtonRun1Yr.setMinimumSize(new java.awt.Dimension(20, 20));
    statsCtlButtonRun1Yr.setName("1yr"); // NOI18N
    statsCtlButtonRun1Yr.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        statsCtlButtonRun1YrMouseClicked(evt);
      }
    });
    statsCtlButtonRun1Yr.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsCtlButtonRun1YrActionPerformed(evt);
      }
    });
    ybuttons.add(statsCtlButtonRun1Yr);

    statsCtlButtonRun10Yr.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsCtlButtonRun10Yr.setText("10 yr");
    statsCtlButtonRun10Yr.setActionCommand("10 ");
    statsCtlButtonRun10Yr.setAlignmentX(5.0F);
    statsCtlButtonRun10Yr.setAlignmentY(0.0F);
    statsCtlButtonRun10Yr.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.magenta, java.awt.Color.black, null, null));
    statsCtlButtonRun10Yr.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    statsCtlButtonRun10Yr.setMargin(new java.awt.Insets(0, 0, 0, 0));
    statsCtlButtonRun10Yr.setMaximumSize(new java.awt.Dimension(50, 40));
    statsCtlButtonRun10Yr.setMinimumSize(new java.awt.Dimension(20, 20));
    statsCtlButtonRun10Yr.setName("10 yr"); // NOI18N
    statsCtlButtonRun10Yr.setPreferredSize(new java.awt.Dimension(45, 23));
    statsCtlButtonRun10Yr.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    statsCtlButtonRun10Yr.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    statsCtlButtonRun10Yr.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        statsCtlButtonRun10YrMouseClicked(evt);
      }
    });
    ybuttons.add(statsCtlButtonRun10Yr);

    statsCtlButtonRun5Yr.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsCtlButtonRun5Yr.setText("5 yr");
    statsCtlButtonRun5Yr.setActionCommand("5");
    statsCtlButtonRun5Yr.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.yellow, java.awt.Color.black, null, null));
    statsCtlButtonRun5Yr.setMargin(new java.awt.Insets(0, 0, 0, 0));
    statsCtlButtonRun5Yr.setMaximumSize(new java.awt.Dimension(50, 40));
    statsCtlButtonRun5Yr.setMinimumSize(new java.awt.Dimension(20, 20));
    statsCtlButtonRun5Yr.setName("5 yr"); // NOI18N
    statsCtlButtonRun5Yr.setPreferredSize(new java.awt.Dimension(45, 23));
    statsCtlButtonRun5Yr.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        statsCtlButtonRun5YrMouseClicked(evt);
      }
    });
    ybuttons.add(statsCtlButtonRun5Yr);

    statsCtlButtonRun20Yr.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsCtlButtonRun20Yr.setText("20 yr");
    statsCtlButtonRun20Yr.setActionCommand("20");
    statsCtlButtonRun20Yr.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.cyan, java.awt.Color.black, java.awt.Color.red, null));
    statsCtlButtonRun20Yr.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    statsCtlButtonRun20Yr.setMargin(new java.awt.Insets(2, 0, 2, 0));
    statsCtlButtonRun20Yr.setMaximumSize(new java.awt.Dimension(25, 40));
    statsCtlButtonRun20Yr.setMinimumSize(new java.awt.Dimension(20, 20));
    statsCtlButtonRun20Yr.setName("20 yr"); // NOI18N
    statsCtlButtonRun20Yr.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        statsCtlButtonRun20YrMouseClicked(evt);
      }
    });
    ybuttons.add(statsCtlButtonRun20Yr);

    bigPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 255), 2));
    bigPanel.setToolTipText("this is the big panel");
    bigPanel.setAlignmentX(600.5F);
    bigPanel.setAlignmentY(0.0F);
    bigPanel.setMaximumSize(new java.awt.Dimension(900, 100));
    bigPanel.setMinimumSize(new java.awt.Dimension(300, 70));
    bigPanel.setPreferredSize(new java.awt.Dimension(800, 70));
    bigPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    statsStop.setBackground(new java.awt.Color(255, 102, 102));
    statsStop.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsStop.setMnemonic('s');
    statsStop.setText("stop");
    statsStop.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
    statsStop.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    statsStop.setIconTextGap(0);
    statsStop.setMargin(new java.awt.Insets(0, 0, 0, 0));
    statsStop.setMaximumSize(new java.awt.Dimension(100, 25));
    statsStop.setMinimumSize(new java.awt.Dimension(20, 21));
    statsStop.setPreferredSize(new java.awt.Dimension(65, 23));
    statsStop.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        statsStopMouseClicked(evt);
      }
    });
    bigPanel.add(statsStop);

    remember.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    remember.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
    remember.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    remember.setIconTextGap(0);
    remember.setLabel("remember");
    remember.setMargin(new java.awt.Insets(0, 0, 0, 0));
    remember.setMaximumSize(new java.awt.Dimension(150, 27));
    remember.setMinimumSize(new java.awt.Dimension(50, 23));
    remember.setPreferredSize(new java.awt.Dimension(105, 23));
    remember.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        rememberMouseClicked(evt);
      }
    });
    remember.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rememberActionPerformed(evt);
      }
    });
    bigPanel.add(remember);

    statsCommentPane.setAlignmentX(0.0F);
    statsCommentPane.setAlignmentY(0.0F);
    statsCommentPane.setPreferredSize(new java.awt.Dimension(400, 94));

    statsRememberWhy.setColumns(50);
    statsRememberWhy.setFont(new java.awt.Font("Palatino Linotype", 0, 13)); // NOI18N
    statsRememberWhy.setRows(5);
    statsRememberWhy.setText("Put here the reason you want to save a stats row");
    statsRememberWhy.setAlignmentX(0.0F);
    statsRememberWhy.setAlignmentY(0.0F);
    statsCommentPane.setViewportView(statsRememberWhy);

    bigPanel.add(statsCommentPane);

    javax.swing.GroupLayout topPaneLayout = new javax.swing.GroupLayout(topPane);
    topPane.setLayout(topPaneLayout);
    topPaneLayout.setHorizontalGroup(
      topPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(topPaneLayout.createSequentialGroup()
        .addComponent(ybuttons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(rbuttons12, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(bigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 777, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    topPaneLayout.setVerticalGroup(
      topPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(rbuttons12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addGroup(topPaneLayout.createSequentialGroup()
        .addGap(2, 2, 2)
        .addGroup(topPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(ybuttons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(bigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    stats.add(topPane);

    statsField.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsField.setText("description");
    statsField.setToolTipText("");
    statsField.setAlignmentX(0.0F);
    statsField.setAlignmentY(0.0F);
    statsField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 102), 2));
    statsField.setMargin(new java.awt.Insets(0, 0, 0, 0));
    statsField.setMaximumSize(new java.awt.Dimension(1900, 50));
    statsField.setMinimumSize(new java.awt.Dimension(500, 30));
    statsField.setPreferredSize(new java.awt.Dimension(1200, 30));
    statsField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsFieldActionPerformed(evt);
      }
    });
    stats.add(statsField);

    statsScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    statsScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    statsScrollPane2.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 255, 255), 2));
    statsScrollPane2.setAlignmentX(0.0F);
    statsScrollPane2.setAlignmentY(5.0F);
    statsScrollPane2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
    statsScrollPane2.setMaximumSize(new java.awt.Dimension(1200, 1000));
    statsScrollPane2.setMinimumSize(new java.awt.Dimension(700, 400));
    statsScrollPane2.setName("StatsScroll"); // NOI18N
    statsScrollPane2.setPreferredSize(new java.awt.Dimension(1200, 900));
    statsScrollPane2.setRequestFocusEnabled(false);
    statsScrollPane2.setVerifyInputWhenFocusTarget(false);

    statsTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 255, 255), 2));
    statsTable1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
    statsTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null}
      },
      new String [] {
        "title", "P-red", "P-orange", "P-yellow", "P-green", "P-blue", "S-red", "S-orange", "S-yellow", "S-green", "S-blue"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false, false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    statsTable1.setAlignmentX(0.0F);
    statsTable1.setAlignmentY(0.0F);
    statsTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    statsTable1.setColumnSelectionAllowed(true);
    statsTable1.setMaximumSize(new java.awt.Dimension(1200, 1000));
    statsTable1.setMinimumSize(new java.awt.Dimension(700, 400));
    statsTable1.setPreferredSize(new java.awt.Dimension(1200, 1000));
    statsTable1.getTableHeader().setReorderingAllowed(false);
    statsScrollPane2.setViewportView(statsTable1);
    statsTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (statsTable1.getColumnModel().getColumnCount() > 0) {
      statsTable1.getColumnModel().getColumn(0).setResizable(false);
      statsTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
      statsTable1.getColumnModel().getColumn(1).setResizable(false);
      statsTable1.getColumnModel().getColumn(2).setResizable(false);
      statsTable1.getColumnModel().getColumn(3).setResizable(false);
      statsTable1.getColumnModel().getColumn(4).setResizable(false);
      statsTable1.getColumnModel().getColumn(5).setResizable(false);
      statsTable1.getColumnModel().getColumn(6).setResizable(false);
      statsTable1.getColumnModel().getColumn(7).setResizable(false);
      statsTable1.getColumnModel().getColumn(8).setResizable(false);
      statsTable1.getColumnModel().getColumn(10).setResizable(false);
    }
    statsTable1.getAccessibleContext().setAccessibleName("stats table");

    stats.add(statsScrollPane2);

    controlPanels.addTab("stats", stats);

    log.setBackground(new java.awt.Color(255, 255, 255));
    log.setAutoscrolls(true);
    log.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    log.setMaximumSize(new java.awt.Dimension(1800, 1200));
    log.setMinimumSize(new java.awt.Dimension(500, 450));
    log.setPreferredSize(new java.awt.Dimension(1200, 800));
    log.setLayout(new java.awt.GridBagLayout());

    logTableScrollPanel.setAutoscrolls(true);
    logTableScrollPanel.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
    logTableScrollPanel.setMaximumSize(new java.awt.Dimension(1800, 1200));
    logTableScrollPanel.setMinimumSize(new java.awt.Dimension(500, 450));
    logTableScrollPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
    logTableScrollPanel.addInputMethodListener(new java.awt.event.InputMethodListener() {
      public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
        logTableScrollPanelCaretPositionChanged(evt);
      }
      public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        logTableScrollPanelInputMethodTextChanged(evt);
      }
    });

    logDisplayTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    logDisplayTable.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
    logDisplayTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null}
      },
      new String [] {
        "Title", "col0", "col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false, false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    logDisplayTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    logDisplayTable.setColumnSelectionAllowed(true);
    logDisplayTable.setDebugGraphicsOptions(javax.swing.DebugGraphics.LOG_OPTION);
    logDisplayTable.setGridColor(new java.awt.Color(153, 153, 255));
    logDisplayTable.setMaximumSize(new java.awt.Dimension(1800, 1200));
    logDisplayTable.setMinimumSize(new java.awt.Dimension(500, 450));
    logDisplayTable.setPreferredSize(new java.awt.Dimension(1200, 800));
    logDisplayTable.getTableHeader().setResizingAllowed(false);
    logDisplayTable.getTableHeader().setReorderingAllowed(false);
    logTableScrollPanel.setViewportView(logDisplayTable);
    logDisplayTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (logDisplayTable.getColumnModel().getColumnCount() > 0) {
      logDisplayTable.getColumnModel().getColumn(0).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(0).setPreferredWidth(200);
      logDisplayTable.getColumnModel().getColumn(1).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(2).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(3).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(4).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(5).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(6).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(7).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(8).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(9).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(10).setResizable(false);
    }
    logDisplayTable.getAccessibleContext().setAccessibleName("logTable");

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 56;
    gridBagConstraints.gridwidth = 19;
    gridBagConstraints.gridheight = 58;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 1100;
    gridBagConstraints.ipady = 600;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(19, 0, 0, 0);
    log.add(logTableScrollPanel, gridBagConstraints);

    logDlevel2.setText("DLevel2");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 14;
    gridBagConstraints.gridy = 26;
    gridBagConstraints.gridheight = 14;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 19, 0, 0);
    log.add(logDlevel2, gridBagConstraints);

    LogDlen1Slider.setMajorTickSpacing(25);
    LogDlen1Slider.setMaximum(75);
    LogDlen1Slider.setMinorTickSpacing(5);
    LogDlen1Slider.setPaintLabels(true);
    LogDlen1Slider.setPaintTicks(true);
    LogDlen1Slider.setValue(20);
    LogDlen1Slider.setMaximumSize(new java.awt.Dimension(200, 35));
    LogDlen1Slider.setMinimumSize(new java.awt.Dimension(24, 12));
    LogDlen1Slider.setName("Length"); // NOI18N
    LogDlen1Slider.setPreferredSize(new java.awt.Dimension(120, 35));
    LogDlen1Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        LogDlen1SliderStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 13;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 9;
    gridBagConstraints.ipadx = 84;
    gridBagConstraints.ipady = 23;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(1, 15, 0, 0);
    log.add(LogDlen1Slider, gridBagConstraints);
    LogDlen1Slider.getAccessibleContext().setAccessibleName("Length");

    logDlen1.setText("DLen1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 12;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 0);
    log.add(logDlen1, gridBagConstraints);

    logDLevel1Slider.setMajorTickSpacing(5);
    logDLevel1Slider.setMaximum(15);
    logDLevel1Slider.setMinorTickSpacing(1);
    logDLevel1Slider.setPaintLabels(true);
    logDLevel1Slider.setPaintTicks(true);
    logDLevel1Slider.setValue(2);
    logDLevel1Slider.setMinimumSize(new java.awt.Dimension(36, 35));
    logDLevel1Slider.setPreferredSize(new java.awt.Dimension(200, 35));
    logDLevel1Slider.setValueIsAdjusting(true);
    logDLevel1Slider.setVerifyInputWhenFocusTarget(false);
    logDLevel1Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logDLevel1SliderStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 15;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 6;
    gridBagConstraints.ipadx = 93;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
    log.add(logDLevel1Slider, gridBagConstraints);
    logDLevel1Slider.getAccessibleContext().setAccessibleName("Level Slider");

    logM1Spinner.setName("histStartValue"); // NOI18N
    logM1Spinner.setValue(1);
    logM1Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logM1SpinnerStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 5;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.ipadx = 38;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(1, 6, 0, 0);
    log.add(logM1Spinner, gridBagConstraints);

    LogDLen2Slider.setMajorTickSpacing(25);
    LogDLen2Slider.setMaximum(75);
    LogDLen2Slider.setMinorTickSpacing(5);
    LogDLen2Slider.setPaintLabels(true);
    LogDLen2Slider.setPaintTicks(true);
    LogDLen2Slider.setValue(20);
    LogDLen2Slider.setMaximumSize(new java.awt.Dimension(200, 45));
    LogDLen2Slider.setName("Length"); // NOI18N
    LogDLen2Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        LogDLen2SliderStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 13;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.gridheight = 29;
    gridBagConstraints.ipadx = 72;
    gridBagConstraints.ipady = -6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
    log.add(LogDLen2Slider, gridBagConstraints);

    logDLevel2Slider.setMajorTickSpacing(5);
    logDLevel2Slider.setMaximum(15);
    logDLevel2Slider.setMinorTickSpacing(1);
    logDLevel2Slider.setPaintLabels(true);
    logDLevel2Slider.setPaintTicks(true);
    logDLevel2Slider.setValue(2);
    logDLevel2Slider.setValueIsAdjusting(true);
    logDLevel2Slider.setVerifyInputWhenFocusTarget(false);
    logDLevel2Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logDLevel2SliderStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 15;
    gridBagConstraints.gridy = 26;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 30;
    gridBagConstraints.ipadx = 84;
    gridBagConstraints.ipady = -7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 22, 0, 0);
    log.add(logDLevel2Slider, gridBagConstraints);

    logDlen2.setText("DLen2");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 12;
    gridBagConstraints.gridy = 14;
    gridBagConstraints.gridheight = 13;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 0);
    log.add(logDlen2, gridBagConstraints);

    logDlevel1.setText("DLevel1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 14;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 19, 0, 0);
    log.add(logDlevel1, gridBagConstraints);

    logM2Spinner.setName("histStartValue"); // NOI18N
    logM2Spinner.setValue(1);
    logM2Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logM2SpinnerStateChanged(evt);
      }
    });
    logM2Spinner.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        logM2SpinnerMouseReleased(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 6;
    gridBagConstraints.gridheight = 11;
    gridBagConstraints.ipadx = 40;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
    log.add(logM2Spinner, gridBagConstraints);

    logNamesScrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    logNamesScrollPanel.setPreferredSize(new java.awt.Dimension(350, 2000));

    logEnvirnNamesList.setModel(namesList
    );
    logEnvirnNamesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    logEnvirnNamesList.setMaximumSize(new java.awt.Dimension(300, 2000));
    logEnvirnNamesList.setMinimumSize(new java.awt.Dimension(50, 50));
    logEnvirnNamesList.setPreferredSize(new java.awt.Dimension(300, 2000));
    logEnvirnNamesList.setVisibleRowCount(3);
    logEnvirnNamesList.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        logEnvirnNamesListMouseClicked(evt);
      }
    });
    logNamesScrollPanel.setViewportView(logEnvirnNamesList);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 17;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 57;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 279;
    gridBagConstraints.ipady = 80;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(10, 14, 0, 0);
    log.add(logNamesScrollPanel, gridBagConstraints);

    Start1Name.setBackground(new java.awt.Color(255, 102, 204));
    Start1Name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
    Start1Name.setForeground(new java.awt.Color(204, 0, 0));
    Start1Name.setText("P000001");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 10;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.ipadx = 10;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(3, 2, 0, 0);
    log.add(Start1Name, gridBagConstraints);

    Start2Name.setBackground(new java.awt.Color(255, 102, 204));
    Start2Name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
    Start2Name.setForeground(new java.awt.Color(204, 0, 0));
    Start2Name.setText("P00001");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 10;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridheight = 10;
    gridBagConstraints.ipadx = 9;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(1, 2, 0, 0);
    log.add(Start2Name, gridBagConstraints);

    logButtonGroup1or2.add(logRadioButtonStart1);
    logRadioButtonStart1.setText("Start1");
    logRadioButtonStart1.setToolTipText("Planet");
    logRadioButtonStart1.setActionCommand("1");
    logRadioButtonStart1.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        logRadioButtonStart1ItemStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.gridheight = 4;
    gridBagConstraints.ipadx = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(1, 10, 0, 0);
    log.add(logRadioButtonStart1, gridBagConstraints);

    logButtonGroup1or2.add(logRadioButtonStart2);
    logRadioButtonStart2.setText("Start2");
    logRadioButtonStart2.setActionCommand("2");
    logRadioButtonStart2.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        logRadioButtonStart2ItemStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.gridheight = 12;
    gridBagConstraints.ipadx = 15;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
    log.add(logRadioButtonStart2, gridBagConstraints);

    logBGactions.add(logActionJump);
    logActionJump.setText("Jump");
    logActionJump.setName("logActionJump22"); // NOI18N
    logActionJump.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        logActionJumpItemStateChanged(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 26;
    gridBagConstraints.gridheight = 15;
    gridBagConstraints.ipadx = -5;
    gridBagConstraints.ipady = -5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 0);
    log.add(logActionJump, gridBagConstraints);

    logBGactions.add(logActionAdd);
    logActionAdd.setText("add");
    logActionAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logActionAddActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 26;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.gridheight = 16;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 3, 0, 0);
    log.add(logActionAdd, gridBagConstraints);

    logBGactions.add(logActionDel);
    logActionDel.setText("del");
    logActionDel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logActionDelActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 7;
    gridBagConstraints.gridy = 26;
    gridBagConstraints.gridheight = 16;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
    log.add(logActionDel, gridBagConstraints);

    controlPanels.addTab("logs", log);

    getContentPane().add(controlPanels);
    controlPanels.getAccessibleContext().setAccessibleName("traderPanel");
  }// </editor-fold>//GEN-END:initComponents

  private void formInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_formInputMethodTextChanged
    /*
     try {
     NumberFormat whole = NumberFormat.getNumberInstance();
     whole.setMaximumFractionDigits(0);
     NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
     format.setParseIntegerOnly(true);
     String source = evt.getSource().toString();
     int M = 0;
     if (E.printlnLimit > 1) {
     System.out.println(since() + "formInputMethodTextChanged source=" + source);
     }
     if (source.equals("StaffGrowthPerYear")) {
     E.staffGrowth = ((Number) StaffGrowthPerYear.getValue()).doubleValue();
     StaffDeaths1.setValue(new Integer((int) E.staffGrowth));
     System.out.println(since() + "InputInputMethodTextChanged StaffGrowthPerYear=" + whole.format(E.staffGrowth));
     } else if (source.equals("StaffDeathsPerYear")) {
     E.staffDeathRate = ((Number) StaffDeathsPerYear.getValue()).doubleValue();
     } else if (source.equals("logDisplay1Start")) {
     M = eM.logEnvirn[0].logM[0] = format.parse(evt.getText().toString()).intValue();
     setLogM(0, M);
     System.out.println(since() + "InputInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[0].logM[0]));
     } else if (source.equals("logDisplay2Start")) {
     M = eM.logEnvirn[1].logM[1] = format.parse(evt.getText().toString()).intValue();
     setLogM(1, M);
     System.out.println(since() + "InputInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[1].logM[1]));

     } else if (source.equals("log")) {
     double d = ((Number) evt.getText()).doubleValue();
     System.out.println(since() + " formInputMethodTextChanged log=" + whole.format(d));
     } else {
     double d = ((Number) evt.getText()).doubleValue();
     System.out.println(since() + " formInputMethodTextChanged Unknown=" + source + ", val=" + whole.format(d));

     }
     } catch (Exception ex) {
        eM.flushes();
      System.err.println(Econ.nowName +  since() + " " + Econ.nowThread  + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " +EM.andMore());
      ex.printStackTrace(System.err);   
        eM.flushes();    
      setFatalError();
    }
     */
  }//GEN-LAST:event_formInputMethodTextChanged

  private void formCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_formCaretPositionChanged
    String sss = evt.toString();
    System.out.println(since() + " formCaretPositionChanged=" + sss);
  }//GEN-LAST:event_formCaretPositionChanged

  private void LogsInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_LogsInputMethodTextChanged
    try {
      String source = evt.getSource().toString();
      NumberFormat whole = NumberFormat.getNumberInstance();
      whole.setMaximumFractionDigits(0);
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int M = 0;

      if (source.equals("logDisplay1Start")) {
        M = eM.logEnvirn[0].logM[0] = format.parse(evt.getText().toString()).intValue();
        setLogM(0, M);
        System.out.println(since() + "LogsInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[0].logM[0]));
      } else if (source.equals("logDisplay2Start")) {
        M = eM.logEnvirn[1].logM[1] = format.parse(evt.getText().toString()).intValue();
        setLogM(1, M);
        System.out.println(since() + "LogsInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[1].logM[1]));
      } else if (source.equals("StaffDeathsPerYear")) {
        //      E.staffDeathRate[0] = ((Number) StaffDeathsPerYear.getValue()).doubleValue();
      } else {
        M = format.parse(evt.getText().toString()).intValue();
        System.out.println(since() + "LogsInputMethodTextChanged unknown=" + source + ", value=" + M);
      }
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();

    }
  }//GEN-LAST:event_LogsInputMethodTextChanged

  private void logM2SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logM2SpinnerStateChanged
    try {
      String sss = evt.toString();
      JSpinner source = (JSpinner) evt.getSource();
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int m = format.parse(source.getValue().toString()).intValue();
      int start2 = format.parse(logM2Spinner.getValue().toString()).intValue();
      System.out.println(since() + " logM2SpinnerStateChanged=" + "m=" + m + "start2=" + start2 + eM.logEnvirn[1].logM[1] + " lev=" + eM.logEnvirn[1].logLev[1] + " Second bunch=" + eM.logEnvirn[1].logLen[1]);
      setLogM(1, m);
      displayLog();
      System.out.println(since() + " logM2SpinnerStateChanged=" + eM.logEnvirn[1].logM[1] + " lev=" + eM.logEnvirn[1].logLev[1] + " first bunch=" + eM.logEnvirn[1].logLen[1]);
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();

    }
  }//GEN-LAST:event_logM2SpinnerStateChanged

  private void logDLevel2SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logDLevel2SliderStateChanged
    try {
      JSlider source = (JSlider) evt.getSource();
      if (!source.getValueIsAdjusting()) {
        int m = (int) source.getValue();
        saveLogLev(1, m);
        displayLog();
        System.out.println(since() + " levelSlider2StateChanged=" + eM.logEnvirn[1].logLev[1]);
      }
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();
    }
  }//GEN-LAST:event_logDLevel2SliderStateChanged

  private void LogDLen2SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_LogDLen2SliderStateChanged
    try {
      JSlider source = (JSlider) evt.getSource();
      if (!source.getValueIsAdjusting()) {
        int m = (int) source.getValue();
        saveLogLen(1, m);
        System.out.println(since() + " lengthSlider2StateChanged=" + eM.logEnvirn[1].logLen[1]);
        displayLog();
      }

    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;

      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      eM.flushes();
      setFatalError();
    }
  }//GEN-LAST:event_LogDLen2SliderStateChanged

  private void logDisplay1StartStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logDisplay1StartStateChanged
    try {
      String sss = evt.toString();
      JSpinner source = (JSpinner) evt.getSource();
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int m = format.parse(source.getValue().toString()).intValue();
      saveLogM(0, m);
      //   displayHistoryFirstBunch = historyDisplay1Length.getValue();
      int start2 = format.parse(logM1Spinner.getValue().toString()).intValue();
      System.out.println(since() + " logDisplay1StartStateChanged=" + eM.logEnvirn[0].logM[0] + " lev=" + eM.logEnvirn[0].logLev[0] + " start2=" + start2);

    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();
    }

  }//GEN-LAST:event_logDisplay1StartStateChanged

  private void logDLevel1SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logDLevel1SliderStateChanged
    JSlider source = (JSlider) evt.getSource();
    if (!source.getValueIsAdjusting()) {
      int m = (int) source.getValue();
      saveLogLev(0, m);
      displayLog();
      System.out.println(since() + " levelSlider1StateChanged=" + E.logLev[0]);

    }
  }//GEN-LAST:event_logDLevel1SliderStateChanged

  private void LogDlen1SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_LogDlen1SliderStateChanged
    try {
      JSlider source = (JSlider) evt.getSource();
      if (!source.getValueIsAdjusting()) {
        int m = (int) source.getValue();
        saveLogLen(0, m);
        displayLog();
        System.out.println(since() + " DLen1StateChanged=" + m);
      }
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println("===================" + Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      eM.flushes();
      setFatalError();

    }
  }//GEN-LAST:event_LogDlen1SliderStateChanged

  private void logTableScrollPanelCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_logTableScrollPanelCaretPositionChanged
    String source = evt.toString();
    System.out.println(since() + " jScrollPane1CaretPositionChanged" + source);
  }//GEN-LAST:event_logTableScrollPanelCaretPositionChanged

  private void logTableScrollPanelInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_logTableScrollPanelInputMethodTextChanged
    String sss = evt.toString();
    System.out.println(since() + " jScrollPane1InputMethodTextChanged=" + sss);
  }//GEN-LAST:event_logTableScrollPanelInputMethodTextChanged

  private void logEnvirnNamesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logEnvirnNamesListMouseClicked
    // TODO add your handling code here:
  }//GEN-LAST:event_logEnvirnNamesListMouseClicked

  private void LogsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogsMouseClicked
    displayLog();
  }//GEN-LAST:event_LogsMouseClicked

  private void logDisplay1StartMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logDisplay1StartMouseReleased
    displayLog();
  }//GEN-LAST:event_logDisplay1StartMouseReleased

  private void logM2SpinnerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logM2SpinnerMouseReleased
    displayLog();
  }//GEN-LAST:event_logM2SpinnerMouseReleased

  private void logRadioButtonStart1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logRadioButtonStart1ItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = logRadioButtonStart1.isSelected();
    if (bid) {
      E.dN = 0;
    }
    System.out.println("logButton1ItemStateChanged state=" + st + ", ID=" + eID + ", bid=" + bid + ", dn=" + E.dN);
  }//GEN-LAST:event_logRadioButtonStart1ItemStateChanged

  private void logRadioButtonStart2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logRadioButtonStart2ItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = logRadioButtonStart2.isSelected();
    if (bid) {
      E.dN = 1;
    }
    System.out.println("logButton2ItemStateChanged state=" + st + ", ID=" + eID + ", bid=" + bid + ", dn=" + E.dN);
  }//GEN-LAST:event_logRadioButtonStart2ItemStateChanged

  private void logActionAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logActionAddActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_logActionAddActionPerformed

  private void logActionDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logActionDelActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_logActionDelActionPerformed

  private void logActionJumpItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logActionJumpItemStateChanged
    // TODO add your handling code here:
  }//GEN-LAST:event_logActionJumpItemStateChanged

  private void gameTopRightFillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameTopRightFillActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_gameTopRightFillActionPerformed

    private void gameTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameTextField1ActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_gameTextField1ActionPerformed

  private void clanRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanRedMouseEntered
    setgameTextField("Display entry options for the clan Red");
  }//GEN-LAST:event_clanRedMouseEntered

  private void clanRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanRedMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanRedMouseExited

  private void controlPanelsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_controlPanelsPropertyChange
    Object source = evt.getSource();
    JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
    int ix = sourceTabbedPane.getSelectedIndex();
    if (ix == 2) {
      statsButton0.setSelected(true);
      statsButton0.setToolTipText(statsButton0Tip);
    } else if (ix == 1) {
      gameMaster.setSelected(true);
      gameTextField.setText("Game Master set options for the overall game, note that there are many options to make planets or ships die quickly");
    } else if (ix == 3) {

    }

  }//GEN-LAST:event_controlPanelsPropertyChange

  private void clanRedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanRedItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanRed.isSelected();
    if (bid) {
      gamePanelChange(0, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    }
    System.out.println("clanRed=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanRedItemStateChanged

  private void gameMasterMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameMasterMouseEntered
    setgameTextField("Display entry options for the game Master");
  }//GEN-LAST:event_gameMasterMouseEntered

  private void gameMasterMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameMasterMouseExited
    revertgameTextField();
  }//GEN-LAST:event_gameMasterMouseExited

  private void clanOrangeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanOrangeMouseEntered
    setgameTextField("Display entry options for clan Orange");
  }//GEN-LAST:event_clanOrangeMouseEntered

  private void clanOrangeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanOrangeMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanOrangeMouseExited

  private void clanYellowMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanYellowMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanYellowMouseExited

  private void clanYellowMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanYellowMouseEntered
    setgameTextField("Display entry options for clan Yellow");
  }//GEN-LAST:event_clanYellowMouseEntered

  private void clanGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanGreenMouseEntered
    setgameTextField("Display entry options for clan Green");
  }//GEN-LAST:event_clanGreenMouseEntered

  private void clanGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanGreenMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanGreenMouseExited

  private void clanBlueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanBlueMouseEntered
    setgameTextField("Display entry options for clan Blue");
  }//GEN-LAST:event_clanBlueMouseEntered

  private void clanBlueMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanBlueMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanBlueMouseExited

  private void gameCtlButtonRun5Years1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun5Years1MouseEntered
    setgameTextField("Run all economies for 5 years");
  }//GEN-LAST:event_gameCtlButtonRun5Years1MouseEntered

  private void gameCtlButtonRun5Years1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun5Years1MouseExited
    revertgameTextField();
  }//GEN-LAST:event_gameCtlButtonRun5Years1MouseExited

  private void gameCtlButtonRun1Year1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1MouseEntered
    setgameTextField("Run all economies for 1 year");
  }//GEN-LAST:event_gameCtlButtonRun1Year1MouseEntered

  private void gameCtlButtonRun1Year1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1MouseExited
    revertgameTextField();
  }//GEN-LAST:event_gameCtlButtonRun1Year1MouseExited

  private void gameMasterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gameMasterItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = gameMaster.isSelected();

    if (bid) {
      setgameTextField("Player Red options");
      gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
      System.out.println("gameMaster=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus + " number=" + eM.gameDisplayNumber[eM.gameClanStatus]);
    }

  }//GEN-LAST:event_gameMasterItemStateChanged

  private void clanOrangeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanOrangeItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanOrange.isSelected();

    if (bid) {
      setgameTextField("Player Orange options");
      System.out.println("clanOrange=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus + "=>1" + " number=" + eM.gameDisplayNumber[1]);
      gamePanelChange(1, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
    }

  }//GEN-LAST:event_clanOrangeItemStateChanged

  private void clanYellowItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanYellowItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanYellow.isSelected();

    if (bid) {
      setgameTextField("Player Yellow options");
      gamePanelChange(2, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
    }
    System.out.println("clanYellow=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanYellowItemStateChanged

  private void clanGreenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanGreenItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanGreen.isSelected();
    if (bid) {
      setgameTextField("Player Green options");
      gamePanelChange(3, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
    }
    System.out.println("clanGreene=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanGreenItemStateChanged

  private void clanBlueItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanBlueItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanBlue.isSelected();

    if (bid) {
      setgameTextField("Player Blue options");
      gamePanelChange(4, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    }
    setGameButtonColors();
    System.out.println("clanBlue =" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanBlueItemStateChanged

  private void gameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_gameComponentShown
    boolean bid = evt.getSource().equals(game);
    if (bid) {

      setgameTextField("Start options input for Game Master");
      gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    }
    setGameButtonColors();
    System.out.println("game componentShown bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
    printMem3();
  }//GEN-LAST:event_gameComponentShown

  private void gameCtlButtonRun1Year1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1MouseClicked
    System.out.println("in Run1Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    } else {
      runYears(1);
    }
  }//GEN-LAST:event_gameCtlButtonRun1Year1MouseClicked

  private void gameCtlButtonRun5Years1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun5Years1MouseClicked

    if (eM.fatalError) {
      setFatalError();
    } else {
      runYears(5);
    }
  }//GEN-LAST:event_gameCtlButtonRun5Years1MouseClicked

  private void gamePanel0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      setgameTextField(eM.getDetail(curVals[0]));
      gamePanel0.setToolTipText(eM.getDetail(curVals[0]));

    };
  }//GEN-LAST:event_gamePanel0MouseEntered

  private void gamePanel0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel0MouseExited

  private void gamePanel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
      gamePanel1.setToolTipText(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gamePanel1MouseEntered

  private void gamePanel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    }
  }//GEN-LAST:event_gamePanel1MouseExited

  private void gamePanel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
      gamePanel2.setToolTipText(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gamePanel2MouseEntered

  private void gamePanel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel2MouseExited
    if (gamePanel2.isEnabled() && curVals[2] < -1) {
      revertgameTextField();
    }
  }//GEN-LAST:event_gamePanel2MouseExited

  private void gameTextField1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
      gamePanel1.setToolTipText(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gameTextField1MouseEntered

  private void gameTextField1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    }
  }//GEN-LAST:event_gameTextField1MouseExited

  private void gameTextField0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      setgameTextField(eM.getDetail(curVals[0]));
      gamePanel0.setToolTipText(eM.getDetail(curVals[0]));
    };
  }//GEN-LAST:event_gameTextField0MouseEntered

  private void gameTextField0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField0MouseExited

  private void statsButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton2ItemStateChanged
    boolean bid = statsButton2.isSelected();
    if (bid) {
      statsButton2.setToolTipText(statsButton2Tip);
      statsField.setText(statsButton2Tip);
      listRes(2, resLoops, fullRes);
    }

  }//GEN-LAST:event_statsButton2ItemStateChanged

  private void statsButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton1ItemStateChanged
    boolean bid = statsButton1.isSelected();
    if (bid) {
      statsButton1.setToolTipText(statsButton1Tip);
      statsField.setText(statsButton1Tip);
      listRes(1, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton1ItemStateChanged

  private void statsButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton3ActionPerformed
    boolean bid = statsButton3.isSelected();
    if (bid) {
      statsButton1.setToolTipText(statsButton3Tip);
      statsField.setText(statsButton3Tip);
      listRes(3, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton3ActionPerformed

  private void statsButton4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton4ItemStateChanged
    boolean bid = statsButton4.isSelected();
    if (bid) {
      statsButton1.setToolTipText(statsButton4Tip);
      statsField.setText(statsButton4Tip);
      listRes(4, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton4ItemStateChanged

  private void gameSliderS0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      statsButton0.setToolTipText(eM.getDetail(curVals[0]));
      setgameTextField(eM.getDetail(curVals[0]));
    };
  }//GEN-LAST:event_gameSliderS0MouseEntered

  private void gameSliderS0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS0MouseExited

  private void gameSliderP0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      setgameTextField(eM.getDetail(curVals[0]));
    };
  }//GEN-LAST:event_gameSliderP0MouseEntered

  private void gameSliderP0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP0MouseExited

  private void gameSliderP1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gameSliderP1MouseEntered

  private void gameSliderP1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP1MouseExited

  private void gameSliderS1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gameSliderS1MouseEntered

  private void gameSliderS1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS1MouseExited

  private void gameTextField2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gameTextField2MouseEntered

  private void gameTextField2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField2MouseExited
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField2MouseExited

  private void gameSliderP2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gameSliderP2MouseEntered

  private void gameSliderP2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP2MouseExited
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP2MouseExited

  private void gameSliderS2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gameSliderS2MouseEntered

  private void gameSliderS2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS2MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS2MouseExited

  private void gameTextField3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField3MouseEntered
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      setgameTextField(eM.getDetail(curVals[3]));
    };
  }//GEN-LAST:event_gameTextField3MouseEntered

  private void gameTextField3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField3MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField3MouseExited

  private void gameSliderP3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP3MouseEntered
    if (gamePanel2.isEnabled() && curVals[3] > -1) {
      setgameTextField(eM.getDetail(curVals[3]));
    };
  }//GEN-LAST:event_gameSliderP3MouseEntered

  private void gameSliderP3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP3MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP3MouseExited

  private void gameSliderS3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS3MouseEntered
    if (gamePanel2.isEnabled() && curVals[3] > -1) {
      setgameTextField(eM.getDetail(curVals[3]));
    };
  }//GEN-LAST:event_gameSliderS3MouseEntered

  private void gameSliderS3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS3MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS3MouseExited

  private void gamePanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gamePanel4MouseEntered

  private void gamePanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel4MouseExited

  private void gameTextField4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gameTextField4MouseEntered

  private void gameTextField4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField4MouseExited

  private void gameSliderP4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gameSliderP4MouseEntered

  private void gameSliderP4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP4MouseExited

  private void gameSliderS4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gameSliderS4MouseEntered

  private void gameSliderS4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS4MouseExited

  private void gamePanel5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    };
  }//GEN-LAST:event_gamePanel5MouseEntered

  private void gamePanel5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel5MouseExited

  private void gameTextField5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    }
  }//GEN-LAST:event_gameTextField5MouseEntered

  private void gameTextField5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField5MouseExited

  private void gameSliderP5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    }
  }//GEN-LAST:event_gameSliderP5MouseEntered

  private void gameSliderP5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP5MouseExited

  private void gameSliderS5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    }
  }//GEN-LAST:event_gameSliderS5MouseEntered

  private void gameSliderS5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS5MouseExited

  private void gamePanel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gamePanel6MouseEntered

  private void gamePanel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel6MouseExited

  private void gameTextField6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gameTextField6MouseEntered

  private void gameTextField6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField6MouseExited

  private void gameSliderP6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gameSliderP6MouseEntered

  private void gameSliderP6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP6MouseExited

  private void gameSliderS6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gameSliderS6MouseEntered

  private void gameSliderS6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS6MouseExited

  private void statsButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton1ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsButton1ActionPerformed

  private void statsButton0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton0ItemStateChanged
    boolean bid = statsButton0.isSelected();
    if (bid) {
      statsField.setText(statsButton0Tip);
      listRes(0, resLoops, fullRes);
      statsButton0.setToolTipText(statsButton0Tip);
    }
  }//GEN-LAST:event_statsButton0ItemStateChanged

  private void statsButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton3ItemStateChanged
    boolean bid = statsButton3.isSelected();
    if (bid) {
      statsField.setText(statsButton3Tip);
      listRes(3, resLoops, fullRes);
      statsButton3.setToolTipText(statsButton3Tip);
    }
  }//GEN-LAST:event_statsButton3ItemStateChanged

  private void statsButton5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton5ItemStateChanged
    boolean bid = statsButton5.isSelected();
    if (bid) {
      statsField.setText(statsButton5Tip);
      listRes(5, resLoops, fullRes);
      statsButton5.setToolTipText(statsButton5Tip);
    }
  }//GEN-LAST:event_statsButton5ItemStateChanged

  private void gameTextField7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField7MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_gameTextField7MouseEntered

  private void gameTextField7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField7MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField7MouseExited

  private void gameSliderP7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP7MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_gameSliderP7MouseEntered

  private void gameSliderP7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP7MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP7MouseExited

  private void gameSliderS7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS7MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_gameSliderS7MouseEntered

  private void gameSliderS7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS7MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS7MouseExited

  private void jSeparator14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator14MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_jSeparator14MouseEntered

  private void jSeparator14MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator14MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_jSeparator14MouseExited

  private void gameTextField8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField8MouseEntered
    if (gamePanel8.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_gameTextField8MouseEntered

  private void gameTextField8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField8MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField8MouseExited

  private void gameSliderP8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP8MouseEntered
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_gameSliderP8MouseEntered

  private void gameSliderP8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP8MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP8MouseExited

  private void jSeparator15MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator15MouseEntered
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_jSeparator15MouseEntered

  private void jSeparator15MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator15MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_jSeparator15MouseExited

  private void gameSliderS8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS8MouseEntered
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_gameSliderS8MouseEntered

  private void gameSliderS8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS8MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS8MouseExited

  private void gameButtonDownMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonDownMouseEntered
    // TODO add your handling code here:
  }//GEN-LAST:event_gameButtonDownMouseEntered

  private void gameButtonDownMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonDownMouseExited
    // TODO add your handling code here:
  }//GEN-LAST:event_gameButtonDownMouseExited

  private void gameTextField9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField9MouseEntered
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      setgameTextField(eM.getDetail(curVals[9]));
    }
  }//GEN-LAST:event_gameTextField9MouseEntered

  private void gameTextField9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField9MouseExited
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField9MouseExited

  private void gameSliderP9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP9MouseEntered
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      setgameTextField(eM.getDetail(curVals[9]));
    }
  }//GEN-LAST:event_gameSliderP9MouseEntered

  private void gameSliderP9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP9MouseExited
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP9MouseExited

  private void gameSliderS9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS9MouseEntered
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      setgameTextField(eM.getDetail(curVals[9]));
    }
  }//GEN-LAST:event_gameSliderS9MouseEntered

  private void gameSliderS9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS9MouseExited
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS9MouseExited

  private void gameButtonUp1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonUp1MouseClicked
    setgameTextField("This is to be filled with descriptions of the field over which the mouse hovers");

    gamePanelChange(eM.gameClanStatus, -1, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
  }//GEN-LAST:event_gameButtonUp1MouseClicked

  private void gameButtonDownMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonDownMouseClicked
    setgameTextField("This is to be filled with descriptions of the field over which the mouse hovers");
    gamePanelChange(eM.gameClanStatus, +1, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    printMem3();
  }//GEN-LAST:event_gameButtonDownMouseClicked

  private void statsButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton10ActionPerformed
    boolean bid = statsButton10.isSelected();
    if (bid) {
      statsField.setText(statsButton10Tip);
      listRes(10, resLoops, fullRes);
      statsButton10.setToolTipText(statsButton10Tip);
    }
  }//GEN-LAST:event_statsButton10ActionPerformed

  private void statsButton5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton5MouseEntered
    statsField.setText(statsTips(5));
  }//GEN-LAST:event_statsButton5MouseEntered

  private void statsButton0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton0MouseEntered
    statsButton0.setToolTipText(statsTips(0));
  }//GEN-LAST:event_statsButton0MouseEntered

  private void statsButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton1MouseEntered
    statsButton1.setToolTipText(statsTips(1));
  }//GEN-LAST:event_statsButton1MouseEntered

  private void statsButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton2MouseEntered
    statsButton2.setToolTipText(statsTips(2));
  }//GEN-LAST:event_statsButton2MouseEntered

  private void statsButton3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton3MouseEntered
    statsButton3.setToolTipText(statsButton3Tip);
  }//GEN-LAST:event_statsButton3MouseEntered

  private void statsButton4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton4MouseEntered
    statsButton4.setToolTipText(statsTips(4));
  }//GEN-LAST:event_statsButton4MouseEntered

  private void statsButton6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton6MouseEntered
    statsButton6.setToolTipText(statsTips(6));
  }//GEN-LAST:event_statsButton6MouseEntered

  private void statsButton6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton6ItemStateChanged
    boolean bid = statsButton6.isSelected();
    if (bid) {
      statsField.setText(statsButton6Tip);
      listRes(6, resLoops, fullRes);
      statsButton6.setToolTipText(statsButton6Tip);
    }
  }//GEN-LAST:event_statsButton6ItemStateChanged

  private void statsButton7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton7ItemStateChanged
    boolean bid = statsButton7.isSelected();
    if (bid) {
      listRes(7, resLoops, fullRes);
      statsButton7.setToolTipText(statsTips(7));
      statsField.setText(statsTips(7));
    }
  }//GEN-LAST:event_statsButton7ItemStateChanged

  private void statsButton7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton7MouseEntered
    statsButton7.setToolTipText(statsTips(7));
  }//GEN-LAST:event_statsButton7MouseEntered

  private void statsButton8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton8ItemStateChanged
    boolean bid = statsButton8.isSelected();
    if (bid) {
      statsField.setText(statsTips(8));
      statsButton8.setToolTipText(statsTips(8));
      listRes(8, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton8ItemStateChanged

  private void statsButton8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton8MouseEntered
    statsButton8.setToolTipText(statsTips(8));
  }//GEN-LAST:event_statsButton8MouseEntered

  private void statsButton9ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton9ItemStateChanged
    boolean bid = statsButton9.isSelected();
    if (bid) {
      statsField.setText(statsTips(9));
      statsButton9.setToolTipText(statsTips(9));
      listRes(9, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton9ItemStateChanged

  private void statsButton9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton9MouseEntered
    statsButton9.setToolTipText(statsTips(9));
  }//GEN-LAST:event_statsButton9MouseEntered

  private void statsButton10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton10ItemStateChanged
    boolean bid = statsButton10.isSelected();
    if (bid) {
      statsField.setText(statsTips(10));
      statsButton10.setToolTipText(statsTips(10));
      listRes(10, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton10ItemStateChanged

  private void statsButton10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton10MouseEntered
    statsButton10.setToolTipText(statsTips(10));
  }//GEN-LAST:event_statsButton10MouseEntered

  private void statsButton11ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton11ItemStateChanged
    boolean bid = statsButton11.isSelected();
    if (bid) {
      statsField.setText(statsButton11Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(11, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton11ItemStateChanged

  private void statsButton11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton11MouseEntered
    statsButton11.setToolTipText(statsButton11Tip);
  }//GEN-LAST:event_statsButton11MouseEntered

  private void controlPanelsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_controlPanelsStateChanged
    JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
    int ix = sourceTabbedPane.getSelectedIndex();
    if (ix == 2) {
      statsButton0.setSelected(true);
      statsButton0.setToolTipText(statsTips(0));
      statsButton1.setToolTipText(statsTips(1));
      statsButton2.setToolTipText(statsTips(2));
      statsButton3.setToolTipText(statsTips(3));
      statsButton4.setToolTipText(statsTips(4));
      statsButton5.setToolTipText(statsTips(5));
      statsButton6.setToolTipText(statsTips(6));
      statsButton7.setToolTipText(statsTips(7));
      statsButton8.setToolTipText(statsTips(8));
      statsButton9.setToolTipText(statsTips(9));
      statsButton10.setToolTipText(statsTips(10));
      statsButton11.setToolTipText(statsTips(11));
      statsButton12.setToolTipText(statsTips(12));
      statsButton13.setToolTipText(statsTips(13));
      statsButton14.setToolTipText(statsTips(14));
      statsButton15.setToolTipText(statsTips(15));
      statsButton16.setToolTipText(statsTips(16));
      statsButton17.setToolTipText(statsTips(17));
      statsButton18.setToolTipText(statsTips(18));
      statsButton19.setToolTipText(statsTips(19));
      statsButton20.setToolTipText(statsTips(20));
    }
  }//GEN-LAST:event_controlPanelsStateChanged

  private void statsCtlButtonRun1YrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsCtlButtonRun1YrMouseClicked
    System.out.println("in stats Run 1Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    } else {
      runYears(1);
    }
  }//GEN-LAST:event_statsCtlButtonRun1YrMouseClicked

  private void statsCtlButtonRun1YrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsCtlButtonRun1YrActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsCtlButtonRun1YrActionPerformed

  private void statsCtlButtonRun5YrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsCtlButtonRun5YrMouseClicked
    System.out.println("in stats Run 5Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    } else {
      runYears(5);
    }
  }//GEN-LAST:event_statsCtlButtonRun5YrMouseClicked

  private void gameCtlButtonRun1Year1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_gameCtlButtonRun1Year1ActionPerformed

  private void statsButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton5ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsButton5ActionPerformed

  private void statsButton12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton12ItemStateChanged
    boolean bid = statsButton12.isSelected();
    if (bid) {
      statsField.setText(statsButton11Tip);
      statsButton12.setToolTipText(statsButton12Tip);
      listRes(12, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton12ItemStateChanged

  private void statsButton12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton12MouseEntered
    statsButton12.setToolTipText(statsButton12Tip);
  }//GEN-LAST:event_statsButton12MouseEntered

  private void statsButton13ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton13ItemStateChanged
    boolean bid = statsButton13.isSelected();
    if (bid) {
      statsField.setText(statsButton13Tip);
      statsButton13.setToolTipText(statsButton13Tip);
      listRes(13, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton13ItemStateChanged

  private void statsButton13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton13MouseEntered
    statsButton13.setToolTipText(statsButton13Tip);
  }//GEN-LAST:event_statsButton13MouseEntered

  private void statsButton14ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton14ItemStateChanged
    boolean bid = statsButton14.isSelected();
    if (bid) {
      statsField.setText(statsButton14Tip);
      statsButton14.setToolTipText(statsButton14Tip);
      listRes(14, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton14ItemStateChanged

  private void statsButton14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton14MouseEntered
    statsButton14.setToolTipText(statsButton14Tip);
  }//GEN-LAST:event_statsButton14MouseEntered

  private void statsButton15ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton15ItemStateChanged
    boolean bid = statsButton15.isSelected();
    if (bid) {
      statsField.setText(statsButton15Tip);
      statsButton15.setToolTipText(statsButton15Tip);
      listRes(15, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton15ItemStateChanged

  private void statsButton15MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton15MouseEntered
    statsButton15.setToolTipText(statsButton15Tip);
  }//GEN-LAST:event_statsButton15MouseEntered

  private void statsButton16ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton16ItemStateChanged
    boolean bid = statsButton16.isSelected();
    if (bid) {
      statsField.setText(statsButton16Tip);
      statsButton16.setToolTipText(statsButton16Tip);
      listRes(16, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton16ItemStateChanged

  private void statsButton16MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton16MouseEntered
    statsButton16.setToolTipText(statsButton16Tip);
  }//GEN-LAST:event_statsButton16MouseEntered

  private void statsButton17ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton17ItemStateChanged
    boolean bid = statsButton17.isSelected();
    if (bid) {
      statsField.setText(statsButton17Tip);
      statsButton17.setToolTipText(statsButton17Tip);
      listRes(17, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton17ItemStateChanged

  private void statsButton17MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton17MouseEntered
    statsButton17.setToolTipText(statsButton17Tip);
  }//GEN-LAST:event_statsButton17MouseEntered

  private void statsButton18ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton18ItemStateChanged
    boolean bid = statsButton18.isSelected();
    if (bid) {
      statsField.setText(statsButton18Tip);
      statsButton18.setToolTipText(statsButton18Tip);
      listRes(18, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton18ItemStateChanged

  private void statsButton18MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton18MouseEntered
    statsButton18.setToolTipText(statsButton18Tip);
  }//GEN-LAST:event_statsButton18MouseEntered

  private void statsButton19ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton19ItemStateChanged
    boolean bid = statsButton19.isSelected();
    if (bid) {
      statsField.setText(statsButton19Tip);
      statsButton19.setToolTipText(statsButton19Tip);
      listRes(19, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton19ItemStateChanged

  private void statsButton19MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton19MouseEntered
    statsButton19.setToolTipText(statsButton19Tip);
  }//GEN-LAST:event_statsButton19MouseEntered

  private void statsButton20ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton20ItemStateChanged
    boolean bid = statsButton20.isSelected();
    if (bid) {
      statsField.setText(statsButton20Tip);
      statsButton20.setToolTipText(statsButton20Tip);
      listRes(20, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton20ItemStateChanged

  private void statsButton20MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton20MouseEntered
    statsButton20.setToolTipText(statsButton20Tip);

  }//GEN-LAST:event_statsButton20MouseEntered

  private void statsField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsField2ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsField2ActionPerformed

  private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    System.exit(1);
  }//GEN-LAST:event_formWindowClosed

  private void statsCtlButtonRun10YrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsCtlButtonRun10YrMouseClicked
    System.out.println("in stats Run 10Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    } else {
      runYears(10);
    }
  }//GEN-LAST:event_statsCtlButtonRun10YrMouseClicked

  private void statsCtlButtonRun20YrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsCtlButtonRun20YrMouseClicked
    System.out.println("in stats Run 20Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    } else {
      runYears(20);
    }
  }//GEN-LAST:event_statsCtlButtonRun20YrMouseClicked

  private void statsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsFieldActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsFieldActionPerformed

  private void statsButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton0ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsButton0ActionPerformed

  private void settingsKeepMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsKeepMouseClicked
    EM.keepFromPage = true;
    setgameTextField(EM.keepInstruct);   // keepInstruct
  }//GEN-LAST:event_settingsKeepMouseClicked

  private void rememberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rememberMouseClicked
    EM.rememberFromPage = true;
  }//GEN-LAST:event_rememberMouseClicked

  private void statsStopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsStopMouseClicked
    setStopExe();
  }//GEN-LAST:event_statsStopMouseClicked

  private void settingsStopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsStopMouseClicked
    setStopExe();
  }//GEN-LAST:event_settingsStopMouseClicked

  private void settingsKeepMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsKeepMouseEntered
    setgameTextField(EM.keepInstruct);   // keepInstruct
  }//GEN-LAST:event_settingsKeepMouseEntered

  private void settingsKeepMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsKeepMouseExited

    revertgameTextField();
  }//GEN-LAST:event_settingsKeepMouseExited

  private void settingsStopMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsStopMouseEntered
    setgameTextField("click to do an orderly stop of StarTrader");
  }//GEN-LAST:event_settingsStopMouseEntered

  private void settingsStopMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsStopMouseExited
    revertgameTextField();
  }//GEN-LAST:event_settingsStopMouseExited

  private void rememberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rememberActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_rememberActionPerformed

  public void second() throws IOException {
    System.err.println("starting out in Second");
    if (!fatalError) { // whoo, stop now
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          new StarTrader().setVisible(true);
        }
      });
    }
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  protected javax.swing.JSlider LogDLen2Slider;
  protected javax.swing.JSlider LogDlen1Slider;
  protected javax.swing.JLabel Start1Name;
  protected javax.swing.JLabel Start2Name;
  protected javax.swing.JPanel bigPanel;
  protected javax.swing.JPanel clan;
  protected javax.swing.JRadioButton clanBlue;
  protected javax.swing.ButtonGroup clanButtonGroupActiveClan;
  protected javax.swing.JRadioButton clanGreen;
  protected javax.swing.JLabel clanLabelP3;
  protected javax.swing.JLabel clanLabelP4;
  protected javax.swing.JLabel clanLabelS4;
  protected javax.swing.JRadioButton clanOrange;
  protected javax.swing.JPanel clanPanel0;
  protected javax.swing.JPanel clanPanel1;
  protected javax.swing.JPanel clanPanel2;
  protected javax.swing.JPanel clanPanel3;
  protected javax.swing.JPanel clanPanel4;
  protected javax.swing.JRadioButton clanRed;
  protected javax.swing.JSlider clanSliderP0;
  protected javax.swing.JSlider clanSliderP1;
  protected javax.swing.JSlider clanSliderP2;
  protected javax.swing.JSlider clanSliderP3;
  protected javax.swing.JSlider clanSliderP4;
  protected javax.swing.JSlider clanSliderS0;
  protected javax.swing.JSlider clanSliderS1;
  protected javax.swing.JSlider clanSliderS2;
  protected javax.swing.JSlider clanSliderS3;
  protected javax.swing.JSlider clanSliderS4;
  protected javax.swing.JTextArea clanTextField;
  protected javax.swing.JTextField clanTextField0;
  protected javax.swing.JTextField clanTextField1;
  protected javax.swing.JTextField clanTextField2;
  protected javax.swing.JTextField clanTextField3;
  protected javax.swing.JTextField clanTextField4;
  protected javax.swing.JScrollPane clanTextPane;
  protected javax.swing.JRadioButton clanYellow;
  protected javax.swing.JTabbedPane controlPanels;
  protected javax.swing.JPanel display;
  protected javax.swing.JPanel displayPanel0;
  protected javax.swing.JTextArea displayPanel0Text;
  protected javax.swing.JPanel displayPanel1;
  protected javax.swing.JPanel game;
  protected java.awt.Button gameButtonDown;
  protected javax.swing.ButtonGroup gameButtonGroup;
  protected java.awt.Button gameButtonUp;
  protected java.awt.Button gameButtonUp1;
  protected javax.swing.JButton gameCtlButtonRun1Year1;
  protected javax.swing.JButton gameCtlButtonRun5Years1;
  protected javax.swing.JLabel gameLabelP5;
  protected javax.swing.JLabel gameLabelP6;
  protected javax.swing.JLabel gameLabelP7;
  protected javax.swing.JLabel gameLabelS5;
  protected javax.swing.JLabel gameLabelS6;
  protected javax.swing.JLabel gameLabelS7;
  protected javax.swing.JLabel gameLabelS8;
  protected javax.swing.JRadioButton gameMaster;
  protected javax.swing.JPanel gamePanel0;
  protected javax.swing.JPanel gamePanel1;
  protected javax.swing.JPanel gamePanel2;
  protected javax.swing.JPanel gamePanel3;
  protected javax.swing.JPanel gamePanel4;
  protected javax.swing.JPanel gamePanel5;
  protected javax.swing.JPanel gamePanel6;
  protected javax.swing.JPanel gamePanel7;
  protected javax.swing.JPanel gamePanel8;
  protected javax.swing.JPanel gamePanel9;
  protected javax.swing.JPanel gamePanelBottomPanel;
  protected javax.swing.JSlider gameSliderP0;
  protected javax.swing.JSlider gameSliderP1;
  protected javax.swing.JSlider gameSliderP2;
  protected javax.swing.JSlider gameSliderP3;
  protected javax.swing.JSlider gameSliderP4;
  protected javax.swing.JSlider gameSliderP5;
  protected javax.swing.JSlider gameSliderP6;
  protected javax.swing.JSlider gameSliderP7;
  protected javax.swing.JSlider gameSliderP8;
  protected javax.swing.JSlider gameSliderP9;
  protected javax.swing.JSlider gameSliderS0;
  protected javax.swing.JSlider gameSliderS1;
  protected javax.swing.JSlider gameSliderS2;
  protected javax.swing.JSlider gameSliderS3;
  protected javax.swing.JSlider gameSliderS4;
  protected javax.swing.JSlider gameSliderS5;
  protected javax.swing.JSlider gameSliderS6;
  protected javax.swing.JSlider gameSliderS7;
  protected javax.swing.JSlider gameSliderS8;
  protected javax.swing.JSlider gameSliderS9;
  protected javax.swing.JTextArea gameTextField;
  protected javax.swing.JTextField gameTextField0;
  protected javax.swing.JTextField gameTextField1;
  protected javax.swing.JTextField gameTextField2;
  protected javax.swing.JTextField gameTextField3;
  protected javax.swing.JTextField gameTextField4;
  protected javax.swing.JTextField gameTextField5;
  protected javax.swing.JTextField gameTextField6;
  protected javax.swing.JTextField gameTextField7;
  protected javax.swing.JTextField gameTextField8;
  protected javax.swing.JTextField gameTextField9;
  protected javax.swing.JScrollPane gameTextPane;
  protected javax.swing.JLabel gameToLabelPlanet;
  protected javax.swing.JLabel gameTopLabelShip;
  protected javax.swing.JTextField gameTopRightFill;
  protected javax.swing.JPanel gameXtraPanel1;
  protected javax.swing.ButtonGroup initButtonGroupPorS;
  protected javax.swing.JScrollPane jScrollPane1;
  protected javax.swing.JScrollPane jScrollPane3;
  protected javax.swing.JSeparator jSeparator1;
  protected javax.swing.JSeparator jSeparator10;
  protected javax.swing.JSeparator jSeparator11;
  protected javax.swing.JSeparator jSeparator13;
  protected javax.swing.JSeparator jSeparator14;
  protected javax.swing.JSeparator jSeparator15;
  protected javax.swing.JSeparator jSeparator16;
  protected javax.swing.JSeparator jSeparator2;
  protected javax.swing.JSeparator jSeparator3;
  protected javax.swing.JSeparator jSeparator4;
  protected javax.swing.JSeparator jSeparator5;
  protected javax.swing.JSeparator jSeparator6;
  protected javax.swing.JSeparator jSeparator7;
  protected javax.swing.JSeparator jSeparator8;
  protected javax.swing.JSeparator jSeparator9;
  protected javax.swing.JTable jTable1;
  protected javax.swing.JRadioButton logActionAdd;
  protected javax.swing.JRadioButton logActionDel;
  protected javax.swing.JRadioButton logActionJump;
  protected javax.swing.ButtonGroup logBGactions;
  protected javax.swing.ButtonGroup logButtonGroup1or2;
  protected javax.swing.JSlider logDLevel1Slider;
  protected javax.swing.JSlider logDLevel2Slider;
  protected javax.swing.JTable logDisplayTable;
  protected javax.swing.JLabel logDlen1;
  protected javax.swing.JLabel logDlen2;
  protected javax.swing.JLabel logDlevel1;
  protected javax.swing.JLabel logDlevel2;
  protected javax.swing.DefaultListModel namesList;
  protected javax.swing.JList logEnvirnNamesList;
  protected javax.swing.JSpinner logM1Spinner;
  protected javax.swing.JSpinner logM2Spinner;
  protected javax.swing.JScrollPane logNamesScrollPanel;
  protected javax.swing.JRadioButton logRadioButtonStart1;
  protected javax.swing.JRadioButton logRadioButtonStart2;
  protected javax.swing.JScrollPane logTableScrollPanel;
  protected javax.swing.JPanel rbuttons1;
  protected javax.swing.JPanel rbuttons12;
  protected javax.swing.JPanel rbuttons2;
  protected javax.swing.JButton remember;
  protected javax.swing.JTextArea settingsComment;
  protected javax.swing.JButton settingsKeep;
  protected javax.swing.JButton settingsStop;
  protected javax.swing.JPanel stats;
  protected javax.swing.JRadioButton statsButton0;
  protected javax.swing.JRadioButton statsButton1;
  protected javax.swing.JRadioButton statsButton10;
  protected javax.swing.JRadioButton statsButton11;
  protected javax.swing.JRadioButton statsButton12;
  protected javax.swing.JRadioButton statsButton13;
  protected javax.swing.JRadioButton statsButton14;
  protected javax.swing.JRadioButton statsButton15;
  protected javax.swing.JRadioButton statsButton16;
  protected javax.swing.JRadioButton statsButton17;
  protected javax.swing.JRadioButton statsButton18;
  protected javax.swing.JRadioButton statsButton19;
  protected javax.swing.JRadioButton statsButton2;
  protected javax.swing.JRadioButton statsButton20;
  protected javax.swing.JRadioButton statsButton3;
  protected javax.swing.JRadioButton statsButton4;
  protected javax.swing.JRadioButton statsButton5;
  protected javax.swing.JRadioButton statsButton6;
  protected javax.swing.JRadioButton statsButton7;
  protected javax.swing.JRadioButton statsButton8;
  protected javax.swing.JRadioButton statsButton9;
  protected javax.swing.ButtonGroup statsButtonGroupClans;
  protected javax.swing.ButtonGroup statsButtonGroupReportNumber;
  protected javax.swing.JScrollPane statsCommentPane;
  protected javax.swing.JButton statsCtlButtonRun10Yr;
  protected javax.swing.JButton statsCtlButtonRun1Yr;
  protected javax.swing.JButton statsCtlButtonRun20Yr;
  protected javax.swing.JButton statsCtlButtonRun5Yr;
  protected javax.swing.JTextField statsField;
  protected javax.swing.JTextField statsField2;
  protected javax.swing.JTextArea statsRememberWhy;
  protected javax.swing.JScrollPane statsScrollPane2;
  protected javax.swing.JButton statsStop;
  protected javax.swing.JTable statsTable1;
  protected javax.swing.JPanel story;
  protected javax.swing.JTextArea storyTextField1;
  protected javax.swing.JScrollPane storyTextPane;
  protected javax.swing.JPanel topPane;
  protected javax.swing.JPanel ybuttons;
  // End of variables declaration//GEN-END:variables

  private void logM1SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
    try {
      String sss = evt.toString();
      JSpinner source = (JSpinner) evt.getSource();
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int m = format.parse(source.getValue().toString()).intValue();
      int start2 = format.parse(logM1Spinner.getValue().toString()).intValue();
      System.out.println(since() + " logM1SpinnerStateChanged=" + eM.logEnvirn[0].logM[0] + " lev=" + eM.logEnvirn[0].logLev[0] + "m=" + m + " start2=" + start2);
      saveLogM(0, m);
      displayLog();

    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();

    }

  }

  public void logM1MouseClicked(java.awt.event.MouseEvent evt) {
    try {
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();

    }
  }

  String statsTips(int num) {
    String rtn = "Year " + eM.year + " :" + statsButtonsTips[num];
    return rtn;
  }

  Color redish = new Color(255, 153, 153);

  /**
   * stop core execution by callin setFatalError(Color);
   *
   */
  void setFatalError() {
    setFatalError(redish);
  }

  /**
   * stop core program execution but not triggers from buttons etc.
   *
   */
  void setStopExe() {
    doStop = true;
    EM.stopExe = true;
    getGameValues(curVals, gamePanels, gameTextFields, gameSlidersP, gameSlidersS);
    EM.flushes();
  }

  /**
   * stop all execution of interrupts and core program by setting the
   * eM.fatalError flag and eM.stopExe
   *
   * @param rrr color to set in log table and other tab views
   */
  void setFatalError(Color rrr) {
    boolean hadFatalError = false;
    getGameValues(curVals, gamePanels, gameTextFields, gameSlidersP, gameSlidersS);
    EM.flushes();
    if (EM.dfe()) {
      hadFatalError = true;
    }
    eM.stopExe = true;
    eM.fatalError = true;
    doStop = fatalError = true;
    // change the background color of log to red for fatal error
    Color redish = new Color(255, 204, 204);
    Color r4 = new Color(255, 204, 154);
    if (eM.curEcon != null) {
      setLogEnvirn(0, eM.curEcon);
      setLogEnvirn(1, eM.curEcon);
      //eM.logEnvirn[0] = eM.curEcon;
      // eM.logEnvirn[1] = eM.curEcon;
      eM.hists[0] = eM.logEnvirn[0].hists[0];
      eM.hists[1] = eM.logEnvirn[1].hists[0];
      eM.logEnvirn[0].logLen[0] = 90;
      LogDlen1Slider.setValue(90);
      eM.logEnvirn[0].logLen[1] = 5;
      eM.logEnvirn[0].logLev[0] = 15;
      eM.logEnvirn[0].logLev[1] = 15;
      eM.hists[0].add(new History(3, "final string", "ERROR ==============================="));
      int siz = eM.hists[0].size();
      int siz1 = siz - 87;
      siz = siz1 < 0 ? 0 : siz1;
      eM.logEnvirn[0].logM[0] = siz;
      logM1Spinner.setValue(siz);
      // change to display the log of the erring Econ
      // do not try to display a log that does not exist
      Boolean ll = eM.logEnvirn[0].logM[0] > 50;
      if (ll) {
        eM.logEnvirn[0].logLev[0] = 20;
        logDLevel1Slider.setValue(20);
        displayLog();
      }
    } //curEcon == null
    logDisplayTable.setBackground(rrr);
    logTableScrollPanel.setBackground(rrr);
    game.setBackground(redish);
    stats.setBackground(rrr);
    statsTable1.setBackground(redish);
    gameToLabelPlanet.setBackground(r4);
    gameTopLabelShip.setBackground(rrr);
    controlPanels.getComponent(0).setBackground(new Color(255, 204, 152));
    controlPanels.setBackground(rrr);
    display.setBackground(rrr);
    displayPanel1.setBackground(rrr);
    StackTraceElement[] aa = Thread.currentThread().getStackTrace();
    String histTrace = " ";
    for (int i = 1; i < 6 && i < aa.length - 1; i++) {
      histTrace += aa[i].getMethodName() + "." + aa[i].getFileName() + "." + aa[i].getLineNumber() + "\n";
    }
    System.err.println("================= setFatalError at" + histTrace + EM.andMore());
    controlPanels.getComponent(3);
    controlPanels.setSelectedIndex(3);
    displayPanel0Text.setRows(18);
    displayPanel0Text.setText("setFatalError at" + histTrace + EM.andMore());
    EM.flushes();
    EM.flushes();
    EM.flushes();
    //    E.sysmsg("CF construct " + E.ROYGB.charAt(clan) + " " + name + " at " + a4.getMethodName() + ", " + a3.getMethodName() + ", " + a2.getMethodName() + " wealth=" + EM.mf(wealth));
    //  displayPanel2.setBackground(rrr);
    // displayPanel1EconName.setBackground(rrr);
    // displayPanel1Operation.setBackground(rrr);
    // displayPanel2EconName.setBackground(rrr);
    // displayPanel2Operation.setBackground(rrr);
    // displayPanel1Operation.setText("fatalError");
    controlPanels.revalidate();
    controlPanels.repaint();
    stateConst = FATALERR;

    System.err.println("--------FE-----request rejected, due to a  fatal error at \n" + histTrace + EM.andMore());
    EM.flushes();
    EM.flushes();
    EM.flushes();
    EM.flushes();
    EM.flushes();
    if (hadFatalError) {
      System.exit(-19);
    }
    System.exit(-18);
    EM.flushes();
    throw new WasFatalError("setFatalError threw WasFatalError" + EM.lfe() + "\n" + EM.secondStack);
    //  controlPanels.setSelectedComponent(log);
  }

  //obsolete
  // public SwingWorker worker = new SwingWorker<Boolean,Boolean>() {
  public class Worker extends SwingWorker<Boolean, Boolean> {

    @Override
    public Boolean doInBackground() throws Exception {
      runBackgroundYears4(yearsToRun);
      return true;
    }
  };  // end new SwingWorker

  /**
   * class of the animation thread call runYears2 which starts the background
   * thread the calls doYear then continues with animation depending on the
   * stateConst
   *
   */
  public class RunYrs1 extends Thread {

    public void run() {
      runYears2();
    }
  }

  /**
   * class of the background thread invokes runBackgroundYears4 which invokes
   * the proper number of runYear -> doYear which sets stateConst before each
   * different loop or routine in the year.
   */
  public class RunYrs3 extends Thread {

    public void run() {
      startRY3 = new Date().getTime();
      runBackgroundYears4(yearsToRun);

    }
  }

  Worker jake;

  /**
   * called from field tab stats or game by the method for the 1, 5,10, or 20
   * yr, also called by a continuation of the StarTrader constructor I think it
   * always runs in the EventDispatchThead, Start a animation thread and end The
   * animation thread continues and starts the background thread The background
   * thread ends after nYears setting state RUNSDONE don't return until nYears
   * have been run
   *
   * @param nYears number of do years to run
   */
  void runYears(int nYears) {
    try {
      System.err.println("-----AAa----- runYears;" + since() + " at start stateConst=" + stateConst + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + " year=" + eM.year + (javax.swing.SwingUtilities.isEventDispatchThread() ? " is eventDispatchThread" : " is not EventDispatchThread"));
      getGameValues(curVals, gamePanels, gameTextFields, gameSlidersP, gameSlidersS);
      stateConst = RUNNING;
      yearsToRun = nYears;
      EM.runYearsTime = (new Date()).getTime();
      System.err.println("-----AAb----- runYears;" + since() + " at start stateConst=" + stateConst + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + " year=" + eM.year + (javax.swing.SwingUtilities.isEventDispatchThread() ? " is eventDispatchThread" : " is not EventDispatchThread"));
      //   E.myTest(!javax.swing.SwingUtilities.isEventDispatchThread(), "not eventDispatchThread");
      RunYrs1 rYrs1 = new RunYrs1();
      rYrs1.setPriority(5);
      rYrs1.start();  // start runYears2 the annimation thread
      //    stateConst = STATS;
      // runBackgroundYears4(nYears);
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();
    }
  }

  String prevWasHere = "";
  int pprevState = 0;

  /**
   * in the annimation thread (runYears2) check the Econ state for stuck if no change in stateConst,EM.curEconName,EM.wasHere;
   * and prevState not STATS,  STOPPED, FATALERR, RUNSDONE
   *
   */
  void checkEconState() {
    ec = curEc = EM.curEcon;
    curEconName = (ec == null ? "noneYet" : ec.name == null? "noName": ec.name);
    prevEconName = prevEconName == null ? "aint named" : prevEconName;
    boolean sameName = prevEconName.equals(curEconName);
    String wh = EM.wasHere == null ? "wasn't here" : EM.wasHere;
    prevWasHere = prevWasHere == null ? "wasn't here" : prevWasHere;
    boolean sameWh = prevWasHere.equals(wh);
    int sc = stateConst;
    boolean sameState = stateConst == prevState;
    long myNow = new Date().getTime() - eM.curEconTime;
    if (sameState && sameName && sameWh
            && stateConst != STATS && stateConst != RUNSDONE && stateConst != STOPPED && stateConst != FATALERR) {
      sameEconState++;
      assert E.debugStuck && sameEconState < 51 : "STUCK at runYears2.checkEconState Year" + eM.year + " myNow=" + myNow + sinceRY2() + sinceRY3() + " " + stateStringNames[stateConst] + " " + EM.curEconName + " sameEconStatecnt=" + sameEconState + " millisecs=" + (new Date().getTime() - startEconState) + " main3 testing"+ " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + " cntr7=" + cntr7 + " cntr8=" + cntr8 +" cntr9=" + cntr9;
      if (false && E.debugStuck && sameEconState > 50) {
        EM.doMyErr("STUCK at:doYear" + EM.year + myNow + " " + stateStringNames[stateConst] + " " + EM.curEconName + ", cnt=" + sameEconState + " millisecs=" + (new Date().getTime() - startEconState));
      }
    } else {
      EM.wasHere5 = "----CES----in runYears2.setEconState Year" + EM.year +" " + Thread.currentThread().getName() + " econTime" + myNow + " " + stateStringNames[stateConst]+ "=> " + stateStringNames[stateConst] + " " + EM.curEconName + ", sameEconStatecnt=" + sameEconState + " millisecs=" + (new Date().getTime() - startEconState) + sinceRY2() + sinceRY3() + " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + " cntr7=" + cntr7 + " cntr8=" + cntr8 +" cntr9=" + cntr9;
      prevEconName = EM.curEconName;
      prevWasHere = EM.wasHere == null ? "no Was Here" : EM.wasHere; // move the reference
      stateCnt = 0;
      if(E.debugThreadsOut && prevState != stateConst && (true || stateConst == CREATING || stateConst == STATS || stateConst == TRADING || prevState == RUNSDONE || prevState == STATS)) System.out.println(EM.wasHere5 + EM.threadsStacks());
      pprevState = prevState;
      prevState = stateConst ;
      sameEconState = 0;  //zero no dif counter
      startEconState = (new Date()).getTime();
    }
  }

   /**
   * set the Econ state in the runBackgroundYears4, and check for stuck if no change in stateConst,EM.curEconName,EM.wasHere and stateConstA not STATS,  STOPPED, FATALERR, RUNSDONE  See also checkEconState for getting stuck
   *
   * @param stateConstA value to be set
   */
  void setEconState(int stateConstA) {
    ec = curEc = EM.curEcon;
    curEconName = (ec == null ? "noneYet" : ec.name == null? "noName": ec.name);
    prevEconName = prevEconName == null ? "aint named" : prevEconName;
    boolean sameName = prevEconName.equals(curEconName);
    String wh = EM.wasHere == null ? "wasn't here" : EM.wasHere;
    prevWasHere = prevWasHere == null ? "wasn't here" : prevWasHere;
    boolean sameWh = prevWasHere.equals(wh);
    int sc = stateConst;
    boolean sameState = stateConst == prevState;
    long myNow = new Date().getTime() - eM.curEconTime;
    if (sameState && sameName && sameWh
    && stateConstA != STATS && stateConstA != RUNSDONE && stateConstA != STOPPED && stateConstA != FATALERR) {
      sameEconState++;
      assert E.debugStuck && sameEconState < 51 : "STUCK at runYears2.setEconState Year" + EM.year  + sinceRY2() + sinceRY3() + sinceEcon() + " " + stateStringNames[stateConstA] + " " + EM.curEconName + ", sameEconStatecnt=" + sameEconState  + " main3 testing"+ " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + " cntr7=" + cntr7 + " cntr8=" + cntr8 +" cntr9=" + cntr9;
      if (false && E.debugStuck && sameEconState > 50) {
        eM.doMyErr("STUCK at:doYear" + eM.year + myNow + " " + stateStringNames[stateConstA] + " " + EM.curEconName + ", cnt=" + sameEconState + " millisecs=" + (new Date().getTime() - startEconState));
      }
    } else {
      EM.wasHere5 = "----SES----in runYears2.setEconState Year" + EM.year +" " + Thread.currentThread().getName() + " " + stateStringNames[stateConst]+ "=> " + stateStringNames[stateConstA] + " " + EM.curEconName + ", sameEconStatecnt=" + sameEconState + " millisecs=" + sinceRY2() + sinceRY3() + sinceEcon() + " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + " cntr7=" + cntr7 + " cntr8=" + cntr8 +" cntr9=" + cntr9;
      prevEconName = EM.curEconName;
      prevWasHere = EM.wasHere == null ? "no Was Here" : EM.wasHere; // move the reference
      stateCnt = 0;
      if(E.debugThreadsOut && (stateConstA != stateConst || prevState != stateConst ) && (stateConstA == CREATING || stateConstA == STATS || stateConstA == TRADING || prevState == RUNSDONE || stateConst == STATS || stateConst == RUNSDONE || prevState == STATS)) System.out.println(EM.wasHere5 + EM.threadsStacks());
      pprevState = prevState;
      prevState = stateConst = stateConstA;
      sameEconState = 0;  //zero no dif counter
      startEconState = (new Date()).getTime();
    }
  }

  /**
   * start of the animation thread waiting between calls of paintCurDisplay
   * thread RunYrs1 Then it watches stateConst and updates the display tab
   *
   */
  void runYears2() {
    try {
      ec = curEc = eM.curEcon;;
      startRY2 = (new Date()).getTime();  //started runYears2
      if(E.debugStatsOut1) {
        System.out.println(EM.wasHere3 = "-------MA--------runYears2;" + sinceA() + " at start" + " cnt" + stateCnt + stateStringNames[stateConst] + "Y" + eM.year);
      }
      E.myTest(javax.swing.SwingUtilities.isEventDispatchThread(), "runYears2 is eventDispatchThread not a separate animation thread");
      paintCurDisplay(ec);
      if(E.debugStatsOut1)System.out.println(EM.wasHere3 = "------MB------- runYears2;" + sinceA() + sinceRY2() + stateStringNames[stateConst] + "Y" + EM.year);
      
      RunYrs3 rYrs3 = new RunYrs3(); // the thread for background running runYear()->doYear
      rYrs3.setPriority(2);
      rYrs3.start();  // start the background job
      if(E.debugStatsOut1){
        System.out.println(EM.wasHere3 = "-------MC--------runYears2;" + sinceA()+ sinceRY2() + " at start" + " cnt" + stateCnt + stateStringNames[stateConst] + "Y" + eM.year);
      }
      //  stateConst = STARTING;
      paintCurDisplay(ec = curEc = eM.curEcon);
      // now continue EDS thread with updating display
      Boolean done = false, did = false;
      // start the annimation loop until done, waiting to call paintCurDisplay again
      aTime = (new Date()).getTime();
      for (stateCnt = 0; !EM.dfe() && !eM.stopExe && !done; stateCnt++) {
        ec = curEc = EM.curEcon;
        EM.wasHere3 = "---------AA--------runYears2 before seEconState " + sinceA() + sinceAA() + sinceRY2() + sinceRY3() + " " + stateStringNames[stateConst] + " " + EM.curEconName + "Y" + EM.year + " cnt" + stateCnt;
        if (E.debugStatsOut1)System.out.println(EM.wasHere3);
        checkEconState(); // check for stuck
        if (E.debugStatsOut2)System.out.println("------NC------^^runYears2 " + sinceA() + sinceAA() + " " + stateStringNames[stateConst] + "Y" + EM.year + " cnt" + stateCnt + "::" + sameEconState);
        if (E.debugStatsOut1)System.out.println("----------RYa-------runYears2 " + sinceA()+ sinceAA() + sinceRY2() + sinceRY3() + stateConst + " cnts" + stateCnt + "::" + sameEconState + " " + stateStringNames[stateConst] + "Y" + EM.year + (did ? " DID" : " !!DID") + (done ? " DONE" : " !!DONE"));
        paintCurDisplay(ec);
        // now do waits until the next check of stateConst and paintCurDisplay
        if (E.debugStatsOut2)System.out.println("----------RYb-------runYears2 " + sinceA()+ sinceAA() + sinceRY2() + sinceRY3() + stateConst + " cnts" + stateCnt + "::" + sameEconState + " " + stateStringNames[stateConst] + "Y" + EM.year + (did ? " DID" : " !!DID") + (done ? " DONE" : " !!DONE"));
        switch (stateConst) {
          case CONSTRUCTING:
          case CONSTRUCTED:
          case WAITING:
          case STARTING:
            paintCurDisplay(eM.curEcon);
             {
              Thread.sleep(blip);
            }
            break;
          case CREATING:
          //  paintCurDisplay(eM.curEcon);
          // Thread.sleep(blip);
          // break;
          case FUTUREFUNDCREATE:
            // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip);
            break;
          case STARTYR:
            // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip);
            did = true;
            break;
          case SEARCH:
            // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip);
            did = false;
            break;
          case SWAPS:
            //  paintCurDisplay(eM.curEcon);
            Thread.sleep(blip5);
            did = false;
            break;
          case TRADING:
            // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip5);
            did = false;
            break;
          case ENDYR:
            //  paintCurDisplay(eM.curEcon);
            Thread.sleep(blip);
            did = true;
            break;
          case STATS:
            done = true;  //stop looping
            // paintCurDisplay(ec = curEc = eM.curEcon); // force final display as stats not display
            if (E.debugStatsOut1) {
              System.out.println("----------MD-------runYears2;" + since()+ sinceAA() + " " + stateStringNames[stateConst] + "Y" + eM.year + (did ? " DID" : " !!DID") + (done ? " DONE" : " !!DONE"));
            }
            // listRes(fullRes); done in runBackgroundYears4
            break;

          case RUNSDONE:
            // no wait
            if (E.debugStatsOut1) {
              System.out.println("----------ME-----runYears2;" + since()+ sinceAA() + " " + stateStringNames[stateConst] + "Y" + EM.year + (did ? " DID" : " !!DID") + (done ? " DONE" : " !!DONE"));
            }
            done = true; //end the loop stateCnt 
            setEconState(STATS);
            // paintCurDisplay(ec); // force final display as stats not A display
            break;
          default:
            if (E.debugStatsOut1) {
              System.out.println("--------MF--------runYears2;" + since()+ sinceAA() + " stateCnt =" + stateCnt + " " + stateStringNames[stateConst] + "Y" + EM.year + (did ? " DID" : " !!DID") + (done ? " DONE" : " !!DONE"));
            }
            if (did) {
              done = true;
              setEconState(STATS);
            } else {
              Thread.sleep(blip);
            }
            if (E.debugStatsOut1) {
              System.out.println("---------MG----------runYears2;" + since() + sinceRY2() + " DEFAULT;" + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + stateConst + "Y" + eM.year);
            }
        } // switch stateConst
      }// stateCnt end
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println("------MH-----RunYears2 " + Econ.nowName + since() + " " + Econ.nowThread + " " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();
    }
  }

  protected void runBackgroundYears4(int nYears) {

    eM.setMoreValues();
    stateConst = STARTING;
    long myStart = new Date().getTime();
    if(E.debugStatsOut1)System.err.println("------MII------starting in runBackGroundYears4 thread=" + Thread.currentThread().getName() + "startTime" + (new Date().getTime() - startTime));
    if(E.debugStatsOut1)System.err.println(EM.prevLine = "---------MI----- in runBackGroundYears4 nYears=" + nYears + " thread=" + Thread.currentThread().getName() + "msecs" + (new Date().getTime() - startTime) + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + stateConst + "Y" + eM.year + "<<<<<<<<");
    // E.myTest(javax.swing.SwingUtilities.isEventDispatchThread(), "is eventDispatchThread");
    EM.clearWH();

    for (int nn = 0; nn < nYears && !EM.dfe() && !EM.stopExe && !doStop && !fatalError; nn++) {
      EM.errLine = "-------MJ---------in runBackroundYears4" + since() + "run year="
              + (EM.year) + " background years=" + nn + " btime="
              + (new Date().getTime() - myStart) + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + stateConst + "Y" + EM.year;
      if(E.debugStatsOut1) {
        System.err.println(EM.errLine);
      }
      aaTime = (new Date()).getTime();
      runYear();
    } // nn end loop
    setEconState(STATS);
    int cpIx2 = 0, cpIx3 = 0, cpIx4 = 0;
    EM.errLine = "-------MJz---------runBackroundYears4 after STATS " + since() + sinceA() + sinceAA() + " background years=" + nn + " btime="
            + (new Date().getTime() - myStart) + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + "Y" + eM.year;
    if (E.debugStatsOut1) {
      System.err.println(EM.errLine + "<<<<<<<<<<<<<<<<<<<");
    }
    if (false) {
      display.setVisible(false);

      controlPanels.setSelectedIndex(4);
      cpIx2 = controlPanels.getSelectedIndex();

      listRes(0, resLoops, fullRes);
      controlPanels.setSelectedIndex(4);
      cpIx3 = controlPanels.getSelectedIndex();
      stats.setVisible(true);
      stats.revalidate();
      stats.repaint();
      display.setVisible(true);
      cpIx4 = controlPanels.getSelectedIndex();
    }
    //   listRes(fullRes);
    printMem3(); // goes to doYears

    // background thread can now end
  } // end runBackgroundYears

  int initialPlanetShip = 0;
  int nextPlanetShip = 1;

  /**
   * return a new Econ enter with preset counts
   *
   * @param worth The worth of the new Econ to be created
   * @param pors The p or s of the new Econ
   * @param clan The clan of the new Econ
   * @return a new Econ reference
   */
  Econ newEcon(double worth, int pors, int clan) {
    // EM.porsCnt[0] = EM.planets.size();
    // EM.porsCnt[1] = EM.ships.size();
    double xpos = -9999.;
    ec = curEc = eM.curEcon = null;
    // now try to find a dead economy to use instead of recreating one
    if (pors == E.P) {
      for (Econ n : eM.econs) {
        EM.wasHere = "n=" + n + " " + n.getDie() == null ? " null getDie()" : " found getDie()";
        if (n.getDie() && n.getPors() == E.P && n.getDAge() > 2) {
          eM.setCurEcon(ec = curEc = eM.curEcon = n);   // take a dead one
          //  EM.econCnt++;
          EM.wasHere = "-------MK--------found dead Planet cnt=" + n + " " + n.name + sinceA() + " dage=" + n.getDAge() + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + stateConst + "Y" + eM.year;
          System.out.println(EM.wasHere);
          break;
        }
      }// for
    }// E.P
    else if ((eM.curEcon == null) && pors == E.S) {
      for (Econ n : eM.econs) {
        if (n.getDie() && n.pors == E.S && n.getDAge() > 2) {
          eM.setCurEcon(ec = curEc = eM.curEcon = n);
          //    EM.econCnt++;
          EM.wasHere = "-------ML--------found dead Ship cnt=" + n + " " + n.name + sinceA() + " dage=" + n.getDAge() + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + stateConst + "Y" + eM.year;
          System.out.println(EM.wasHere);
          break;
        }
      }// for
    }//E.S
    if (eM.curEcon == null) {  // no dead one found create one
      EM.wasHere = "-------MMa--------Init new Econ" + EM.econCnt + sinceA() + " stateCnt" + stateCnt + " " + stateStringNames[stateConst] + stateConst + "Y" + eM.year;
      System.out.println(EM.wasHere);
      EM.setCurEcon(ec = curEc = EM.curEcon = new Econ());
      EM.econs.add(EM.curEcon); // add to the main list
      // EM.econCnt++;
    }
    NumberFormat nameF = NumberFormat.getNumberInstance();
    nameF.setMinimumIntegerDigits(4);
    nameF.setGroupingUsed(false);
    String name = (pors == 0 ? "P" : "S") + nameF.format(eM.nameCnt++);
    // reduce the size of ships cash by shipsPerPlanet
    // double mCash = eM.initialWorth[pors] * (pors == E.S ? 1.0 / shipsPerPlanet : 1.0);
    EM.wasHere = "-------MMb--------Init new Econ" + EM.econCnt + sinceA() + " stateCnt" + stateCnt + " " + stateStringNames[stateConst] + stateConst + "Y" + eM.year + " name=" + name;
    System.out.println(EM.wasHere);
    eM.curEcon.init(this, eM, name, clan, EM.econCnt, pors, xpos, eM.difficultyPercent[0], worth);
    startEconState = (new Date()).getTime();
    EM.wasHere = "-------MN--------Inited  Econ" + EM.econCnt + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + stateConst + "Y" + eM.year + " name=" + name + sinceRunYear();
    System.out.println(EM.wasHere);
    // now update counts planets and ships
    Econ t = eM.curEcon;
    if (!t.getDie()) {
      if (E.debugChangeEconCnt) {
        synchronized (EM.econCnt) { // protect the increment of econCnt
          EM.porsClanCnt[t.pors][t.clan]++;
          EM.clanCnt[t.clan]++;
          EM.porsCnt[t.pors]++;
          EM.econCnt = EM.porsCnt[0] + EM.porsCnt[1];
        }
      }// end synchronized
      else {
        eM.porsClanCnt[t.pors][t.clan]++;
        eM.clanCnt[t.clan]++;
        eM.porsCnt[t.pors]++;
      }
      if (t.pors == P) {
        eM.planets.add(t);
      } else {
        eM.ships.add(t);
      }
      EM.wasHere = "-------MMc--------" + sinceA() + " counted Econ" + EM.econCnt + "::" + EM.econs.size() + " planets" + EM.porsCnt[0] + "::" + EM.planets.size() + " ships" + EM.porsCnt[1] + "::" + EM.ships.size() + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + stateConst + "Y" + eM.year + " name=" + name + sinceRunYear();
      System.out.println(EM.wasHere);

    }
    return EM.curEcon;
  }

  /**
   * get a reference to Class E
   *
   * @return reference to instance E
   */
  public E getE() {
    return eE;
  }

  /**
   * return the location of EM set by StarTrader()
   *
   * @return reference to instance EM
   */
  public EM getEM() {
    return eM;
  }

  /**
   * return the seconds since start of StarTrader
   *
   * @return seconds nnn.mmm
   */
  public String since() {
    return since("since game start", startTime);
  }

  /**
   * get the seconds for the currentEconState
   *
   * @return Since P????? secs=nnn.mmm
   */
  public String sinceEcon() {
    return since("since " + EM.curEconName, startEconState) + ", ";
  }

  /**
   * return the clan and names of trading Econs along with time
   *
   * @return since trade clan and names of trading Econs followed by time since
   * startEconState
   */
  public String tradingEcon() {
    return since("since trade " + EM.curEconClan + " " + EM.curEconName + " :: " + EM.otherEconClan + " " + EM.otherEconName, startEconState) + ", ";
  }

  /**
   * return the seconds since the start of RunYear
   *
   * @return seconds nnn.mmm
   */
  public String sinceRunYear() {
    return since("year" + eM.year + " ", startYear);
  }
  
  /** Seconds since aaTime start of runYears2
   * 
   * @return fraction seconds since aaTime in runYears2
   */
  public String sinceAA(){return since(" aaTime", aaTime);}

  /** Seconds since aTime start of runYears2
   * 
   * @return fraction seconds since aTime in runYears2
   */
  public String sinceA(){return since(" aTime", aTime);}
  
  /** Seconds since  start of runYears2
   * 
   * @return fraction seconds since start runYears2
   */
  public String sinceRY2(){return since(" ry2Time", startRY2);}
  
   /** Seconds since start of runYears2
   * 
   * @return fraction seconds since start runYears2
   */
  
  public String sinceRY3(){return since(" ry3Time", startRY3);}

  /**
   * format the seconds since a given recorded time
   *
   * @param prefix a String to previx the number
   * @param startTime the original start time to be reported
   * @return a string of the now - startTime with seconds as a fraction
   */
  public String since(String prefix, long startTime) {
    long now = (new Date()).getTime();
    double nu = (now - startTime) * .001;
    // String sAge = (eM.curEcon == null ? " " : " " + eM.curEcon.name); // + " age=" + eM.curEcon.age);
    return prefix + " " + EM.mf(nu);
  }

  protected double mapHealth(double h) {
    double[] hh = {-10., -.5, -.3, -.3, -.2, -.1, 0., .3, .5, .7, 2., 10., 100., 1000., 10000};
    double[] h2 = {1.5, 1.45, 1.4, 1.35, 1.3, 1.25, 1.2, 1.15, 1.1, 1.05, 1, .95, .90, .85, .8};
    for (int i = 0; i < hh.length; i++) {
      if (h < hh[i]) {
        return h2[i];
      }
    }
    return .75;
  }

  /**
   * return the current value of the AssetsYr loop n
   *
   * @return
   */
  int getN() {
    return eM.curEcon.getN();
  }

  /**
   * return the pointer to the hist of the current Economy (Planet or Ship)
   *
   * @return
   */
  ArrayList<History> getHist() {
    return eM.curEcon.getHist();
  }

  /**
   * display a hist file reference for a given economy using displayLog
   *
   * @param table table to be displayed with displayLog
   * @param his unused
   * @param rowToM unused
   * @param startM unused
   * @param maxs unused
   * @param levs unused
   * @return number of last row displayed
   */
  protected int displayHistory(javax.swing.JTable table, ArrayList<History> his, int[] rowToM, int startM, int[] maxs, int[] levs) {
    return displayLog(table);
  }

  ;
   public int displayLog() {
    return displayLog(logDisplayTable);
  }

  int k;

  protected int displayLog(javax.swing.JTable table) {
    //  pLogDisplayTable = table;
    //  logHistoryHist = his;
    NumberFormat dispFraction = NumberFormat.getNumberInstance();
    dispFraction.setMinimumFractionDigits(2);
    NumberFormat whole = NumberFormat.getNumberInstance();
    whole.setMaximumFractionDigits(0);
    table.getColumnModel().getColumn(0).setMinWidth(180);
    table.getColumnModel().getColumn(0).setPreferredWidth(180);
    int tableRowCount = table.getRowCount();
    if (eM.hists[0] == null || eM.hists[1] == null) {
      return 0;
    }
    E.logSizeHis[0] = eM.hists[0].size();
    E.logSizeHis[1] = eM.hists[1].size();
    E.myTest(eM.hists[0] != eM.logEnvirn[0].getHist(), " Error hists[0] not match hist for " + eM.logEnvirn[0].name);
    E.myTest(eM.hists[1] != eM.logEnvirn[1].getHist(), " Error hists[1] not match hist for " + eM.logEnvirn[1].name);
    //  E.myTest(E.logSizeHis[0] == 0, " error Empty hist " + eM.logEnvirn[0].name);
    //  E.myTest(E.logSizeHis[1] == 0, " error Empty hist " + eM.logEnvirn[1].name);
    E.logLen[0] = Math.min(eM.logEnvirn[0].logLen[0], tableRowCount - 1);
    E.logLen[1] = Math.min(eM.logEnvirn[1].logLen[1], tableRowCount);
    E.logLen[2] = Math.min(E.logLen[0] + 1 + E.logLen[1], tableRowCount);

    // M or m represents the line number in the display table= table
    E.logM[0] = eM.logEnvirn[0].logM[0];
    E.logM[1] = eM.logEnvirn[1].logM[1];
    int lead = 250; // prior numbers we look for titles
    int logLev[] = new int[2];
    logLev[0] = E.logLev[0] = eM.logEnvirn[0].logLev[0];
    logLev[1] = E.logLev[1] = eM.logEnvirn[1].logLev[1];
    int logRowStart[] = {0, E.logLen[0] + 1};
    int logRowEnd[] = {E.logLen[0], E.logLen[2]};
    int prev20 = -5;
    int row = 0, rowsStart = 0, m;
    for (k = 0; k < 2; k++) {
      if (E.logM[k] < 0) {
        E.logM[k] = 0;
      }
      if (E.logSizeHis[k] < 1) {
        System.out.printf("hist %d is empty\n", k);
      } else {
        int ma, mb, mc, r0, r1, rp;
        r1 = rp = r0 = -1;
        boolean showLine = false;
        int maxLev = logLev[k];
        // int rowsStart = logRowStart[k];
        rowsStart = row;
        int rowsEnd = logRowEnd[k];
        int mEnd = E.logSizeHis[k] = eM.hists[k].size();;
        //set start of look for a 20 title
        ma = (mc = ((mb = E.logM[k]) - lead)) < 0 ? 0 : mc;
        // don't go over the end of the hist
        ma = mEnd > ma ? ma : mEnd - 1;
        //     row = logDisplayStart[k];
        System.out.println(since() + " display history k=" + k + " size=" + mEnd + " max=" + maxLev + " lev=" + eM.hists[k].get(ma).level + " m=" + ma + " row=" + row);
        System.out.flush();
        int drlev = 4;
        int md = 50000;
        // int row = rowsStart;
        // row = rowsStart;
        for (m = ma; row < rowsEnd && m < mEnd; m++) {
          md = m;
          History dr = eM.hists[k].get(m);
          if (dr == null) {

            System.out.println("in displayLog null line at k=" + k + " m=" + m);
            m = mEnd;
            // E.myTest(dr == null, "null dr at k=" + k + " m=" + m);
          } else {
            drlev = dr.level;
            if (drlev == 20 && maxLev > 5) {
              showLine = true;
              if (m < mb) {
                row = rowsStart;
              }
            } else if (drlev == 1 && rp != 1 && m < mb) {
              showLine = true;
              row = rowsStart;
            } else if ((drlev == 1 || drlev == 2) && rp == 1 && row == rowsStart + 1 && m < mb) {
              showLine = true;
            } else if (drlev <= maxLev && m >= mb) {
              showLine = true;
            } else {
              showLine = false;
            }
            rp = drlev;
            if (showLine) {
              String tit = row + dr.pre + m + ":" + dr.level + "=" + dr.title;
              table.setValueAt(tit, row, 0);
              int i2 = 1;
              for (int ii = 0; ii < 10; ii++, i2++) {
                table.setValueAt(dr.Ss[ii], row, i2);
              }
              logRowToM[row] = m;
              row++;
              logLastM[k] = m;
            }
          }
        }
        if (k == 0 && row == rowsEnd) {  // need separator line?
          table.setValueAt("---------------------", row, 0);
          int i2 = 1;
          for (int ii = 0; ii < 10; ii++, i2++) {
            table.setValueAt("--------", row, i2);
          }
          row++;
        }
        if (m == mEnd && row < rowsEnd) {
          table.setValueAt(">>>>>>>>END <<<<<<<<", row, 0);
          int i2 = 1;
          for (int ii = 0; ii < 10; ii++, i2++) {
            table.setValueAt(">>>>>end<<<<<", row, i2);
          }
          row++;
        }

        System.out.println("displayLog k=" + k + ", row=" + row + ", last=" + logLastM[k] + ", m=" + m + ", md=" + md + ", level=" + logLev[k] + ", logRowEnd=" + logRowEnd[k]);
        System.out.flush();
        //     logMHist.add(logLastM[k]);
      }
    }
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = table.getSelectionModel();
    k = 0;
    System.out.println("displayLog lsm k=" + k + ", row=" + row + ", last=" + logLastM[k] + ", level=" + logLev[k] + ", logRowEnd=" + logRowEnd[k]);
    System.out.flush();
    rowSM.addListSelectionListener((ListSelectionEvent e) -> {
      //Ignore extra messages.
      NumberFormat whole1 = NumberFormat.getNumberInstance();
      whole1.setMaximumFractionDigits(0);
      k = 0;
      if (e.getValueIsAdjusting()) {
        return;
      }
      ListSelectionModel lsm = (ListSelectionModel) e.getSource();
      if (lsm.isSelectionEmpty()) {
        //      System.out.println(since() + " No rows are selected.");
      } else {
        int selectedRow = lsm.getMinSelectionIndex();
        int nrows = logRowCount;
        ;
        logSelectedRow = selectedRow;
        int am = 0, am0 = 0, am1 = 0, am2 = 0, am3 = 0;
        // process the first Econ display
        if (logSelectedRow < E.logLen[0]) {
          k = 0;
          am = logRowToM[selectedRow];
          // do we go backword
          if (selectedRow < E.logLen[0] / 4) {
            // 12/30/15 try for a better back up
            // find  neg the number of lines from start+1 to end-1
            // get number of log lines per display line
            am0 = (logRowToM[1] - logRowToM[E.logLen[0] - 1]) / (E.logLen[0] - 2);
            // find number of display lines to go back
            int am0a = E.logLen[0] / 4 - selectedRow;
            // compute a log row am0a * am0 less than display row1
            am1 = logRowToM[1] - am0a * am0 - 40;
            am2 = am1 < 0 ? 2 : am1; // keep it positive
            am3 = am2 > eM.hists[0].size() ? eM.hists[0].size() - 20 : am2;
            // am = am - E.logLen[0] * 3 / 4;
          } else {
            am3 = am2 = am1 = am;
          }
          setLogM(0, am3);
        } else { // second section
          k = 1;
          int logStart = E.logLen[0] + 1; // go past the -- -- --
          am = logRowToM[selectedRow];
          if (selectedRow - logStart < E.logLen[1] / 4) {
            am1 = logRowToM[logStart + 1] - (logRowToM[logStart + E.logLen[1] - 1] - am);
            am2 = am1 < 0 ? 10 : am1; // keep positive
            am3 = am2 > eM.hists[1].size() ? eM.hists[1].size() - 20 : am2;
            // am = am - E.logLen[0] - E.logLen[1] * 3 / 4;
          } else {
            am3 = am2 = am1 = am;
          }
          setLogM(1, am3);
        }
        System.out.println(since() + " selectedRow=" + selectedRow + " k:am=" + k + ":" + am + "=>" + am1 + "=>" + am2 + "am3 " + am3);
        System.out.println(since() + " from" + logLastM[k] + " selectRow=" + whole1.format(k) + ":" + whole1.format(am));
        displayLog(logDisplayTable);
      }
    });

    return logSelectedRow;
  }// end displayLog

  static int envsLoop = 0;
  static int planetsLoop = 0;
  static int shipsLoop = 0;
  static int envsLoop2 = 0;
  // static int[] envsPerYear = {10, 20, 30, 40, 40, 40, 40, 40, 40, 40, 40, 40};
  //static int[] envsPerYear = {10, 20, 30, 40, 50, 60, 10};
  JSlider[] gameSlidersP = {gameSliderP0, gameSliderP1, gameSliderP2, gameSliderP3, gameSliderP4, gameSliderP5, gameSliderP6, gameSliderP7, gameSliderP8, gameSliderP9};
  JSlider[] gameSlidersS = {gameSliderS0, gameSliderS1, gameSliderS2, gameSliderS3, gameSliderS4, gameSliderS5, gameSliderS6, gameSliderS7, gameSliderS8, gameSliderS9};
  JSlider[] clanSlidersP = {clanSliderP0, clanSliderP1, clanSliderP2, clanSliderP3, clanSliderP4};
  JSlider[] clanSlidersS = {clanSliderS0, clanSliderS1, clanSliderS2, clanSliderS3, clanSliderS4};
  JTextField[] gameTextFields = {gameTextField0, gameTextField1, gameTextField2, gameTextField3, gameTextField4, gameTextField5, gameTextField6, gameTextField7, gameTextField8, gameTextField9};
  JTextField[] clanTextFields = {clanTextField0, clanTextField1, clanTextField2, clanTextField3, clanTextField4};
  JPanel gamePanels[] = {gamePanel0, gamePanel1, gamePanel2, gamePanel3, gamePanel4, gamePanel5, gamePanel6, gamePanel7, gamePanel8, gamePanel9};
  JPanel clanPanels[] = {clanPanel0, clanPanel1, clanPanel2, clanPanel3, clanPanel4};
  JLabel clanLabelsP[] = {gameLabelP5, gameLabelP6, gameLabelP7, clanLabelP3, clanLabelP4};
  JLabel clanLabelsS[] = {gameLabelS5, gameLabelS6, gameLabelS7, gameLabelS8, clanLabelS4};
  double fullRes[] = {1., 2.};
  int lGameRes = fullRes.length;

  Runtime runtime = Runtime.getRuntime();
  static final long MEGABYTE = 1024L * 1024L;

  public static double bytesToMegabytes(Long bytes) {
    return bytes / MEGABYTE;
  }

  Dimension screenSize;
  int screenHeight = -2, screenWidth = -2, myHeight = -2, myWidth = -2, myH2 = -2, myW2 = -2;
  int panelW = -2, panelH = -2, tableW = -2, tableH = -2, table2W = -2, table2H = -2;
  int myW3 = -2;
//  String[][] statsData;

  public StarTrader() {
    try {
      starTrader2();
      Thread.sleep(2000);
      //   runYears(1);
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowName + " " + Econ.nowThread + " Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();
    }
  }

  /**
   * Creates new Class/Form StarTrader
   */
  public void starTrader2() {
    try {
      stateConst = CONSTRUCTING;
      this.eE = new E();
      this.eM = new EM(eE, this);
      EM.startTime = startTime;
      for (int y = 0; y < yearsL; y++) {
        theYear[y] = -2;
        yearSecs[y] = 0;
        yearEcons[y] = 0;
        yearSecPerEcon[y] = 0;
        yearPlanets[y] = 0;
        yearShips[y] = 0;
        //gamePlanets[y] = 0; 
        // gameShips[y] = 0; 
        yearTW[y] = 0.;
        //gameTW[y] = 0.;
      }

      //  fullRes = EM.gameRes.values();
      //  lGameRes = fullRes.length;
      // ABalRows dummy;
      //  dummy = new ABalRows();
      /**
       *
       */
      initComponents();
      Object statsData[][];
      screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      screenHeight = screenSize.height; //1080
      screenWidth = screenSize.width;  // 1920
      myHeight = Math.max(500, Math.min((int) (screenHeight * .93), 1250));
      EM.panelH[0][0] = Math.max(500., Math.min((int) (screenHeight * .93), 1250));
      EM.tableH[0][0] = Math.max(500., EM.panelH[0][0] - 200.);
      myWidth = Math.max(700, Math.min((int) (screenWidth * .95), 1850));
      EM.panelW[0][0] = Math.max(700, Math.min((int) (screenWidth * .95), 1850));
      myW3 = Math.max(650, (myW2 = Math.max(600, (int) (myWidth * .99))) - 300);
      E.sysmsg("resize1 height=" + screenHeight + "->" + myHeight + ", " + myH2 + ", width=" + screenWidth + "=>" + myWidth + ", " + myW2 + ", " + myW3);
      if (screenHeight < 600 || screenWidth < 600) {
        // if(screenHeight < 2700 || screenWidth < 1200){
        myHeight = Math.max(500, Math.min((int) (screenHeight * .97), 1250));
        myH2 = Math.max(500, myHeight - 200);
        myWidth = Math.max(500, Math.min((int) (screenWidth * .97), 2200));
        myW3 = Math.max(450, (myW2 = Math.max(400, (int) (myWidth * .99))) - 300);
        E.sysmsg("resize2 height=" + screenHeight + "->" + myHeight + ", " + myH2 + ", width=" + screenWidth + "=>" + myWidth + ", " + myW2 + ", " + myW3);
        this.setSize(myWidth, myHeight);
        controlPanels.setSize(myWidth, myHeight);
        stats.setMaximumSize(new Dimension(myW2, (int) (myHeight * 1.50)));
        stats.setMinimumSize(new Dimension((int) (myWidth * .9), (int) (myHeight * .9)));
        stats.setPreferredSize(new Dimension(myWidth, myHeight));
        statsScrollPane2.setPreferredSize(new Dimension(myW2, myH2));
        statsTable1.setMaximumSize(new Dimension(myW3 + 100, myH2 + 100));
        statsTable1.setMinimumSize(new Dimension(myW3 - 100, myH2 - 100));
        statsTable1.setPreferredSize(new Dimension(myW3, myH2));
      }
      eE.init(eM, this);
      eM.init();
      this.pack();
      Rectangle statsR = stats.getBounds();
      int ss1 = statsR.width;
      Rectangle spR = statsScrollPane2.getBounds();
      Rectangle stR = statsTable1.getBounds();
      System.out.println("===============StarTraderresize2 height=" + screenHeight + "->" + myHeight + ", " + myH2 + ", width=" + screenWidth + "=>" + myWidth + ", " + myW2 + ", " + myW3);
      System.out.println("=================StarTrader  sized stats w=" + statsR.width + ", h=" + statsR.height
              + ", statsP w=" + spR.width + ", h=" + spR.height
              + ", statsTable w=" + stR.width + ", h=" + stR.height);
      int statsTW = statsTable1.getWidth();
      int storyTextField1W = storyTextField1.getWidth();
      int storyTextPaneW = storyTextPane.getWidth();
      int storyW = story.getWidth();
      //  int logW = log.getWidth();
      System.out.println("----------- stf1w=" + storyTextField1W + ", stp=" + storyTextPaneW + ", story=" + storyW);
      E.sysmsg("after pack statsTable1.width=" + statsTW);
      //   statsTable = new javax.swing.JTable();
      JSlider[] gameSlidersP1 = {gameSliderP0, gameSliderP1, gameSliderP2, gameSliderP3, gameSliderP4, gameSliderP5, gameSliderP6, gameSliderP7, gameSliderP8, gameSliderP9};
      JSlider[] gameSlidersS1 = {gameSliderS0, gameSliderS1, gameSliderS2, gameSliderS3, gameSliderS4, gameSliderS5, gameSliderS6, gameSliderS7, gameSliderS8, gameSliderS9};
//  JSlider[] clanSlidersP = {clanSliderP0, clanSliderP1, clanSliderP2, clanSliderP3, clanSliderP4};
      // JSlider[] clanSlidersS = {clanSliderS0, clanSliderS1, clanSliderS2, clanSliderS3, clanSliderS4};
      JTextField[] gameTextFields1 = {gameTextField0, gameTextField1, gameTextField2, gameTextField3, gameTextField4, gameTextField5, gameTextField6, gameTextField7, gameTextField8, gameTextField9};
      // JTextField[] clanTextFields = {clanTextField0, clanTextField1, clanTextField2, clanTextField3, clanTextField4};
      JPanel gamePanels1[] = {gamePanel0, gamePanel1, gamePanel2, gamePanel3, gamePanel4, gamePanel5, gamePanel6, gamePanel7, gamePanel8, gamePanel9};
//  JPanel clanPanels[] = {clanPanel0, clanPanel1, clanPanel2, clanPanel3, clanPanel4};

//  JLabel clanLabelsP[] = {gameLabelP5, gameLabelP6, gameLabelP7, clanLabelP3, clanLabelP4};
      // JLabel clanLabelsS[] = {gameLabelS5, gameLabelS6, gameLabelS7, gameLabelS8, clanLabelS4};
      //   E.sysmsg("gameSlidersP1[0] =" + (gameSlidersP1[0] == null ? "null" : gameSlidersP1[0].isEnabled() ? "enabled" : "disabled"));
      gameSlidersP = gameSlidersP1;
      gameSlidersS = gameSlidersS1;
      gameTextFields = gameTextFields1;
      gamePanels = gamePanels1;
      double tCons = 300.26;
      double tWork = 200.33;
      double tFert = 250.45;
      NumberFormat dispFraction = NumberFormat.getNumberInstance();

      dispFraction.setMinimumFractionDigits(2);
      //   displayConsumers.setText(dispFraction.format(tCons));
      //   displayWorkers.setValue(dispFraction.format(tWork));
      //   displayFertile.setValue(dispFraction.format(tFert));
      logDisplayTable.setShowHorizontalLines(true);
      logDisplayTable.setShowVerticalLines(true);
      logDisplayTable.getColumnModel().getColumn(0).setMinWidth(150);
      logDisplayTable.getColumnModel().getColumn(0).setPreferredWidth(150);
      statsTable1.setShowHorizontalLines(true);
      statsTable1.setShowVerticalLines(true);

      // now reset the slide values
      /*    historyDisplay1Length.setMajorTickSpacing(10);
     historyDisplay1Length.setMinorTickSpacing(5);
     historyDisplay1Length.setPaintTicks(true);
     historyDisplay1Length.setPaintLabels(true);
     historyDisplay1Length.setValue(20);
     historyDisplay1Length.setMaximumSize(new java.awt.Dimension(100, 24));
     historyDisplay1Length.setName("Length");
     *
       */
      logRowToM = new int[100];

      //   TreeMap<Double, Econ> runOrder = new TreeMap<Double, Econ>();
      putInitValues();
      System.out.println("===================StarTrader resize4 height=" + screenHeight + "->" + myHeight + ", " + myH2 + ", width=" + screenWidth + "=>" + myWidth + ", " + myW2 + ", " + myW3);
      statsR = stats.getBounds();
      ss1 = statsR.width;
      spR = statsScrollPane2.getBounds();
      stR = statsTable1.getBounds();
      Rectangle scrnR = this.getBounds();
      int scrnH = scrnR.height;
      int scrnW = scrnR.width;
      System.out.println(Thread.currentThread().getName() + " =================StarTrader sized6 screen w=" + scrnW + ", h=" + scrnH
              + ", stats w=" + statsR.width + ", h=" + statsR.height
              + ", statsP w=" + spR.width + ", h=" + spR.height
              + ", statsTable w=" + stR.width + ", h=" + stR.height);
      storyTextField1.setText(storyText);
      storyTextField1.setCaretPosition(0);
      //storyVersionField.setText(versionText);

      //  EM.clanVals.TEST.show();
      //  EM.clanVals.TEST2.show();
      //   EM.clanVals.TEST1.show();
      // resetRes(fullRes);
      //   setFatalError();
      // set the following gamePanelChange if wanted before first year
      // gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      //  runYear();  // do if a year execution before game request
      System.out.println(Thread.currentThread().getName() + " ======================StarTrader before gamePanelChange ========================");
      gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      //eM.curEcon.runYear(.0);
      //eM.curEcon.runYear(.0);
      //eM.curEcon.runYear(.0);
      //eM.curEcon.runYear(.0);
      //eM.curEcon.runYear(.0);
      String gchgdone = "================gamePaneChange done ==============";

      System.out.println(gchgdone);
      System.err.println(gchgdone);
      printMem3();
      stateConst = CONSTRUCTED;
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      eM.flushes();
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + " Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      setFatalError();

    }
  } //StarTrader2

  long gigMem = 1000000000L;
  String prGigMem = "";
  /** print memory facts at this time 
   * 
   */
  public static void printMem3() { EM.printMem3();}
  void oldprintMem3(){
    // runtime.gc(); // garbage collect
    totMem = runtime.totalMemory();
    freeMem = runtime.freeMemory();
    usedMem = totMem - freeMem;
    double tmem = (double) totMem / gigMem, fmem = (double) freeMem / gigMem, umem = (double) usedMem / gigMem;
    //System.out.println("");
    prGigMem = " Game Memory " + stateStringNames[stateConst] + " year=" + eM.year + "Gigs total=" + EM.mf(tmem) + " used=" + EM.mf(umem) + " free=" + EM.mf(fmem);
    System.out.printf("----PM----" + since() + prGigMem + "<<<<<<" + "\n");
  }

  void printMem() {
   // System.out.println();
  }

  int[] clanShipsDone = {0, 0, 0, 0, 0};

  /**
   * find tradable planets for this ship
   *
   * @param shipsDone the number of ships which have visited this year.
   * @param curShip The Econ of the current ship
   * @param psize number of tradable Planets to select
   * @param tradablePlanets An array of tradable planets, only psize if filled
   * @return
   */
  int getWildCurs(int shipsDone, Econ curShip, int psize, Econ[] tradablePlanets) {
    int lPlanets = eM.planets.size(); // known planets
    int lShips = eM.ships.size();  // known ships
    //int[] tradablePlanets = new int[psize];
    int lTradablePlanets = psize;  // number planets to be selected
    double lsel, maxsel;
    int rtns = -1; // counter for planets in tradablePlanets
    // you only get to scan as many planets as you have ships
    //clanShipsDone[eM.curEcon.clan]++;
    Econ planet = eM.planets.get(0);

    int curClan = curShip.getClan();

    double shipsVisitedPerPlanetVisited = EM.porsVisited[E.P] == 0 ? 0. : EM.porsVisited[E.S] / EM.porsVisited[E.P];
    double shipsVisitedPerEconsVisited = EM.visitedCnt == 0 ? 0 : EM.porsVisited[E.S] / EM.visitedCnt;

    // if ...Frac > shipsVisitedPerEconsPlus1Visited means extra ship for any planet
    double shipsVisitedPerEconsPlus1Visited = EM.visitedCnt == 0 ? 0 : EM.porsVisited[E.S] / (EM.visitedCnt + 1);
    // if ...Frac > shipsPlus1VistedPerEconsVisited  means extra ship for any planet
    double shipsPlus1VistedPerEconsVisited = EM.visitedCnt == 0 ? 0 : (EM.porsVisited[E.S] + 1) / EM.visitedCnt;
    double planetsGameGoalFrac = 1.0 - eM.gameShipFrac[E.P]; // percent of econs planets
    double goalGameShipsPerPlanet = planetsGameGoalFrac == 0.0 ? 0.0 : eM.gameShipFrac[E.P] / planetsGameGoalFrac;

    double clanPlanetFrac[] = {1.0 - eM.clanShipFrac[E.P][0], 1.0 - eM.clanShipFrac[E.P][1], 1.0 - eM.clanShipFrac[E.P][2], 1.0 - eM.clanShipFrac[E.P][3], 1.0 - eM.clanShipFrac[E.P][4]};
    double clanGoalShipsPerPlanet[] = {eM.clanShipFrac[E.P][0] / clanPlanetFrac[0], eM.clanShipFrac[E.P][1] / clanPlanetFrac[1], eM.clanShipFrac[E.P][2] / clanPlanetFrac[2], eM.clanShipFrac[E.P][3] / clanPlanetFrac[3], eM.clanShipFrac[E.P][4] / clanPlanetFrac[4]};
//    double clanCurShipsPerPlanet[] = {eM.porsClanVisited[E.S][0]/eM.porsClanVisited[E.P][0],eM.porsClanVisited[E.S][1]/eM.porsClanVisited[E.P][1],eM.porsClanVisited[E.S][2]/eM.porsClanVisited[E.P][2],eM.porsClanVisited[E.S][3]/eM.porsClanVisited[E.P][3],eM.porsClanVisited[E.S][4]/eM.porsClanVisited[E.P][4]};

    int majorLoops = 0; // multiple scans of planets, each loop with higher limits
    double lse1 = 0., lse2 = 0.;
    boolean lla = true, llb = true;
    // find planets new to old
    for (majorLoops = 0; majorLoops < 4 && rtns < lTradablePlanets - 1; majorLoops++) {
      boolean okClanSovrP[] = {};
      //newest planets to oldest planets
      for (int planetsLoop = lPlanets - 1; planetsLoop >= 0 && rtns < lTradablePlanets - 1; planetsLoop--) {
        planet = eM.planets.get(planetsLoop); // pick a planet
        if (!planet.getDie()) {
          // if shipsDone/EconsDone  <= eM.gameShipFrac[E.P] + eM.addGoal[majorLoops]
          // always allow trade to planets < age 3 with no ship visited
          // check tests
          int jjj = planet.as.econVisited; // == null ? 0:planet.as.shipsVisited;
          jjj = shipsDone;  // test for null values in algorithim
          jjj = EM.porsVisited[E.P];
          jjj = planet.getAge();
          jjj = planet.getAge() < 3 ? 5 : 7;
          jjj = (int) (shipsDone
                  + EM.porsVisited[E.P] + .0001); // force a double value
          // jjj = 0;
          jjj = (int) (shipsDone
                  / (jjj + .0001)); // never divide by zero
          jjj = (int) (shipsDone
                  / (shipsDone
                  + EM.porsVisited[E.P] + .0001));
          jjj = planet.visitedShipNext; // index of last saved ship

          // check that planet is close enough, expand distance for hirgher loops
          if ((lsel = planet.calcLY(planet, curShip))
                  < (lse2 = eM.maxLY[0]
                  + eM.addLY[E.P] * eM.multLYM[majorLoops])) {
            // check new planet always can trade
            boolean q0 = planet.canDoAnotherBarter();
            // new planets up to 3 year get preference -- ignore
            boolean q1 = majorLoops < 2 && planet.getAge() < 3; //age 0,1 first 2 majorLoops
            boolean q3 = majorLoops == 4;  // no restriction at last loop
            boolean t0 = q0 || q3; // accept planet

            //test 1 the limit clan planets per clan ships 
            double r1 = (1. - eM.gameShipFrac[E.P]) / eM.gameShipFrac[E.P];//goal P/s
            double r2 = (eM.porsClanVisited[E.P][planet.clan] + .0001) / (clanShipsDone[planet.clan] + .0001);
            boolean t1 = r2 <= r1; //ok more planet

            // test2 
            double p2 = (shipsDone / 5.) / (1. - eM.clanShipFrac[E.P][planet.clan]); // planets
            boolean t2 = (p2 + .0001 + eM.addGoal[majorLoops]) >= EM.porsClanVisited[E.P][planet.clan];

            double p3 = shipsDone;
            double j1 = (shipsDone + EM.porsVisited[E.P] + .0001);
            double j2 = shipsDone / j1; // cur gameShipFrac
            double j3 = eM.gameShipFrac[E.P] + eM.addGoal[majorLoops];
            boolean t3 = j2 <= j3; // cur <= gameShipFrac+addGoal planet ok

            //combine tests 
            if (t0 || (t1 && t2 && t3 && q0)) {
              boolean goPrev = true;
              // see if planet already in the list
              for (int prev = 0; prev < rtns && goPrev == true; prev++) {
                if (planet == tradablePlanets[prev]) {
                  goPrev = false;
                }
              }
              if (goPrev) {  // planet not yet in list
                if (rtns < lTradablePlanets - 1) {
                  tradablePlanets[++rtns] = planet;

                  //    System.out.println(eM.curEcon.getName() + " build select list=" + planet.getName());
                  System.out.printf("-----ST-----ST build planets list #%d for %s, dist=%5.2f < max=%5.2f planet %s\n", rtns, eM.curEcon.getName(), lsel, eM.maxLY[0] + eM.addLY[0] * majorLoops, planet.getName());
                }
              }
            }
          }
        }
      }
    }// majorLoops
    if (rtns > 0) {
      for (int i = rtns; i < lTradablePlanets; i++) {
        tradablePlanets[i] = tradablePlanets[0]; // fill with first econ
      }
    }
    return rtns;
  }// getWildCurs

  boolean clearHist(Econ myCur) {
    if (myCur.myEconCnt >= eM.keepHistsByYear[eM.year > eM.keepHistsByYear.length - 1 ? eM.keepHistsByYear.length - 1 : eM.year] && myCur.hist.size() > 20) {
      myCur.hist.clear();
      return true;
    }
    return false;
  }

  void runYearsInBackgroundNot(int years) {
    for (int yy = 0; yy < years; yy++) {
      runYear();
    }
    //stateConst = STATS;
  }

  /**
   * run one year of planets than ships ships must first select a next plaet to
   * trade, then run a year than a startShipTrade is done to the ship from
   * runBackgroundYears4 years
   */
  public synchronized void runYear() {
    if (eM.fatalError) {
      setFatalError();
      return;
    }

    //initialize the yearly variabes
    // I may want to allow intrupts to change values on the fly, call doYear
    envsLoop = 0;
    planetsLoop = 0;
    shipsLoop = eM.ships.size() - 1;
    envsLoop2 = 0;

    E.msgs = E.dmsgs;   // reset messages for each year
    E.msgcnt = 0;
    System.out.println("-----Y0-----in runYear before doYear, year=" + (eM.year + 1) + " now doYear");
    // all restarts after user input go to doYear keeping yearly variables
    if (!doStop && !eM.stopExe && !EM.dfe()) {
      doYear();
      //stateConst = STATS;

      for (int y = yearsL - 1; y > 0; y--) {
        theYear[y] = theYear[y - 1];
        yearSecs[y] = yearSecs[y - 1];
        yearEcons[y] = yearEcons[y - 1];
        yearPlanets[y] = yearPlanets[y - 1];
        yearShips[y] = yearShips[y - 1];
        gamePlanets[y] = gamePlanets[y - 1];
        gameShips[y] = gameShips[y - 1];
        gameEcons[y] = gameEcons[y - 1];
        yearTW[y] = yearTW[y - 1];
        gameTW[y] = gameTW[y - 1];
        yearSecPerEcon[y] = yearSecPerEcon[y - 1];
      }
      theYear[0] = eM.year;
      yearSecs[0] = (int) (new Date().getTime() - startYear);

      //gamePlanets[0] = EM.porsCnt[E.P] > gamePlanets[0] ?  EM.porsCnt[E.P]  : gamePlanets[0];
      //gameShips[0] = EM.porsCnt[E.S] >  gameShips[0]  ?  EM.porsCnt[E.S]  :  gameShips[0] ;
      // gameTW[0] = yearTW[0] > gameTW[0] ? yearTW[0] : gameTW[0];
      //gameEcons[0] = yearEcons[0] > gameEcons[0] ? yearEcons[0] : gameEcons[0];
      yearSecPerEcon[0] = (int) (yearEcons[0] < 1 ? 0 : yearSecs[0] / yearEcons[0]);
      System.err.println("-----YA-----runYear " + stateStringNames[stateConst] + " year=" + eM.year + " theYr=" + theYear[0] + " yearSecs " + EM.mf(yearSecs[0]) + " yearEcons=" + EM.mf(yearEcons[0]) + " year S per E=" + EM.mf(yearSecPerEcon[0]));
      System.err.println("-----YB-----runYear theYr=" + theYear[1] + " yearSecs " + EM.mf(yearSecs[1]) + " yearEcons=" + EM.mf(yearEcons[1]) + " year S per E=" + EM.mf(yearSecPerEcon[1]));
      System.err.println("----YC----runYear theYr=" + theYear[2] + " yearSecs " + EM.mf(yearSecs[2]) + " yearEcons=" + EM.mf(yearEcons[2]) + " year S per E=" + EM.mf(yearSecPerEcon[2]));
    }
  }

  Date dnow = new Date();
  int lEcons = 0;
  volatile boolean okEconCnt = false;
  //int clanShipsDone[] = {0,0,0,0,0}; // new every doYear

  /**
   * process another year for each of the ships and planets settings may have
   * been changed before this year do any initial creations and then forward
   * fund creations start planets presetting some variables to 0 start ships
   * tradings starting with the newest ships and planets limit number planet
   * trades to the goal of ships for the clan allow multiple ships to trade with
   * a planet and each other if more ships than planets after all ships done, do
   * endYear of all economies some economies experience catastrophies, losses
   * and gains economies without enough infrastructure die planets with enough
   * infrastructure grow, the more surplus the more growth statisics are
   * gathered through out the year, but most statistics at end of endYear
   * statistics are prepared for display, then end of year for all mouse clicks
   * are enabled and statistics can be read and priorities changed.
   */
  public synchronized void doYear() {
    try {
      NumberFormat df = NumberFormat.getNumberInstance();
      df.setMinimumFractionDigits(2);
      df.setMaximumFractionDigits(5);
      NumberFormat whole = NumberFormat.getNumberInstance();
      whole.setMaximumFractionDigits(0);
      double curWorth = 1.;
      if (E.debugDoYearOut) {
        System.out.println("----DYa----Enter doYear statsTable1.width=" + statsTable1.getWidth());
      }
      // years is a -1 origin,
      EM.doYearTime = startYear = new Date().getTime();
      stateConst = STARTING;
      eM.year++;
      E.resetMsgs();
      Thread.yield();
      int jj1 = eM.econs.size();
      int jj2 = eM.envsPerYear.length;
      System.out.println("in doYear year=" + eM.year + " econs=" + eM.econs.size() + " envs.length=" + eM.envsPerYear.length);
      System.out.println(" new:" + eM.envsPerYear[(int) ((eM.year + 1) > (eM.envsPerYear.length - 1) ? (eM.envsPerYear.length - 1) : (eM.year + 1))]);
      //     resetRes(fullRes);  // move years up cur, leave 0 ready for new statRes
      paintWaiting();
      if (!doStop && !eM.dfe()) {
        eM.doStartYear();  //move stats up for the next year
      } else {
        stateConst = STOPPED;
        return;
      }
      //    eE.newRpt();  // zero the report lines for a new sum from each economy
      // add more planets or ships for each new year to the limit of defined Envirns
      // envsCreate

      // set up counts to be used later
      int lNamesList = namesList.getSize();
      int clanBias = 2;
      // eM.porsCnt[0] = eM.planets.size();
      // eM.porsCnt[1] = eM.ships.size();
      int econClan = -5;
      lEcons = eM.econs.size();

      // preset counts to zero, they will be counted next
      // preset traded to zero
      eM.econCnt = 0;
      EM.deadCnt = 0;
      eM.planets.clear();
      eM.ships.clear();
      EM.tradedCnt = 0;
      EM.visitedCnt = 0;
      for (int m = 0; m < 2; m++) {
        EM.porsCnt[m] = 0;
        EM.porsTraded[m] = 0;
        EM.porsVisited[m] = 0;
        for (int n = 0; n < 5; n++) {
          EM.clanCnt[n] = 0; // doing twice
          EM.clanTraded[n] = 0;
          EM.porsClanCnt[m][n] = 0;
          EM.porsClanTraded[m][n] = 0;
          EM.clanVisited[n] = 0;
          EM.porsClanVisited[m][n] = 0;

        }// n
      }// m

      // now set the counts and planets and ships
      for (Econ t : eM.econs) {
        if (t != null && t.as != null && !t.getDie()) {
          EM.porsClanCnt[t.pors][t.clan]++;
          EM.clanCnt[t.clan]++;
          EM.porsCnt[t.pors]++;
          EM.econCnt++;
          eM.names2ec.put(t.name, t);
          if (t.pors == P) {
            eM.planets.add(t);
          } else {
            eM.ships.add(t);
          }
        } else {
          EM.deadCnt++;
        }
      }
      if (E.debugEconCnt) {
        synchronized (EM.econCnt) {
          if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
            EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
          }
        }
      }
      if (doStop || eM.dfe()) {
        stateConst = STOPPED;
        paintStopped();
      } else {
        // set up the preexisting names on the namelist
        int tyear;
        eM.envsPerYear[eM.envsPerYear.length - 1] = (int) eM.minEcons[0][0];
        // yEcons the number of Econs we can have this year.
        int yEcons = (int) (eM.minEconsMult[0][0] * (eM.envsPerYear[tyear = (eM.year < eM.envsPerYear.length ? eM.year : eM.envsPerYear.length - 1)]));
        //dnow = new Date();
        System.out.println(since() + " tyr=" + tyear + " curE=" + lEcons + " maxE=" + yEcons + " eCnt=" + eM.econCnt);
        printMem();
        lNamesList = namesList.getSize();
        lEcons = eM.econCnt;
        // add this years new economies 
        stateConst = CREATING;
        curStateName = "GameCreate";
        paintEconCreate();
        // randomize the first choice of clan
        E.msgcnt = 0;
        //start game create
        clanBias = new Random().nextInt(5);
        if (E.debugEconCnt) {
          synchronized (EM.econCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
        }
        for (envsLoop = lEcons; envsLoop < yEcons && !eM.dfe(); envsLoop++) {
          startEconState = (new Date()).getTime();
          if (E.debugEconCnt) {
            synchronized (EM.econCnt) {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          // dnow = new Date();
          // econCnt = envsLoop;
          econClan = (envsLoop + clanBias) % 5;
          if (E.debugDoYearOut) {
            System.out.println("------" + since() + " gCreate envsLoop=" + envsLoop + " maxE=" + yEcons + " eCnt=" + eM.econCnt + " clanBias=" + clanBias + " clan=" + econClan);
          }
          int econPorS = EM.getNewPorS(econClan);
          double newWorth = EM.getInitialEconWorth(econPorS, econClan);
          ec = curEc = EM.curEcon = newEcon(newWorth, econPorS, econClan);  // include new of Econ
          if (E.debugEconCnt) {
            synchronized (EM.econCnt) {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          curWorth = eM.curEcon.getWorth();
          curWorth = newWorth;
          Thread.yield();
          EM.curEcon.as.setStat(EM.YEARCREATE, EM.curEcon.pors, EM.curEcon.clan, curWorth, 1);
          EM.curEcon.as.setStat(EM.BOTHCREATE, EM.curEcon.pors, EM.curEcon.clan, curWorth, 1);
          System.out.println("++++++++year" + eM.year + " " + (new Date().getTime() - EM.doYearTime) + " gameCreated " + E.clanLetter[eM.curEcon.clan] + " " + Econ.nowName + E.clanLetter[eM.curEcon.clan] + " worth " + EM.mf(EM.curEcon.getWorth()) + " econssize=" + eM.econs.size());;
          // printMem();
          // E.msgcnt = 0;
        }// end for envsLoop
      } // end dostop else 

      if (doStop || eM.dfe()) {
        stateConst = STOPPED;
      } else {
        paintFutureFundEconCreate();
        E.msgcnt = 0;
        int nClans = E.clan.values().length - 3;
        int finishedClans = 0, clansLoop = clanBias; // end when all 5 clans can create no more econs
        if (E.debugEconCnt) {
          synchronized (EM.econCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
        }
        for (finishedClans = 0; clansLoop < 50 && finishedClans < 5 && eM.econCnt <= eM.econLimits3[0] && !eM.dfe(); clansLoop++) {
          econClan = (int) (clansLoop) % 5;
          startEconState = (new Date()).getTime();
          double limits3 = eM.econCnt - eM.econLimits3[0];
          double mDif = limits3 > E.PZERO ? limits3 / 5 : 1.;
          //clanWorth over econLimits1 is at least initialWorth*4, otherwise just initial worth
          //double clanWorth = eM.econCnt > eM.econLimits1[0] ? Math.max(eM.initialWorth[0] * 4., eM.clanFutureFunds[econClan] / ((eM.econLimits3[0] - eM.econCnt) / 5.)) : eM.initialWorth[0];
          int econPorS = EM.getNewPorS(econClan);
          double clanWorth = EM.getInitialEconWorth(econPorS, econClan);
          // now make a econ if FFunds > clanWorth
          finishedClans++; // stop is if none of the next five
          if (eM.clanFutureFunds[econClan] > clanWorth) {
            System.out.println("year" + eM.year + " " + "  clan=" + econClan + " initial clan worth=" + clanWorth + " econCnt=" + eM.econCnt);
            finishedClans = 0; // start over finished future create clans
            EM.setCurEcon(ec = curEc = newEcon(clanWorth, econPorS, econClan));  // include new of Econ
            if (E.debugEconCnt) {
              synchronized (EM.econCnt) {
                if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                  EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
                }
              }
            }
            curWorth = eM.curEcon.getWorth();
            curWorth = clanWorth; // what we put in
            EM.curEcon.as.setStat(EM.FUTURECREATE, EM.curEcon.pors, eM.curEcon.clan, curWorth, 1);
            EM.curEcon.as.setStat(EM.BOTHCREATE, EM.curEcon.pors, eM.curEcon.clan, curWorth, 1);
            //eM.clanFutureFunds[econClan] -= eM.initialWorth[eM.curEcon.pors];
            eM.clanFutureFunds[econClan] -= curWorth;
            System.out.println("++++++++year" + eM.year + " " + (new Date().getTime() - EM.doYearTime) + " FFCreated " + E.clanLetter[eM.curEcon.clan] + " " + Econ.nowName + E.clanLetter[eM.curEcon.clan] + " worth " + EM.mf(curWorth) + ":" + EM.mf(EM.curEcon.getWorth()) + " econssize=" + eM.econs.size());
          } // opasd  [\P]      
        } // end clansLoop

        // now initialize the first 2 Econ for logDisplay if it is null, the first time through
        if (eM.logEnvirn[0] == null || eM.hists[0] == null) {
          int lecon1 = eM.econs.size();
          Econ econ1 = eM.econs.get(0);

          setLogEnvirn(0, eM.econs.get(0));
          eM.hists[0] = eM.logEnvirn[0].hist;
          //  String msgLine1 = " Add eM.hists[0], setLogEnvirn 0, " + eM.year + "=year " + eM.econs.get(0).name + "=ship" + (eM.econs.get(0).getDie() ? " is dead" : " live");
          // System.out.println(dnow.toString() + " " + msgLine1);
        }
        if (eM.logEnvirn[1] == null || eM.hists[1] == null && eM.econs.size() > 2) {
          setLogEnvirn(1, eM.econs.get(1));
          eM.hists[1] = eM.logEnvirn[1].hist;
          //  String msgLine1 = dnow.toString() + " Add eM.hists[1], setLogEnvirn 1, " + eM.year + "=year " + eM.econs.get(1).name + "=ship " + (eM.econs.get(1).getDie() ? " is dead" : " live");
          //  System.out.println(msgLine1);
          //    System.out.println(dnow.toString() + " doYear=" + eM.year + " logE0=" + eM.logEnvirn[0].getName() + "," + eM.logEnvirn[1].getName());
        }
      } // end doStop future fund
      if (doStop || eM.dfe()) {
        stateConst = STOPPED;
      } else {
        //stateConst = STARTYR;
        paintStartYear();
        //curStateName = "startYear";
        E.msgcnt = 0;
        // ignored planetsStart 0:planets.size(). start the years
        for (planetsLoop = 0; planetsLoop < eM.planets.size() && !eM.dfe(); planetsLoop++) {
          ec = curEc = eM.curEcon = eM.planets.get(planetsLoop);
          String msgLine0 = since() + "Planet yearStart " + eM.year + " " + eM.curEcon.name + "=planet" + (eM.curEcon.getDie() ? " is dead" : " live");
          //  System.out.println(msgLine0);
          startEconState = (new Date()).getTime();
          paintCurDisplay(eM.curEcon);
          if (!eM.curEcon.getDie()) {
            //   E.msgcnt = 0;
            eM.curEcon.yearStart(0.);
            //      paintStartYear(eM.curEcon);
          } else {
            //      EM.gameRes.DEAD.set(eM.curEcon.pors, eM.curEcon.clan, 1.);

          }
        }
      }
      //shipsSizeN1 = ships.size()-1
      // do costs and trades here, ships initiate trades
      Econ[] wildCurs = new Econ[(int) eM.wildCursCnt[0][0]];
      if (E.debugEconCnt) {
        synchronized (EM.econCnt) {
          if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
            EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
          }
        }
      }
      // start trading the newest planets/ships get first choice, and make the first trades
      // assume newest planets are by newest ships, 
      EM.wasHere6 = "-----BT---- before trading starts, " + stateStringNames[stateConst] + " year=" + eM.year;
      if(E.debugStatsOut1)System.out.println(EM.wasHere6);
      paintTrade(curEc, curEc);
    //  curStateName = "trading";
      E.msgcnt = 0;
      if (doStop || eM.dfe()) {
        paintStopped();
        stateConst = STOPPED;
      } else {

        printMem3();
        for (int n = 0; n < E.LCLANS && !eM.dfe(); n++) {
          clanShipsDone[n] = 0;
        }
        // go latest to earliest, smallest to largest trades
        for (shipsLoop = eM.ships.size() - 1; shipsLoop >= 0 && !eM.dfe(); shipsLoop--) {
          eM.setCurEcon(ec = curEc = eM.ships.get(shipsLoop));
          //startEconState = (new Date()).getTime();
          //paintCurDisplay(eM.curEcon);
          E.msgs = E.dmsgs;;  // reset sysMsg counter before each trade

          if (!eM.curEcon.getDie()) {  //live
            // ship selects its next planet, from offer list and wildCurs
            Econ cur1 = eM.curEcon;
            clanShipsDone[cur1.clan]++; // count clan of ship
            double distance = 0.0;
            //ArrayList<Econ> tradablePlanets = new ArrayList<Econ>();

            int foundTradablePlanets = 0;
            int nTradablePlanets = (int) (eM.wildCursCnt[0][0]);
            Econ[] tradablePlanets = new Econ[nTradablePlanets];
            int shipCnt = eM.ships.size() - 1 - shipsLoop;
            foundTradablePlanets = getWildCurs(shipCnt, cur1, nTradablePlanets, tradablePlanets);
            if (foundTradablePlanets > 0) {
              Econ cur2 = eM.curEcon.selectPlanet(tradablePlanets, foundTradablePlanets);
              if (E.debugEconCnt) {
                synchronized (EM.econCnt) {
                  if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                    EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
                  }
                }
              }
              if (cur2 != null) {
                //  System.out.println(" @@@@@@Ship=" + eM.curEcon.getName() + ", loop select planet=" + cur2.getName() + " distance=" + eM.curEcon.mf(calcLY(eM.curEcon,cur2)));
                distance = calcLY(eM.curEcon, cur2);
                clearHist(eM.logEnvirn[1]);
                setLogEnvirn(1, cur2);  // set start2
                eM.hists[1] = eM.logEnvirn[1].hist;
              }
              clearHist(eM.logEnvirn[0]);
              eM.setCurEcon(ec = curEc = eM.curEcon = cur1);
              //startEconState = (new Date()).getTime();
              setLogEnvirn(0, eM.curEcon);  // set start1
              eM.hists[0] = eM.logEnvirn[0].hist;
              //  distance = distance < .01 ? eM.nominalDistance[0] : distance; // add arbitrary distance if none
              //  eM.curEcon = cur1;  // was a repeat
              E.msgcnt = 0;
             // paintEconYearStart(eM.curEcon);
             //curStateName = "econYrStrt";
              eM.curEcon.yearStart(distance);
              if (E.debugEconCnt) {
                synchronized (EM.econCnt) {
                  if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                    EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
                  }
                }
              }
              //    E.msgcnt = 0;
              eM.setCurEcon(ec = curEc = eM.curEcon = cur1);
              paintTrade(eM.curEcon, cur2);
             // startEconState = (new Date()).getTime();
              //     eM.curEcon.sStartTrade(eM.curEcon, cur2);
              // paintTrade(eM.curEcon,cur2);
            } else {
              //       EM.gameRes.DEAD.set(eM.curEcon.pors, eM.curEcon.clan, 1.);

            }
          }

          System.out.println("================" + since() + " after ship barter year =" + eM.year + ", ship=" + eM.curEcon.name + (eM.curEcon.getDie() ? " is dead" : " is live"));

          printMem();
        }//shipsLoop
        // after all trades, end year for all economies.
        // now initialize the first 2 Econ if one is null, the first time through
        if (eM.logEnvirn[0] == null || eM.hists[0] == null) {
          String msgLine1 = new Date().toString() + "Add eM.hists[0], setLogEnvirn 0, " + eM.year + "=year " + eM.econs.get(0).name + "=ship" + (eM.econs.get(0).getDie() ? " is dead" : " live");
          System.out.println(msgLine1);
          setLogEnvirn(0, eM.econs.get(0));
          eM.hists[0] = eM.econs.get(0).hist;

        }
        if (eM.logEnvirn[1] == null || eM.hists[1] == null) {
          setLogEnvirn(1, eM.econs.get(1));
          eM.hists[1] = eM.econs.get(1).hist;
        }
      } // else doStop
      // end each year and build the final namelist
      namesList.clear();
      setEconState(DOYEAREND);
      E.msgcnt = 0;
      int maxEcons = eM.econs.size();
      if (doStop || eM.dfe()) {
        stateConst = STOPPED;
      } else {
       // curStateName = "ecYrEnds";
        if (E.debugEconCnt) {
          /*synchronized (EM.econCnt) */
          {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
        }

        // loop to end years
        for (envsLoop2 = 0; envsLoop2 < maxEcons && !eM.dfe(); ++envsLoop2) {
          if (E.debugEconCnt) {
            /* synchronized (EM.econCnt) */
            {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1] + " envsLoop2=" + envsLoop2);
              }
            }
          }
          ec = curEc = EM.econs.get(envsLoop2);
          EM.setCurEcon(ec);
          if (E.debugEconCnt) {
            /* synchronized (EM.econCnt) */
            {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
         // startEconState = (new Date()).getTime();
          EM.wasHere = "after startEconState ";
          EM.twh1 = new Date().getTime();
          if (E.debugEconCnt) {
            /* synchronized (EM.econCnt) */
            {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          EM.wasHere = "after startEconState econCnt ";
          EM.twh1 = new Date().getTime();
          //    System.out.printf(new Date().toString() + " in doYear at envsLoop2 econ.yearEnd() name=" + EM.curEcon.name);
          // now reset the log environ to this current econ
          if (0 == envsLoop2 % 25) {
            printMem3();
          }
          EM.wasHere = "after printMem3 ";
          EM.twh1 = new Date().getTime();
          if (E.debugEconCnt) {
            /*synchronized (EM.econCnt) */
            {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          EM.wasHere = "after printMem3 econCnt ";
          EM.twh1 = new Date().getTime();
          clearHist(EM.logEnvirn[0]);
          EM.wasHere = "after clearHist  ";
          EM.twh1 = new Date().getTime();
          if (E.debugEconCnt) {
            /* synchronized (EM.econCnt) */
            {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          EM.wasHere = "after clearHist econCnt ";
          EM.twh1 = new Date().getTime();
          setLogEnvirn(0, EM.curEcon);  // set start1
          EM.hists[0] = EM.logEnvirn[0].hist;
          E.msgcnt = 0;
          EM.wasHere = "after setLogEnvirn ";
          EM.twh1 = new Date().getTime();

          if (E.debugEconCnt) {
            /*synchronized (EM.econCnt)*/ {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          EM.wasHere = "after setLogEnvirn econCnt)";
          EM.twh1 = new Date().getTime();
          EM.curEcon.doYearEnd(); // finally Assets.CashFlow.yearEnd()
          EM.wasHere = "after EM.curEcon.doYearEnd()";
          EM.twh1 = new Date().getTime();
          if (E.debugEconCnt) {
            /* synchronized (EM.econCnt) */
            {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          //   paintEconEndYear(EM.curEcon);

          if(E.debugDidEconYearEnd)System.out.println("-----DYE------" + " after year end cnt=" + envsLoop2 + " of" + maxEcons + " "  + EM.sinceRunYear() + " " + EM.curEconName + (EM.curEcon.getDie() ? " is dead" : " is alive ") + groupNames[EM.curEcon.clan] + " h=" + EM.curEcon.df(EM.curEcon.getHealth()) + ", age=" + EM.curEcon.getAge() + ", w=" + EM.curEcon.df(EM.curEcon.getWorth()));
          if (E.debugEconCnt) {
            synchronized (EM.econCnt) {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
          String disp1 = (EM.curEcon.getDie() ? " is dead " : " is alive ") + groupNames[EM.curEcon.clan] + " " + EM.curEconName + " h="
                  + EM.curEcon.df(EM.curEcon.getHealth()) + ", age=" + EM.curEcon.age
                  + ", w=" + EM.curEcon.df(EM.curEcon.getWorth());
          System.out.println(new Date().toString() + disp1);
          namesList.add(envsLoop2, disp1);
          if (E.debugEconCnt) {
            synchronized (EM.econCnt) {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
          }
        } // finish curEcon.yearEnd

        //wait for doEndYearCnt to zero, finish all yearEnd
        if (E.debugThreads) {
          System.out.println("-------EYw-----Waiting Ending year=" + EM.andET());
        }
        setEconState(WAITING);
        if (E.debugEconCnt) {
          synchronized (EM.econCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
        }
        EM.curEcon.imWaiting(Econ.doEndYearCnt, 0, 4, "doYear ended yearEnds");
        if (E.debugEconCnt) {
          synchronized (eM.syncE) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
        }
        setEconState(ENDYR);
        if (E.debugThreads) {
          System.out.println("------EYg-----Ending year=" + EM.andET());
        }
        // preset counts to zero, they will be counted next
        EM.econCnt = 0;
        EM.planets.clear();
        EM.ships.clear();
        for (int m = 0; m < 2; m++) {
          EM.porsCnt[m] = 0;
          for (int n = 0; n < 5; n++) {
            EM.clanCnt[n] = 0; // doing twice
            EM.porsClanCnt[m][n] = 0;
          }// n
        }// m

        // now set the counts and planets and ships
        synchronized (EM.econCnt) {
          for (Econ t : EM.econs) {
            if (!t.getDie()) {
              EM.porsClanCnt[t.pors][t.clan]++;
              EM.clanCnt[t.clan]++;
              EM.porsCnt[t.pors]++;
              EM.econCnt++;
              //        EM.names2ec.put(t.name, t);
              if (t.pors == P) {
                EM.planets.add(t);
              } else {
                EM.ships.add(t);
              }
            }
          }
        }
        namesList.clear();
       // stateConst = ENDYR; already done
        maxEcons = EM.econs.size();
        for (envsLoop2 = 0; envsLoop2 < maxEcons && !EM.dfe(); ++envsLoop2) {
          EM.setCurEcon(ec = curEc = EM.econs.get(envsLoop2));
          //    System.out.printf(new Date().toString() + " in doYear at envsLoop2 econ.yearEnd() name=" + EM.curEcon.name);

          String disp1 = (EM.curEcon.getDie() ? " is dead " : " is alive ") + groupNames[EM.curEcon.clan]
                  + " " + EM.curEcon.name + " h=" + EM.curEcon.df(EM.curEcon.getHealth())
                  + ", age=" + EM.curEcon.age
                  + ", w=" + EM.curEcon.df(EM.curEcon.getWorth());
          System.out.println(new Date().toString() + disp1);
          namesList.add(envsLoop2, disp1);
        } // finish curEcon.name list

        long[][][] resii1 = eM.resI[0];
        long[][] resi2 = resii1[1];
        long[] resi23 = resi2[2];
        EM.wasHere = "before EM.doEndYear()";
        eM.doEndYear();
        EM.wasHere = "after EM.doEndYear()";
        long[][][] resii = eM.resI[0];
        long[][] resii2 = resii[1];
        long[] resi3 = resii2[2];
        //    gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);

        // System.out.print(EM.curEcon.name + since() + " after gamePanelChange");
        printMem3();
      }
      String eMCurEcon = EM.curEconName;
      EM.wasHere = "In doYear finally econ=" + eMCurEcon;
      String xxx = since() + " In doYear finally econs=" + EM.econs.size();
      ec = curEc = EM.curEcon; // just to be safe
      if (EM.curEcon != null && EM.curEcon.name != null) {
        xxx += " name=" + EM.curEconName;
      }
      xxx += " econsCnt=" + EM.econCnt;

      System.out.println(xxx);
      int row = 0;
//      row = displayLog();  //unknown value
      /**
       * initialize event for namesList, change not during a year
       */
      logEnvirnNamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ListSelectionModel rowSM = logEnvirnNamesList.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          //Ignore extra messages.
          NumberFormat whole = NumberFormat.getNumberInstance();
          whole.setMaximumFractionDigits(0);
          if (e.getValueIsAdjusting()) {
            EM.wasHere = "econ=" + eMCurEcon + " doYear finally Names: adjusting";
            System.out.println(since() + " " + eMCurEcon + " doYear finally Names: adjusting");
            setEconState(RUNSDONE);
            return;
          }
          ListSelectionModel lsm = (ListSelectionModel) e.getSource();
          if (lsm.isSelectionEmpty()) {
            System.out.println(since() + "Names: No rows are selected.");
            EM.wasHere = "econ=" + eMCurEcon + " doYear finally Names: No rows are selected.";
          } else {
            int selectedRow = lsm.getMinSelectionIndex();

            namesListRow = selectedRow;
            EM.curEcon = EM.econs.get(namesListRow);
            setLogEnvirn(E.dN, EM.curEcon);
            System.out.println(since() + "ListSelectionModel namesListRow=" + namesListRow + "name=" + EM.curEcon.name);
            EM.wasHere = "econ=" + eMCurEcon + " doYear finally Names: ListSelectionModel namesListRow=" + namesListRow + ".";
            //     int row = displayLog();  //unknown value
          }
        }
      });
      EM.wasHere = "at end of doYear try";

    } // try
    catch (WasFatalError ex) {
      ex.printStackTrace(EM.pw);
      EM.thirdStack = EM.sw.toString();
      EM.flushes();
      System.err.println("do Year aWasFatalError=" + ex.toString() + " " + since() + " " + EM.curEconName + " " + Econ.nowThread + EM.andMore());
      EM.flushes();
      System.exit(-21);
      //ex.printStackTrace(System.err);
      // go to finally
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      EM.flushes();
      System.err.println(EM.tError = ("-----BBB----doYear bException=" + ex.toString() + " " + since() + " " + EM.curEconName + " " + Econ.nowThread + ", cause=" + ex.getCause() + ",  message=" + ex.getMessage() + " " + EM.andMore()));
      // ex.printStackTrace(System.err);
      EM.flushes();
      System.exit(-25);
      setFatalError();
      throw new WasFatalError(EM.tError);
    } finally {
      setEconState(RUNSDONE);
      EM.flushes();
      if (EM.dfe()) {
        return;
      }
      /**
       * now initialize values for display
       */
//    E.incrFracStaffForRes[1][4]++;

      EM.wasHere = "----DYy----econ=" + Econ.nowName + " doYear in finally at end" + stateStringNames[stateConst] + "Y" + EM.year;
    }// end finally
    EM.wasHere = "----DYz----econ=" + Econ.nowName + sinceA() + sinceAA() + " doYear at end after finally " + stateStringNames[stateConst] + "Y" + EM.year;
    if (E.debugDoYearOut) {
      System.out.println(EM.wasHere);
    }
    return;
  } // end doYear

  void paintEconCreate() {
    setEconState(CREATING);
  }

  void paintFutureFundEconCreate() {
    setEconState(FUTUREFUNDCREATE);
  }

  void paintSearch() {
    setEconState(SEARCH);
  }

  void paintStopped() {
    setEconState(STOPPED);
  }

  void paintTrade(Econ curEc, Econ ec2) {
    setEconState(TRADING);
  }

  void paintEconYearStart(Econ ec1) {
    setEconState(STARTING);
  }

  void paintTradeRejected(Econ curEc, Econ ec2) {
    setEconState(TRADING);
  }

  void paintTradeLost(Econ curEc, Econ ec2) {
    setEconState(TRADING);
  }

  void paintSwaping() {
    setEconState(SWAPS);

  }

  void paintWaiting() {
    setEconState(WAITING);
  }

  int curDisplayPrints = 0;
  int gameE = 0, gameP = 0, gameS = 0, rcgsE = 0, rcsgP = 0, rcsgS = 0;
  double gameMaxW = 0., gameMinW = 0.;

  /**
   * do the animated display in tab display for the animation thread
   *
   * @param curEc the current econ usually EM.curEcon
   */
  void paintCurDisplay(Econ curEc) {
    try {
      int numEcons = EM.econs.size();
      int rN = 999999;
      int loopCntr = 0;
      int cpIx1 = 8, cpIx2 = 7, cpIx3 = 9, cpIx4 = 6;
      int loopChange = 100; // every .1 seconds
      String newLine = "\n";
      String line1 = "", line0 = "", line2 = "", line3 = "", line4 = "", line5 = "";
      //   controlPanels.setVisible(true);
      int rNWorth = EM.STARTWORTH;
      // static final int INITRCSG = ++e4;
      // static final int LIVERCSG = ++e4;
      int rNinitrcsg = EM.INITRCSG;
      int rNlivercsg = EM.LIVERCSG;
      double maxWealth = 5., minWealth = 5., totalWealth = 5., wTot = 0.;
      double pworth = 0., sworth = 0., bworth = 0.;
      double initBrcsg = 0., initSrcsg = 0., initPrcsg = 0., brcsg = 0., prcsg = 0., srcsg = 0.;
      if ((loopCntr++ % loopChange) == 0) {
        double aWorth = 0., wMax = 0., wMin = 100000000000000000.;
        // for(int lcnt=0;lcnt < E.LCLANS; lcnt++){
        sworth += eM.getCurCumPorsClanAve(rNWorth, EM.ICUR0, 1, E.S, E.S + 1, 0, 5);
        pworth += eM.getCurCumPorsClanAve(rNWorth, EM.ICUR0, 1, E.P, E.P + 1, 0, 5);
        aWorth = eM.getCurCumPorsClanAve(rNWorth, EM.ICUR0, 1, E.P, E.S + 1, 0, 5);
        initSrcsg += eM.getCurCumPorsClanAve(rNinitrcsg, EM.ICUR0, 1, E.S, E.S + 1, 0, 5);
        initPrcsg += eM.getCurCumPorsClanAve(rNinitrcsg, EM.ICUR0, 1, E.P, E.P + 1, 0, 5);
        initBrcsg = eM.getCurCumPorsClanAve(rNinitrcsg, EM.ICUR0, 1, E.P, E.S + 1, 0, 5);
        srcsg += eM.getCurCumPorsClanAve(rNlivercsg, EM.ICUR0, 1, E.S, E.S + 1, 0, 5);
        prcsg += eM.getCurCumPorsClanAve(rNlivercsg, EM.ICUR0, 1, E.P, E.P + 1, 0, 5);
        brcsg = eM.getCurCumPorsClanAve(rNlivercsg, EM.ICUR0, 1, E.P, E.S + 1, 0, 5);
        bworth = aWorth;

        wTot = aWorth;
        wMin = aWorth < wMin ? aWorth : wMin;
        wMax = aWorth > wMax ? aWorth : wMax;
        //}
        totalWealth = wTot;
        maxWealth = wMax;
        minWealth = wMin;
        //  totalWealth = totalWealth < totalWealth? totalWealth:yearTW[0] ;
        yearTW[0] = wTot;
        gameTW[0] = gameTW[0] < totalWealth ? totalWealth : gameTW[0];
        gameMaxW = gameMaxW < maxWealth ? maxWealth : gameMaxW;
        gameMinW = gameMinW < minWealth ? minWealth : gameMinW;
        yearEcons[0] = EM.econCnt;
        yearPlanets[0] = EM.porsCnt[E.P];
        yearShips[0] = EM.porsCnt[E.S];
        gameE = gameEcons[0] = gameEcons[0] < EM.econCnt ? EM.econCnt : gameEcons[0];
        gameP = gamePlanets[0] = gamePlanets[0] < EM.porsCnt[E.P] ? EM.porsCnt[E.P] : gamePlanets[0];
        gameS = gameShips[0] = gameShips[0] < EM.porsCnt[E.S] ? EM.porsCnt[E.S] : gameShips[0];
      }
      controlPanels.getComponent(3);
      controlPanels.setSelectedIndex(3);
      displayPanel0Text.setRows(18);
      // displayPanel0Text.setSize(1200,750);
      // display.setVisible(false);
      //displayPanel1SinceYearStart.setVisible(false)

      int rNyCreated = eM.YEARCREATE;
      int rNCreated = rN = eM.BOTHCREATE;
      int rNLiveWorth = eM.LIVEWORTH;
      int rNFutCreated = eM.FUTURECREATE; // FUTURECREATE
      int rNTraded = EM.TradeLastStrategicValue;
      int rNAlsoTraded = EM.AlsoTradeLastStrategicValue;
      int rNRejected = EM.TradeRejectedStrategicValue;
      int rNLost = EM.TradeLostStrategicValue;
      int rNHlpdSos1 = eM.TRADEOSOS1;
      int rNHlpdSos2 = eM.TRADEOSOS2;
      int rNHlpdSos3 = eM.TRADEOSOS3;
      int rNDied = eM.DIED;
      int rNDAcc = EM.DTRADEACC;
      int rNDLost = EM.TradeDeadLostStrategicValue;
      int rNDRej = EM.TradeDeadRejectedStrategicValue;
      int rNCrisis = eM.getStatrN("sCatCosts");
      int rNLstS1 = EM.TRADEOSOSR1;
      int rNDS1 = EM.DTRADEOSOSR1;
      int rNLstS2 = EM.TRADEOSOSR2;
      int rNDS2 = EM.DTRADEOSOSR2;
      int rNLstS3 = EM.TRADEOSOSR3;
      int rNDS3 = EM.DTRADEOSOSR3;
      //   controlPanels.setSelectedIndex(3);
      int blip = 5;

      if (curEc == null) {

      } else { // curEc != null
        //      econCnt = curEc.econCnt;

        //String linez =  "both=" + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine  + since () + sinceRunYear() + newLine;
        // line0 = line1 = line2 = line3 = line4 = line5 = "";
        // line0 = stateStringNames[stateConst];
        if (eM.haveColors[0][0] > 1.2) {
          displayPanel0Text.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
        }
        cpIx1 = controlPanels.getSelectedIndex();
        controlPanels.setSelectedIndex(3);
        cpIx2 = controlPanels.getSelectedIndex();
        // displayPanel0Text.setBackground(E.clan.values()[curEc.getClan()].getColor(curEc.pors));
        displayPanel0Text.setBackground(new Color(0x00849A));
        Color myTest = E.clan.values()[curEc.getClan()].getColor(curEc.pors);
        String disp1 = "year" + eM.year + " ";
        disp1 += (stateConst == TRADING ? tradingEcon() : sinceEcon());
        disp1 += " " + EM.econCnt + ":" + EM.econs.size() + " Planets=" + EM.porsCnt[E.P] + " ships=" + EM.porsCnt[E.S] + " dead=" + EM.deadCnt + newLine
                + " Total Wealth=" + EM.mf(totalWealth) + " minWealth=" + EM.mf(minWealth) + " maxWealth=" + EM.mf(maxWealth) + newLine
                + "yrAveWorth =" + EM.mf(bworth) + " Planets " + EM.mf(pworth) + " Ships " + EM.mf(sworth) + newLine
                + "initAveRCSG =" + EM.mf(initBrcsg) + " Planets " + EM.mf(initPrcsg) + " Ships " + EM.mf(initSrcsg) + newLine
                + "iyrAveRCSG =" + EM.mf(brcsg) + " Planets " + EM.mf(prcsg) + " Ships " + EM.mf(srcsg) + newLine
                + "TradedYear " + eM.getCurCumPorsClanUnitSum(rNTraded, EM.ICUR0, E.P, E.S + 1, 0, E.LCLANS) + " Planets " + eM.getCurCumPorsClanUnitSum(rNTraded, EM.ICUR0, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNTraded, EM.ICUR0, E.S, E.S + 1, 0, 5) + newLine
                + "GameYrs    " + eM.getCurCumPorsClanUnitSum(rNLiveWorth, EM.ICUM, E.P, E.S + 1, 0, 5) + " Planets " + eM.getCurCumPorsClanUnitSum(rNLiveWorth, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNLiveWorth, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine
                + "TradedGame " + eM.getCurCumPorsClanUnitSum(rNTraded, EM.ICUM, E.P, E.S + 1, 0, 5) + " also:" + eM.getCurCumPorsClanUnitSum(rNAlsoTraded, EM.ICUM, E.P, E.S + 1, 0, 5) + " rej:" + eM.getCurCumPorsClanUnitSum(rNRejected, EM.ICUM, E.P, E.S + 1, 0, 5) + " lost:" + eM.getCurCumPorsClanUnitSum(rNLost, EM.ICUM, E.P, E.S + 1, 0, 5) + " Planets " + eM.getCurCumPorsClanUnitSum(rNTraded, EM.ICUM, E.P, E.P + 1, 0, 5) + " :" + eM.getCurCumPorsClanUnitSum(rNAlsoTraded, EM.ICUM, E.P, E.P + 1, 0, 5) + " :" + eM.getCurCumPorsClanUnitSum(rNRejected, EM.ICUM, E.P, E.P + 1, 0, 5) + " :" + eM.getCurCumPorsClanUnitSum(rNLost, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNTraded, EM.ICUM, E.S, E.S + 1, 0, 5) + " :" + eM.getCurCumPorsClanUnitSum(rNAlsoTraded, EM.ICUM, E.S, E.S + 1, 0, 5) + " :" + eM.getCurCumPorsClanUnitSum(rNRejected, EM.ICUM, E.S, E.S + 1, 0, 5) + " :" + eM.getCurCumPorsClanUnitSum(rNLost, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine
                + "BothCreated " + eM.getCurCumPorsClanUnitSum(rNCreated, EM.ICUM, E.P, E.S + 1, 0, 5) + " Planets " + eM.getCurCumPorsClanUnitSum(rNCreated, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNCreated, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine
                + "GameCreated" + eM.getCurCumPorsClanUnitSum(rNyCreated, EM.ICUM, E.P, E.S + 1, 0, 5) + " Planets " + eM.getCurCumPorsClanUnitSum(rNyCreated, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNyCreated, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine
                + "FutCreated  " + eM.getCurCumPorsClanUnitSum(rNFutCreated, EM.ICUM, E.P, E.S + 1, 0, 5) + " Planets " + eM.getCurCumPorsClanUnitSum(rNFutCreated, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNFutCreated, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine
                + "GCatastrophies " + eM.getCurCumPorsClanUnitSum(rNCrisis, EM.ICUM, E.P, E.S + 1, 0, 5) + " Planets " + eM.getCurCumPorsClanUnitSum(rNCrisis, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNCrisis, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine
                + "Difficulty" + EM.difficultyPercent[0] + " DiedGame "
                + eM.getCurCumPorsClanUnitSum(rNDied, EM.ICUM, E.P, E.S + 1, 0, 5)
                + ":acc=" + eM.getCurCumPorsClanUnitSum(rNDAcc, EM.ICUM, E.P, E.S + 1, 0, 5)
                + ":rej=" + eM.getCurCumPorsClanUnitSum(rNDRej, EM.ICUM, E.P, E.S + 1, 0, 5)
                + ":lost=" + eM.getCurCumPorsClanUnitSum(rNDLost, EM.ICUM, E.P, E.S + 1, 0, 5)
                + " Planets " + eM.getCurCumPorsClanUnitSum(rNDied, EM.ICUM, E.P, E.P + 1, 0, 5)
                + ":" + eM.getCurCumPorsClanUnitSum(rNDAcc, EM.ICUM, E.P, E.P + 1, 0, 5)
                + ":" + eM.getCurCumPorsClanUnitSum(rNDRej, EM.ICUM, E.P, E.P + 1, 0, 5)
                + ":" + eM.getCurCumPorsClanUnitSum(rNDLost, EM.ICUM, E.P, E.P + 1, 0, 5)
                + " Ships " + eM.getCurCumPorsClanUnitSum(rNDied, EM.ICUM, E.S, E.S + 1, 0, 5)
                + ":" + eM.getCurCumPorsClanUnitSum(rNDAcc, EM.ICUM, E.S, E.S + 1, 0, 5)
                + ":" + eM.getCurCumPorsClanUnitSum(rNDRej, EM.ICUM, E.S, E.S + 1, 0, 5)
                + ":" + eM.getCurCumPorsClanUnitSum(rNDLost, EM.ICUM, E.S, E.S + 1, 0, 5)
                + newLine;
        int tmp1;
        if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNLstS1, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "LostSOS1 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNLstS1, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNLstS1, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        } else if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNLstS2, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "LostSOS2 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNLstS2, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNLstS2, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        } else if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNLstS3, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "LostSOS3 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNLstS3, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNLstS3, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        }
        if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNHlpdSos1, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "HelpdSOS1 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNHlpdSos1, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNHlpdSos1, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        } else if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNHlpdSos2, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "HelpdSOS2 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNHlpdSos2, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNHlpdSos2, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        } else if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNHlpdSos3, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "HelpdSOS1 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNHlpdSos3, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNHlpdSos3, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        }
        if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNDS1, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "DiedRSOS1 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNDS1, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNDS1, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        } else if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNDS2, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "DiedRSOS2 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNDS2, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNDS2, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        } else if ((tmp1 = eM.getCurCumPorsClanUnitSum(rNDS3, EM.ICUM, E.P, E.S + 1, 0, 5)) > 0) {
          disp1 += "DiedRSOS3 " + tmp1 + " Planets " + eM.getCurCumPorsClanUnitSum(rNDS3, EM.ICUM, E.P, E.P + 1, 0, 5) + " Ships " + eM.getCurCumPorsClanUnitSum(rNDS3, EM.ICUM, E.S, E.S + 1, 0, 5) + newLine;
        }
        disp1 += "year" + eM.year + " Threads=" + Econ.getThreadCnt() + ":" + Thread.activeCount() + " " + EM.wasHere8 + " " + since() + " " + sinceRunYear() + "  " + newLine + prGigMem + newLine;

        /*
       + "==millisecs econ per year= millisecs per year/econs  ===========" + newLine;
        for (int yy = 0; yy < yearsL && yearEcons[yy] > -1; yy += 5) {
          for (int y = yy; y < 5 + yy && yearEcons[y] > -1; y++) {
            disp1 += "(" + theYear[y] + ")" + (int)(yearSecPerEcon[y]) + " " + yearSecs[y] + "/" + yearEcons[y] + ", ";
          }
          disp1 += newLine;
         */
        disp1 += "===== (year) Tsecs/econs  WyearWorth/gameWorth  EyearEcons/gameEcons  PyearPlanets/gameP S yearShips/gameShips ===========" + newLine;
        int entPerRow = 2;

        for (int y = 0; y < yearsL && theYear[y] > -1;) {
          int yMax = y + entPerRow;
          for (; y < yMax && theYear[y] > -1; y++) {
            disp1 += "(" + theYear[y] + ")" + "T" + yearSecPerEcon[y]
                    + " W" + EM.mf(yearTW[y]) + "/"
                    + EM.mf(gameTW[y])
                    + "  E" + yearEcons[y] + "/"
                    + gameEcons[y] + "  P"
                    + yearPlanets[y] + "/"
                    + gamePlanets[y] + "  S"
                    + yearShips[y] + "/"
                    + gameShips[y]
                    + (y < yMax - 1 ? " === " : "");

          }
          disp1 += newLine;
        }
        disp1 += "------------------" + newLine;

        //   System.err.println(" PyearSecs " + EM.mf(yearSecs[0]) + " yearEcons " + EM.mf(yearEcons[0]) + " year S per E " + EM.mf(yearSecPerEcon[0]));
        //  System.err.println(" PyearSecs " + EM.mf(yearSecs[1]) + " yearEcons " + EM.mf(yearEcons[1]) + " year S per E " + EM.mf(yearSecPerEcon[1]));
        // System.err.println(" PyearSecs " + EM.mf(yearSecs[2]) + " yearEcons " + EM.mf(yearEcons[2]) + " year S per E " + EM.mf(yearSecPerEcon[2]));
        switch (stateConst) {
          case WAITING:
            displayPanel0Text.setText(
                    "Waiting     " + disp1);
            break;
          case STARTING:
            displayPanel0Text.setText(
                    "Starting   " + disp1);
            break;
          case CREATING:
            displayPanel0Text.setText(
                    "gameCreate   " + disp1);
          case FUTUREFUNDCREATE:
            displayPanel0Text.setText(
                    "FutFCreate  " + disp1);
            break;
          case STARTYR:
            displayPanel0Text.setText(
                    "StartYear  " + disp1);
            break;
          case SEARCH:
            displayPanel0Text.setText(
                    "Searching  " + disp1);
            break;
          case SWAPS:
            displayPanel0Text.setText(
                    "Swaping    " + disp1);
            break;
          case TRADING:
            displayPanel0Text.setText(
                    "Trading    " + disp1);
            break;
          case ENDYR:
            displayPanel0Text.setText(
                    "EndYear    " + disp1);
            break;
          case DOYEAREND:
            displayPanel0Text.setText(
                    "DoYearEnd " + disp1);
            break;
          case RUNSDONE:
            displayPanel0Text.setText(
                    "RunsDone  " + disp1);
            break;
          case STATS:
            controlPanels.setSelectedIndex(4);
            break;
          case FATALERR:
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            String sw1 = sw.toString();
            displayPanel0Text.setText(
                    "Fatal Error    " + disp1 + "\n" + sw1);
            break;
          default:

        }
      } // curEc != null
      //  displayPanel0Text.setText(line0 + line1 + line2 + line3 + line4 + line5);

      //displayPanel1Operation.setText(stateStringNames[stateConst]);
      // displayPanel1SinceYearStart.setText(sinceRunYear());
      //  displayPanel1EconName.setVisible(true);
      //  displayPanel1Operation.setVisible(true);
      //  displayPanel1SinceYearStart.setVisible(true);
      //  displayPanel1.setVisible(true);
      if (stateConst == STATS) {
        display.setVisible(false);

        controlPanels.setSelectedIndex(4);
        cpIx2 = controlPanels.getSelectedIndex();

        listRes(0, resLoops, fullRes);
        controlPanels.setSelectedIndex(4);
        cpIx3 = controlPanels.getSelectedIndex();
        stats.setVisible(true);
        stats.revalidate();
        stats.repaint();
        display.setVisible(true);
        cpIx4 = controlPanels.getSelectedIndex();
      } else {  // not STATS
        controlPanels.setSelectedIndex(3);
        display.setVisible(true);
        if (eM.haveColors[0][0] > 1.2 && curEc != null) {
          display.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
          displayPanel0.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
          displayPanel0Text.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
        }
        display.revalidate();
        display.repaint();
      }
      controlPanels.setVisible(true);
      if (stateCnt % 50 == 0 || stateConst == STATS) {
        if (curDisplayPrints < E.ssMax * 5) {
          curDisplayPrints++;
          long nTime = new Date().getTime();
          String aLine = "------DA-----paintCurDisplay " + (nTime - startTime) + " " + stateStringNames[stateConst] + "Y" + EM.year + " eCnt=" + EM.econCnt + ":" + EM.econs.size() + ":"
                  + EM.curEconName + "::" + Econ.nowName + " controlPanelIx=" + cpIx1 + ":" + cpIx2 + ":" + cpIx3 + ":" + cpIx4 ;

          System.out.println(aLine);
        }
      }
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      eM.flushes();
      eM.flushes();
      setFatalError();
    }
  }

  void paintStartYear() {
    //   controlPanels.setVisible(true);
    ec = curEc = EM.curEcon;
    setEconState(STARTYR);
    controlPanels.getComponent(3);
    display.setVisible(false);
    displayPanel1.setVisible(false);
    //   displayPanel2.setVisible(false);
    // displayPanel1EconName.setVisible(false);
    // displayPanel1Operation.setVisible(false);
    // displayPanel1SinceYearStart.setVisible(false);
    // displayPanel1EconName.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
    // displayPanel1EconName.setText(curEc.name);
    // displayPanel1Operation.setText("Start Year");
    // displayPanel1SinceYearStart.setText(sinceRunYear());

    //  displayPanel1EconName.setVisible(true);
    // displayPanel1Operation.setVisible(true);
    // displayPanel1SinceYearStart.setVisible(true);
    display.setVisible(true);
    controlPanels.setVisible(true);
    display.revalidate();
    display.repaint();
  }

  /**
   * set variable environment for the logs tab
   *
   * @param dN 0 or 1 for which portion of log table
   * @param En Econ for that table
   */
  public void setLogEnvirn(int dN, Econ En) {
    E.dN = dN;
    if (dN == 0) {
      eM.logEnvirn[0] = En;
      Object aa = E.clan.values()[En.clan];
      Start1Name.setText(En.name);
      //  Start1Name.setForeground(E.clan.values()[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].group]));
      //Start1Name.setForeground(E.clan.values()[eM.logEnvirn[dN].clan].getColor(eM.logEnvirn[dN].pors));
      //Start1Name.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
      //Start1Name.setBackground(new Color(-E.clanColors[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].group]));
    } else {
      E.dN = 1;
      eM.logEnvirn[1] = En;
      Start2Name.setText(En.name);
      // Start2Name.setForeground(E.clan.values()[eM.logEnvirn[dN].clan].getColor(eM.logEnvirn[dN].pors));
      // Start2Name.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
      // Start2Name.setForeground(new Color(E.clanColors[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].clan]));
      // Start2Name.setBackground(new Color(-E.clanColors[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].clan]));
    }
    eM.hists[dN] = En.hists[0];  // applies to both 0,1
    //  System.out.println("setLogEnvirn int=" + dN + " name=" + En.getName() + " year=" + eM.year);
  }

  /**
   * called by a ship to select the next planet for a barter
   *
   * @param curEcon
   * @return
   */
  protected Econ selectPlanet(Econ curEcon) {
    int a = 3;
    return eM.planets.get(0);
  }

  /**
   * calculate the light years to the next planet for a ship
   *
   * @param curEcon the econ of the ship
   * @param cur2 the econ of the candidate planet
   * @return
   */
  protected double calcLY(Econ curEcon, Econ cur2) {
    double x = (curEcon.xpos - cur2.xpos);
    double y = (curEcon.ypos - cur2.ypos);
    double z = (curEcon.zpos - cur2.zpos);
    double xyz = Math.pow(x, 2.) + Math.pow(y, 2.) + Math.pow(z, 2.);
    return Math.sqrt(xyz);
  }

  /**
   * save the current position of a log display
   *
   * @param dN 0,1 which display environment
   * @param M position in that environment
   */
  void saveLogM(int dN, int M) {
    eM.logEnvirn[dN].logM[dN] = M;
  }

  /**
   * set the current position of a log display
   *
   * @param dN 0,1 display environment
   * @param M position in that environment
   */
  void setLogM(int dN, int M) {
    saveLogM(dN, M);
    if (dN == 0) {
      logM1Spinner.setValue(M);
      //   logM1Spinner.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    } else {
      logM2Spinner.setValue(M);
      logM2Spinner.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }
  }

  void saveLogLen(int dN, int len) {
    eM.logEnvirn[dN].logLen[dN] = len;
  }

  void setLogLen(int dN, int len) {
    saveLogLen(dN, len);
    if (dN == 0) {
      LogDlen1Slider.setValue(len);
      //  LogDlen1Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    } else {
      LogDLen2Slider.setValue(len);
      // LogDLen2Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }

  }

  void saveLogLev(int dN, int lev) {
    eM.logEnvirn[dN].logLev[dN] = lev;
  }

  void setLogLev(int dN, int lev) {
    saveLogLev(dN, lev);
    if (dN == 0) {
      logDLevel1Slider.setValue(lev);
      //   logDLevel1Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    } else {
      logDLevel2Slider.setValue(lev);
      //  logDLevel2Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }
  }

  void savLogEnv(int dN, Econ en) {
    eM.logEnvirn[dN] = en;
  }

  public void putInitValues() {
    //  initLimitsHealthMaxSlider.setValue((int) Math.floor(E.resourceGrowth[E.pors]));
  }

  /**
   * before setting initHelperField save the current value up to 3 levels
   *
   * @param txt string to be placed in the helper field
   */
  public void setgameTextField(String txt) {
    E.savedgameTextField4 = E.savedgameTextField3;
    E.savedgameTextField3 = E.savedgameTextField2;
    E.savedgameTextField2 = E.savedgameTextField;
    E.savedgameTextField = gameTextField.getText();
    gameTextField.setText(txt);
  }

  /**
   * revert initHelperField up to 4 levels
   *
   */
  public void revertgameTextField() {
    gameTextField.setText(E.savedgameTextField);
    E.savedgameTextField = E.savedgameTextField2;
    E.savedgameTextField2 = E.savedgameTextField3;
    E.savedgameTextField3 = E.savedgameTextField4;
  }

  int[] curVals1 = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
//  EM.gameVals[] curVals = new EM.gameVals[10];
//  EM.gameVals[] fullVals = EM.gameVals.values();
  int[] d10 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  int[] curVals = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
  int[] fullVals = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

  /**
   * get any values already set into currentVals, ignore disabled elements then
   * disable each element
   *
   * @param currentVals1
   * @param panelAr
   * @param textFieldsAr
   * @param gamePSliders
   * @param gameSSliders}
   */
  public void getGameValues(int[] currentVals1, JPanel[] panelAr, JTextField[] textFieldsAr, JSlider[] gamePSliders, JSlider[] gameSSliders) {
    try {
      int val = 0, v = -1, oldval = 0, gc = -1;
      EM.wasHere5 = "-----GV-----inTo ST.getGameValues " + EM.threadsStacks() + ", clan =" + eM.gameClanStatus;
      if (E.debugSettingsTab) {
        System.out.println(EM.wasHere5);
      }
      for (int p = 0; p < 10; p++) { // go through elements in this panel
        EM.wasHere5 = "in ST.getGameValues " + " getGameValues #" + p + " vv=" + (v = currentVals1[p]) + ", clan =" + eM.gameClanStatus;
        if (E.debugSettingsTab) {
          System.out.print(EM.wasHere5);
        }
        if (v > -1 && panelAr[p].isEnabled()) {
          gc = eM.valI[v][eM.modeC][0][0];
          eM.gamePorS = E.P;
          if (gc <= 4) {
            oldval = eM.valI[v][eM.sliderC][0][0];
          } else {
            oldval = eM.valI[v][eM.sliderC][eM.gamePorS][eM.gameClanStatus];
          }
          val = gamePSliders[p].getValue();
          if (E.debugSettingsTab) {
            System.out.println(" planet name=" + eM.valS[v][0] + ", gc=" + gc + " val=" + val + ", oldval=" + oldval);
          }
          eM.putVal(val, currentVals1[p], E.P, eM.gameClanStatus);
          if (gameSSliders[p].isEnabled()) {
            eM.gamePorS = E.S;
            if (gc <= 2) {
              oldval = eM.valI[v][eM.sliderC][0][eM.gamePorS];
            } else if (gc <= 4) {
              oldval = eM.valI[v][eM.sliderC][eM.gamePorS][0];
            } else {
              oldval = eM.valI[v][eM.sliderC][eM.gamePorS][eM.gameClanStatus];
            }
            val = gameSSliders[p].getValue();
            if (E.debugSettingsTab) {
              System.out.println(" ship name=" + eM.valS[currentVals1[p]][0] + ", gc=" + gc + " val=" + val + ", oldval=" + oldval);
            }
            eM.putVal(val, currentVals1[p], E.S, eM.gameClanStatus);
            // gameSSliders[p].setEnabled(false);
          }
          //  gamePSliders[p].setEnabled(false);
        }
        //  panelAr[p].setEnabled(false);
        EM.wasHere5 = "end ST.getGameValues " + ", clan =" + eM.gameClanStatus;
        if (E.debugSettingsTab) {
          System.out.print(EM.wasHere5);
        }
        EM.flushes();
      }
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      EM.wasHere5 = "-----EXG----end ST.getGameValues " + ", clan =" + eM.gameClanStatus;
      if (E.debugSettingsTab) {
        System.out.print(EM.wasHere5);
      }
      ex.printStackTrace(System.err);
      eM.flushes();
      eM.flushes();
      setFatalError();
    }
  } // gameValues

  /**
   * change a whole gamePanel
   *
   * @param clan 0-4 a clan, 5 the game master use eM.gameClanStatus
   * @param action -2 restart display from the first enum, 0 redisplay page 1 go
   * to the next page, -1 go to the previous page
   * @param panelAr array of 0-9 panels
   * @param textFieldsAr array of 0-9 descriptor fields for a row of sliders
   * @param gamePSliders array of 0-9 planet sliders
   * @param gameSSliders array of 0-9 Ship sliders
   * @param unusedVals fullVals do not change many calls
   * @param currentVals1 the array of panelStarts's starting the next 10 sliders
   */
  public void gamePanelChange(int clan, int action, JPanel[] panelAr, JTextField[] textFieldsAr, JSlider[] gamePSliders, JSlider[] gameSSliders, int[] unusedVals, int[] currentVals1) {
    /**
     * put any values in the display into appropriate places in E. using
     * getGameValues before changing the eM.eM.gameClanStatus
     */
    try {
      getGameValues(currentVals1, panelAr, textFieldsAr, gamePSliders, gameSSliders);
      System.out.println("Enter gamePanelChange clan=" + clan + " gameClanStatus=" + eM.gameClanStatus + " action=" + action + " vvend=" + eM.vvend + " ");
      // now set the new clan
      int v = -1; // sliderc value 0 to 99
      int klan = -1;
      if (clan >= 0) {
        eM.gameClanStatus = clan;
      }
      if (eM.gameClanStatus == 5) {
        if (E.debugSettingsTab) {
          System.out.print("game master panel=" + eM.gPntr);
        }
        klan = 0;
      } else {
        if (E.debugSettingsTab) {
          System.out.print("clan panel" + eM.gameClanStatus + "=" + eM.cPntr);
        }
        klan = clan;
      }
      if (E.debugSettingsTab) {
        System.out.println(" " + new Date().toString());
      }

      nn = 9;
      if (action == -1) { // back up a panel if possible
        if (E.debugSettingsTab) {
          System.out.print("backup a pannel if possible to ");
        }
        if (eM.gameClanStatus == 5) {
          eM.gPntr = Math.max(0, eM.gPntr - 1);
        } else {
          eM.cPntr = Math.max(0, eM.cPntr - 1);
        }
        if (E.debugSettingsTab) {
          System.out.println();
        }
      } else if (action == -2) { // restart from the beginning
        if (eM.gameClanStatus == 5) {
          eM.gPntr = 0;  // rewind
          klan = 0;
          if (E.debugSettingsTab) {
            System.out.print("Restart at the first game master panel ");
          }
        } else {
          eM.cPntr = 0;
          klan = clan;
          if (E.debugSettingsTab) {
            System.out.print("Restart at the first user panel clan=" + clan);
          }
        }
        if (E.debugSettingsTab) {
          System.out.println(new Date().toString());
        }
      } // go to the next set of panels
      else if (action > 0) {
        int savGPntr = eM.gPntr;
        int savCPntr = eM.cPntr;
        if (E.debugSettingsTab) {
          System.out.print("Move to the next ");
        }
        if (eM.gameClanStatus == 5) {
          klan = 0;
          if (eM.gStart[eM.gPntr + 1] < 0) {
            if (E.debugSettingsTab) {
              System.out.print("Remain at the current game-master no additional panel" + eM.gPntr + " ");
            }
          } else {
            klan = 0;
            eM.gPntr++;
            if (E.debugSettingsTab) {
              System.out.print("Move to the next game-master panel=" + eM.gPntr + " ");
            }
          }
        } else {
          klan = clan;
          if (eM.cStart[eM.cPntr + 1] < 0) {
            if (E.debugSettingsTab) {
              System.out.print("Remain at current clan-master panel, not additonal panel=" + eM.cPntr);
            }
          } else {
            eM.cPntr++;
            if (E.debugSettingsTab) {
              System.out.print("Advance to the next clan-master panel" + eM.cPntr);
            }
          }
        }
      }
      // in any case now display upto 10  panels

      if (eM.gameClanStatus == 5) {
        klan = 0;
        eM.vv = eM.gStart[eM.gPntr];
        if (E.debugSettingsTab) {
          System.out.println("Start the next game-master panel at vv =" + eM.vv + " = " + eM.valS[eM.vv][0]);
        }
      } else {
        klan = clan;
        eM.vv = eM.cStart[eM.cPntr];
        int ix = 0;
        int iy = eM.vv;
        if (iy <= eM.valS.length && eM.valS[iy] != null) {
          ix = iy;
        }
        if (E.debugSettingsTab) {
          System.out.println("Start the next clan-master panel at cPntr=" + eM.cPntr + " " + iy + ":" + ix + "=" + eM.valS[ix][0]);
        }
      }
      nn = 0;
      int aclan = eM.gameClanStatus;
      while (eM.vv < eM.vvend && nn < 10) {
        int gc = eM.valI[eM.vv][eM.modeC][0][0];
        boolean isGameMaster = gc >= eM.vone && gc <= eM.vfour;
        int vfive = eM.vfive;
        int vten = eM.vten;
        if (E.debugSettingsTab) {
          System.out.println("line nn=" + nn + " gc=" + eM.valI[eM.vv][eM.modeC][0][0] + " clan=" + eM.gameClanStatus + " ??displaying?? ww=" + eM.vv + " = " + eM.valS[eM.vv][0]);
          // System.out.print(nn);
          // System.out.print("nn=" + nn + " gc=" + eM.valI[eM.vv][eM.modeC][0][0]);
          // System.out.print(eM.gameClanStatus);
          // System.out.print(" ??displaying??=" + eM.vv + " = " + eM.valS[eM.vv][0]);
          // System.out.print(" nn=" + nn + " gc=" + eM.valI[eM.vv][eM.modeC][0][0]);
        }
        // is this vv  match the gameClanStatus
        eM.gamePorS = E.P;
        currentVals1[nn] = eM.vv;  // the object to display
        if (eM.matchGameClanStatus(eM.vv)) {
          if (E.debugSettingsTab) {
            System.out.println(" <<<<<DISPLAY clan planet " + ", desc=" + eM.valS[currentVals1[nn]][0] + ", klan=" + klan + ", line=" + nn + ", sliderV=" + eM.valI[currentVals1[nn]][eM.sliderC][eM.gamePorS][klan] + ", ww=" + currentVals1[nn] + ", clan=" + eM.gameClanStatus);
          }
          // E.sysmsg(" <<<<<DISPLAY clan=" + eM.gameClanStatus);

          panelAr[nn].setEnabled(true);
          panelAr[nn].setVisible(true);
          // gamePanel0.removeMouseListener(l);
          // add listeners for mouse entered and exited
          // do these end being duplicates?
          panelAr[nn].addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
              gamePanel0MouseEntered(evt);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
              gamePanel0MouseExited(evt);
            }
          });

          panelAr[nn].setBackground(E.clan.values()[aclan].getColor(0));
          panelAr[nn].setForeground(E.clan.values()[aclan].getInvColor(0));
          textFieldsAr[nn].setText(eM.valS[currentVals1[nn]][0]);
          eM.gamePorS = E.P;
          gamePSliders[nn].setEnabled(true);
          // gamePSliders[nn].setForeground(Color.blue);
          gamePSliders[nn].setMajorTickSpacing(10);
          gamePSliders[nn].setMinorTickSpacing(2);
          gamePSliders[nn].setPaintLabels(true);
          gamePSliders[nn].setSnapToTicks(false);
          gamePSliders[nn].setVisible(true);
          gamePSliders[nn].setValue(eM.valI[currentVals1[nn]][eM.sliderC][eM.gamePorS][klan]);
          gamePSliders[nn].setMaximumSize(new java.awt.Dimension(350, 35));
          gamePSliders[nn].setMinimumSize(new java.awt.Dimension(150, 35));
          gamePSliders[nn].setPreferredSize(new java.awt.Dimension(250, 35));
          gamePSliders[nn].setValueIsAdjusting(true);

          eM.gamePorS = E.S;
          // is there an s entry, check valI
          int vv = 0, vl = 0, wl = 0;
          double ww = 0.;
          v = -1;
          int sliderC = eM.sliderC;
          if (gc == eM.vfour || gc == eM.vten) {
            v = eM.valI[currentVals1[nn]][sliderC][E.S][klan];
          } else if (gc == eM.vtwo) {
            v = eM.valI[currentVals1[nn]][sliderC][0][E.S];
          }
          //    v = (vl = eM.valI[currentVals1[nn]][sliderC].length) > 1 ? (vv = eM.valI[currentVals1[nn]][sliderC][E.S][klan]) : eM.valI[currentVals1[nn]][sliderC][0].length > 1? (vv = eM.valI[currentVals1[nn]][eM.sliderC][0][E.S]: -1);
          //  int w = (int)Math.floor((wl=eM.valD[currentVals1[nn]][0].length) > 1 ?(ww= eM.valD[currentVals1[nn]][0][1][0] ): (ww = eM.valD[currentVals1[nn]][0][0][1]));
          // enable staff slider if value > -1 the staff values exist as positive slider vals
          if (v > -1) {
            if (E.debugSettingsTab) {
              System.out.println(" <<<<<DISPLAY ship sliderV=" + v + ", clan=" + eM.gameClanStatus + ", klan=" + klan + ", line=" + nn + ", desc=" + eM.valS[currentVals1[nn]][0]);
            }
            // gamePSliders[nn].setForeground(Color.blue);
            gameSSliders[nn].setSnapToTicks(false);
            gameSSliders[nn].setForeground(Color.blue);
            gameSSliders[nn].setMajorTickSpacing(10);
            gameSSliders[nn].setMinorTickSpacing(2);
            gameSSliders[nn].setPaintLabels(true);
            gameSSliders[nn].setEnabled(true);
            gameSSliders[nn].setVisible(true);
            gameSSliders[nn].setValue(v);
            gameSSliders[nn].setMaximumSize(new java.awt.Dimension(350, 35));
            gameSSliders[nn].setMinimumSize(new java.awt.Dimension(150, 35));
            gameSSliders[nn].setPreferredSize(new java.awt.Dimension(250, 35));
            gameSSliders[nn].setValueIsAdjusting(true);
            //     panelAr[nn].setBackground(shipBackgroundColor);
            //    panelAr[nn].setForeground(shipInvColor);
          } else {
            gameSSliders[nn].setEnabled(false);
            gameSSliders[nn].setVisible(false);
            if (E.debugSettingsTab) {
              System.out.println(" <<<<< NO DISPLAY ship sliderv=" + v + ", clan=" + eM.gameClanStatus + ", gc=" + gc + ", klan=" + klan + ", desc=" + eM.valS[currentVals1[nn]][0]);
            }
          }
          nn++;
        } else {
          if (E.debugSettingsTab) {
            System.out.println(" >>>>SKIP line=" + nn + ", gc=" + gc + ", klan=" + klan + ", clan=" + eM.gameClanStatus + ", desc=" + eM.valS[currentVals1[nn]][0]);
          }
        }

        eM.vv++;
      }
      if (eM.vv >= eM.vvend) {
        int vv2 = eM.gameClanStatus == 5 ? eM.gStart[0] : eM.cStart[0];
        if (E.debugOutput) {
          System.out.println("start over from" + eM.vv + " panel clan=" + eM.gameClanStatus + " action=" + action + " start=" + (eM.vv = vv2) + " length=" + eM.vvend + " ");
        }
        System.out.println(new Date().toString());
        eM.gameDisplayNumber[eM.gameClanStatus] = eM.prevGameDisplayNumber[eM.gameClanStatus] = 0; // start over

      }
      // now disable the unused panels
      if (nn < 10) {
        while (nn < 10) {
          textFieldsAr[nn].setText("unused");
          panelAr[nn].setEnabled(false);
          panelAr[nn++].setVisible(false);
          System.out.print("disable panel nn=" + nn + " clan=" + eM.gameClanStatus);
          System.out.println(" " + new Date().toString());
        }
      }

      if (E.debugOutput) {
        System.out.print(
                "exit gamePanelChange clan=" + eM.gameClanStatus + " action=" + action);
        System.out.println(
                " " + new Date().toString());
      }
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      System.err.println(Econ.nowName + since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      eM.flushes();
      eM.flushes();
      eM.flushes();
      setFatalError();
    }
  } // gamePanelChange

  private void gamePanel0MouseExited2(java.awt.event.MouseEvent evt) {
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }

  private void gamePanel1MouseEntered2(java.awt.event.MouseEvent evt) {
    if (gamePanel1.isEnabled() && curVals1[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
      gamePanel1.setToolTipText(eM.getDetail(curVals[1]));
    };
  }

  void setGameButtonColors() {
    Color oldRedColor = E.clan.RED.getColor(1);
    Color oldRedInvColor = E.clan.RED.getInvColor(1);
    System.out.printf("set colors clanRed color=%s %n ", Integer.toHexString(oldRedColor.getRGB()));
    System.out.printf("set colors clanRed invcolor=%s %n ", Integer.toHexString(oldRedInvColor.getRGB()));
    clanRed.setForeground(E.clan.RED.getColor(1));
    clanOrange.setForeground(E.clan.ORANGE.getColor(1));
    clanYellow.setForeground(E.clan.YELLOW.getColor(1));
    clanGreen.setForeground(E.clan.GREEN.getColor(1));
    clanBlue.setForeground(E.clan.BLUE.getColor(1));
    clanRed.setForeground(E.clan.RED.getColor(1));
    gameMaster.setForeground(E.clan.GAMEMASTER.getColor(1));
  }

  public void gameSliderChange(int num, int val, javax.swing.JSlider[] sliderAr) {
    sliderAr[num].setMajorTickSpacing(250);
    sliderAr[num].setMaximum(1000);
    sliderAr[num].setMinorTickSpacing(50);
    sliderAr[num].setPaintLabels(true);
    sliderAr[num].setPaintTicks(true);
    sliderAr[num].setValue(val);
    //   gameXtraPanel1.setBackground(new java.awt.Color(153, 255, 255));
    //   gameMaster.setBackground(new java.awt.Color(204, 204, 204));

    sliderAr[num].addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        LogDlen1SliderStateChanged(evt);
      }
    });
  }

  /**
   * this string holds pointers to longer explanations for given results
   */
  long testlist = EM.LIST3;
  String resExt[] = new String[200];
  long lists[] = {EM.LIST0, EM.LIST1, EM.LIST2, EM.LIST3, EM.LIST4, EM.LIST5, EM.LIST6, EM.LIST7, EM.LIST8, EM.LIST9, EM.LIST10, EM.LIST11, EM.LIST12, EM.LIST13, EM.LIST14, EM.LIST15, EM.LIST16, EM.LIST17, EM.LIST18, EM.LIST19, EM.LIST20};

  /**
   * this set array of arrays match the 20 radio buttons on the stats tab The
   * 0'th array is invoked by the 0 radio button etc. Each array contains an
   * array of one or more numbers that are filters for the response doRes's in
   * class EM Each doRes has up to 4 filters that must match at least one
   * "listx" in a number the rest of the number specifies one or more outputs
   * when a listx matches The methods in Assets and associated classes invoke a
   * selection of EM.gameRes to store a value The display for each list consists
   * of 4 rounds (no ROWS,ROWS1,ROWS2,ROWS3)
   */
  long[] rowsm = {0L,
    EM.THISYEAR | EM.THISYEARAVE | EM.THISYEARUNITS | EM.SUM | EM.BOTH | EM.CUMAVE | EM.CUM | EM.CUR | EM.CURAVE | EM.CURUNITS | EM.CUMUNITS,
    EM.ROWS1 | EM.BOTH | EM.THISYEAR | EM.THISYEARAVE | EM.THISYEARUNITS | EM.CUM | EM.CUMAVE | EM.CURUNITS | EM.CUR | EM.CURAVE | EM.CUMUNITS,
    EM.ROWS2 | EM.BOTH | EM.CUR | EM.CURAVE | EM.CURUNITS | EM.THISYEAR | EM.THISYEARAVE | EM.THISYEARUNITS | EM.CUM | EM.CUMAVE | EM.CUMUNITS,
    EM.ROWS3 | EM.BOTH | EM.THISYEARUNITS | EM.CUR | EM.CURUNITS | EM.CURUNITS | EM.CUM | EM.CUMAVE | EM.CUMUNITS
  };
  long resLoops[][] = {
    {EM.LIST0 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST1 | EM.SKIPUNSET, EM.LIST1, 0L, 0L, 0L},
    {EM.LIST2 | EM.SKIPUNSET, EM.THISYEARAVE, 0L, 0L, 0L},
    {EM.LIST3 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST4 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST5 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST6 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST7 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST8 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST9 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST10 | EM.SKIPUNSET, EM.CURAVE, 0L, 0L, 0L},
    {EM.LIST11 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST12 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST13 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST14 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST15 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST16 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST17 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST18 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST19 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST20 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST21 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST22 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},
    {EM.LIST23 | EM.SKIPUNSET, 0L, 0L, 0L, 0L},};
  int m = 0, arow = 0;

  /**
   * list the results saved by setStats in Econs and Assets
   *
   * @param fullRes
   */
  void listRes(double[] fullRes) {
    listRes(0, resLoops, fullRes);
  }

  static int listResNoteCount = 0;

  void listRes(int list, long resLoops[][], double[] fullRes) {
    arow = 0;
    eM.getWinner(); // recalc winner, my be after settings changed
    eM.rememberFromPage = false; // forget remember at a new page
    // statsField2.setText("year" + eM.year);
    statsField.setText("year" + eM.year + " " + statsButtonsTips[list]);
    int lrows = statsTable1.getRowCount();
    int cntLoops = 0;
    int[] rowsCnts = {100, 0, 0, 0};
    int mm = 0; // count of selected rows
    int ii = 0;
    long i = 0l;
    long myLIST = resLoops[list][0];
    for (int rowsIx = 1; rowsIx < 5 && rowsIx < resLoops[list].length; rowsIx++) {
      i = myLIST | rowsm[rowsIx] | resLoops[list][rowsIx];

      if (E.debugPutRowsOut6) {
        if ((listResNoteCount++ < 10)) {
          System.out.printf("in StarTrader.listRes resLoops[%d][%d], key=%o, row%d\n", list, rowsIx, i, arow);
        }
      }
      arow = eM.putRows(statsTable1, resExt, arow, i);
    }

    // now blank the rest of the table
    System.out.println("listRes blank rest of table arow=" + arow);
    for (; arow < statsTable1.getRowCount() - 1; arow++) {
      statsTable1.setValueAt("----mt---", arow, 0);
      for (int mmm = 1; mmm < E.lclans * 2 + 1; mmm++) {
        statsTable1.setValueAt(":", arow, mmm);
      }
    }
    statsTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM2 = statsTable1.getSelectionModel();
    k = 0;
    //  System.out.println("displayLog lsm k=" + k + ", row=" + row + ", last=" + logLastM[k] + ", level=" + logLev[k] + ", logRowEnd=" + logRowEnd[k]);
    System.out.flush();
    rowSM2.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        NumberFormat whole = NumberFormat.getNumberInstance();
        whole.setMaximumFractionDigits(0);
        k = 0;
        if (e.getValueIsAdjusting()) {
          return;
        }

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty()) {
          //      System.out.println(since() + " No rows are selected.");
        } else {
          int selectedRow = lsm.getMinSelectionIndex();
          statsField.setText(stringTemp = resExt[selectedRow]);
          if (eM.rememberFromPage) {
            eM.doRememberValues(list, statsTable1, selectedRow, resExt[selectedRow]);
          }
          //    statsTable1.setToolTipText(stringTemp);
        } // 
      } // end valueChanged
    } // end ListSelectionListener
    ); // end addListSelectionListener
  }// for reslooops

  static Boolean resetOut = true;
  /* static RunYrs1 rYrs1 = new RunYrs1();
  
  Runnable tests1 = new Runnable(){
    public void run(){
      try {
      while(!(stateConst == CONSTRUCTED || stateConst == RUNSDONE)){ 
        Thread.sleep(1000);
      }
      rYrs1 = new RunYrs1();
      rYrs1.setPriority(3);
      rYrs1.start();  // start runYears2 the annimation thread
      //    stateConst = STATS;
      runBackgroundYears4(1);
       } catch (InterruptedException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(Econ.nowName + " " + Econ.nowThread + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;
    }
  }
  }; // tests1
  
  static Runnable tests2 = new Runnable(){
    public void run(){
      try {
        // wait for runYears1 to finish
      while(!(stateConst == CONSTRUCTED || stateConst == RUNSDONE)){ 
        Thread.sleep(1000);
      }
       } catch (InterruptedException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(Econ.nowName + " " + Econ.nowThread + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;
    }
  }
  }; // tests2
   */

  static boolean testing = false; // change to false in production
  // public static StarTrader st = new StarTrader();
  public static StarTrader st = (new StarTrader());

  /**
   * @param args the command line arguments
   */

  public static void main(String args[]) throws IOException {
    /* Set the Nimbus look and feel --change to animation*/

    // Look and feel setting code (optional)
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try {
      //   E.bRemember = Files.newBufferedWriter(E.REMEMBER, E.CHARSET);
      System.err.println("starting out in oldmain thread=" + Thread.currentThread().getName() + "msecs" + (new Date().getTime() - startTime));
      mainStart(args);
      st.setVisible(true);
      stateConst = CONSTRUCTED;

      if (testing || (args.length > 0 && args[0].contains("test"))) {
        main3();
      } else {
        System.err.println("continuing main thread=" + Thread.currentThread().getName() + "msecs" + (new Date().getTime() - startTime));
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
          // java.awt.EventQueue.invokeAndWait(new Runnable() {
          @Override
          public void run() {
            st.setVisible(true);
          }
        });// invokeLater
      } // end if test
      /* } catch (InterruptedException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(Econ.nowName + " " + Econ.nowThread + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;;
      /*
    } catch (InvocationTargetException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(Econ.nowName + " " + Econ.nowThread + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;;
       */
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(Econ.nowName + " " + Econ.nowThread + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;;
    } finally {
      System.err.println("doyear finally do flushes next then close bKeep");
      EM.flushes();
      if (EM.bKeep != null) {
        EM.bKeep.close();
      }
    }
  }

  /**
   * the starting part of main
   *
   * @throws IOException
   */
  public static void mainStart(String args[]) throws IOException {
    try {
      //   E.bRemember = Files.newBufferedWriter(E.REMEMBER, E.CHARSET);
      System.err.println("----MSa-----starting out in mainStart thread=" + Thread.currentThread().getName());
      //  String dateString = EM.MYDATEFORMAT.format(new Date());
      //  String rOut = "New Game " + dateString + "\n";
      // E.bRemember.write(rOut,0,rOut.length());
      // E.bKeep.write(rOut,0,rOut.length());
      PrintStream jout, jerr, jout1, jerr1;
      
      if (E.debugOutput || (args.length > 0 && args[0].contains("see"))) {
      //if (E.debugOutput) {
        jout = new PrintStream(new File("StarTraderOutput.txt"));
        //  jout1 = new PrintStream(new File("StarTraderOut1.txt"));
        //  jout1.println("jout1 line");
        jout.println("jout line0");
        System.out.println("----MSO0----System.out line 0");
        jerr = new PrintStream(new File("StarTraderErrors.txt"));
        //     jerr1 = new PrintStream(new File("StarTraderErr1.txt"));
        if (E.resetOut || (args.length > 0 && args[0].contains("see"))) {
      //  if (E.resetOut) {
          System.setOut(jout);
          System.out.println("-----MSOO----output to System.out after setOut");
          System.setErr(jerr);
          System.err.println("----MSOE----output to System.err after setErr");

        }
        System.out.println("-----MSOo1-----output after if statment");
        System.err.println("----MSOe1-----output to err after if statment");
      }

      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {

        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception | Error ex) {
      EM.firstStack = EM.secondStack + "";
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.newError = true;
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(Econ.nowName + " " + Econ.nowThread + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + EM.addMore());
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;;
      System.exit(-17);
    }
  }// mainStart

  static int myYears=2;
  static volatile int cntr1 = 0, cntra=0,cntrb=0,cntrc=0;
  static volatile long ttime = 0,bbtime=0,rtime=0;
  static volatile int cntr2 = 0;
  static volatile int cntr3 = 0;
  static volatile int cntr3a = 0;
  static volatile int cntr4 = 0;
  static volatile int cntr5 = 0;
  static volatile int cntr6 = 0;
  static volatile int cntr7 = 0;
  static volatile int cntr8 = 0;
  static volatile int cntr9 = 0;
  static volatile int cntr10 = 0;

  /**
   * the testing part of the main routine
   *
   */
  public static void main3() {
    try {
      System.err.println("------TA0-----enter main3 thread=" + Thread.currentThread().getName() + ", " + (new Date().getTime() - startTime) + EM.mem());
      testing = true; // flag in testing mode
      st.setVisible(true);
     bbtime = new Date().getTime();
      Runnable tests1 = new Runnable() {
        public void run() {  //tests1.run
          try {
            st.setVisible(true);
            cntr1 = 0;
            stateConst = STARTING;
            ttime = new Date().getTime();
            st.runYears(myYears); // higher random
            if (!eM.dfe()) {
              System.err.println("----TA------run main3 start testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", msecs" + (new Date().getTime() - startTime) + ", cntr1=" + cntr1 + EM.mem());
              while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
                System.out.println("----TT----tests1 round1 waiting out testing thread=" + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() + ", cntr1=" + cntr1++);
                if(cntr1 == 9)EM.printAddMore();
                assert cntr1 < 11 : "stuck round1 " + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() + "  at cntr1=" + cntr1;
                if (E.noAsserts && cntr1 > 10) {
                  eM.doMyErr("stuck at cntr1 > 10");
                }
                Thread.sleep(500);  // one second
              } // while
              eM.randFrac[0][0] = .7; // increase game random
              eM.randFrac[1][0] = .7;
              EM.difficultyPercent[0] = 80;
              stateConst = STARTING;
              System.out.println("-----TA2-----Countinue main3 test1 round2 doing testingthread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() );
              stateConst = RUNNING;

              st.runYears(2); // higher random
              // wait for runYears to finish
              cntr1 = 0;
              while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
                System.out.println("----TA3----main3 test1 round3 waiting testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() + ", cnt1=" + cntr1++);
                assert cntr1 < 81 : " stuck at wait round3 cntr1=" + cntr1;
                if (E.noAsserts && cntr1 > 40) {
                  eM.doMyErr("stuck at cntr > 40");
                }
                Thread.sleep(1000);
              }
              if (!eM.dfe()) {
// after successful first st.runYears(2)
                double[] rr = {.2, .05};  // reduce costs res, staff
                eM.mab1 = eM.mac1 = rr;
                double[] rrr = {5., .7};
                eM.resourceGrowth = eM.staffGrowth = rrr;
                //stateConst = STARTING;
                System.err.println("----R4----Countinue main3 round4 doing testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem());
                stateConst = RUNNING;

                st.runYears(2); // higher random
                // wait for runYears to finish
                cntr1 = 0;
                while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
                  System.err.println("#######main3 round5 waiting testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem()  + ", cntr1=" + cntr1++);
                  assert cntr1 < 81 : " stuck waiting in round5 " + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() + " cntr1=" + cntr1;
                  if (E.noAsserts && cntr1 > 80) {
                    eM.doMyErr(" stuck waiting in round5 " + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime) + EM.mem() + " cntr1=" + cntr1);
                  }
                  Thread.sleep(1000);  //1 sec
                }
                if (!eM.dfe() && !st.fatalError) {
                  //  eM.difficultyPercent[0] = 15.;
                  //stateConst = STARTING;
                  System.err.println("----R6----Countinue main3 round6 test1 doing testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + " stuck waiting in round5 " + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() + " cntr1=" + cntr1);
                  stateConst = RUNNING;
                  st.runYears(2); // much lower difficulty
                  // wait for runYears to finish
                  cntr1 = 0;
                  System.err.println("#######main3 round7 before testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime) + ", cntr1=" + cntr1);
                  while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
                    System.err.println("#######main3 round7 waiting testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime) + ", cntr1=" + cntr1++);
                    assert cntr1 < 151 : "stuck at round7 cntr1=" + cntr1;
                    Thread.sleep(1000);
                  }
                  System.err.println("#######main3 round7 after testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime) + ", cntr1=" + cntr1);
                  if (!eM.dfe()) {
                    eM.randFrac[0][0] = .7;
                    eM.randFrac[1][0] = .7;
                    //stateConst = STARTING;
                    System.err.println("----R8----Countinue main3 round8 doing testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + "msecs" + (new Date().getTime() - startTime));
                    stateConst = RUNNING;

                    //st.runYears(20); // higher random
                    st.runYears(2); // higher random
                    // wait for runYears to finish
                    cntr1 = 0;

                    while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
                      System.err.println("#######main3 round9 waiting testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime) + ", cntr1=" + cntr1++);
                      assert cntr1 < 161 : "stuck at round9 cntr1=" + cntr1;

                      Thread.sleep(1000);
                    }
                  }
                }
              }
            }
            /*
           st.runBackgroundYears4(1);
        // wait for runYears to finish
      //  int cntr2 = 0;
        while (!(stateConst == CONSTRUCTED || stateConst == RUNSDONE)) {
          System.err.println("tests1 running out testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", msecs" + (new Date().getTime() - startTime) + ", cnt2=" + cntr2++);
          Thread.sleep(1000);
        }
        stateConst = RUNNING;
             */

          } catch (WasFatalError ex) {
            ex.printStackTrace(EM.pw);
            EM.thirdStack = EM.sw.toString();
            eM.flushes();
            System.err.println("Main3 test Error " + ex.toString() + " " + EM.curEconName + " " + Thread.currentThread().getName() + EM.andMore());
            //ex.printStackTrace(System.err);
            System.exit(-12);
            // go to finally
          } catch (Exception | Error ex) {
            EM.firstStack = EM.secondStack + "";
            ex.printStackTrace(EM.pw);
            EM.secondStack = EM.sw.toString();
            EM.newError = true;
            System.err.println(EM.tError = ("Main3 test1 Error " + ex.toString() + " " + EM.curEconName + " " + Thread.currentThread().getName() + ", cause=" + ex.getCause() + ",  message=" + ex.getMessage() + " " + EM.andMore()));
            // ex.printStackTrace(System.err);
            eM.flushes();
            eM.flushes();
            st.setFatalError();
            throw new WasFatalError(EM.tError);
          }
        } //tests1.run
      }; // end tests1
      
             eM.randFrac[0][0] = .7; // increase game random
              eM.randFrac[1][0] = .7;
              EM.difficultyPercent[0] = 80;
              stateConst = STARTING;
              rtime = new Date().getTime();
              System.out.println("-----TAa-----Countinue main3 test1 round2 doing testingthread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", times r" + EM.since(rtime) + ":t" + EM.since(ttime) + ":s" + EM.since(startTime)  + EM.mem() );
         //    stateConst = RUNNING;
              st.runYears(myYears); // higher random
              // wait for runYears to finish
              cntra = 0;
              while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
                System.out.println("----TA3----main3 test1 rounda waiting testing thread=" + Thread.currentThread().getName() + ", " + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() + ", cntra=" + cntra++);
                assert cntr1 < 91 : " stuck at wait rounda "  + Thread.currentThread().getName() + ", " + stateStringNames[stateConst] + ", times" + EM.since(bbtime) + ":" + EM.since(ttime) + ":" + EM.since(startTime)  + EM.mem() + ", cntra=" + cntra;
                EM.wasHere8 = "--rnda lcnt=" + (90-cntra) + " rtime" + EM.since(rtime) + EM.mem();
                if (E.noAsserts && cntr1 > 40) {
                  eM.doMyErr("stuck at cntr > 40");
                }
                Thread.sleep(4000);
              }
      eM.difficultyPercent[0] = 80.;
      EM.prioritySetMult[0][0] = 1.0;
      EM.prioritySetMult[1][0] = 1.0;
      EM.clanStartFutureFundDues[0][0] = 1000.;
      EM.clanStartFutureFundDues[0][1] = 1000.;
      EM.clanStartFutureFundDues[1][0] = 1000.;
      EM.clanStartFutureFundDues[1][2] = 1000.;
      EM.clanStartFutureFundDues[1][1] = 1000.;
      st.runYears(2); // higher difficult

   //   SwingUtilities.invokeAndWait(tests1);
   //   SwingUtilities.invokeLater(tests1);
      cntr1 = 0;
      rtime = (new Date().getTime());
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("testing waiting out round1 thread=" + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", times " + EM.since(bbtime) + ":" + EM.since(rtime) + ", cntr1=" + ++cntr1);
        
        assert cntr1 < 101 : " stuck waiting after round1 " + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", times " + EM.since(bbtime) + ":" + EM.since(ttime) + " cntr1=" + cntr1;
EM.wasHere8 = "--rnd1 lcnt=" + (100-cntr1) + " rtime" + EM.since(rtime) + EM.mem();
Thread.sleep(4000);
      }
    //  eM.maxThreads[0][0] = 7.;
      eM.difficultyPercent[0] = 60.;
      EM.prioritySetMult[0][0] = 2.9;
      EM.prioritySetMult[1][0] = 2.9;
      cntr2 = 0;
      rtime = (new Date().getTime());
      st.runYears(2);
      
       while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("tests1 waiting out round2 thread=" + Thread.currentThread().getName() + ", " + stateStringNames[stateConst] + EM.since("rtime",rtime) + ", cntr2=" + ++cntr2);
       EM.wasHere8 = "--rnd3 lcnt=" + (100-cntr2) + " rtime" + EM.since(rtime) + EM.mem(); 
       assert cntr2 < 101 : " stuck waiting after round3 " + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", times " + EM.since("rtime",rtime) + ":" + EM.since("startTime",startTime) + " cntr2=" + cntr2;
       Thread.sleep(4000);
      }
      eM.difficultyPercent[0] = 55.;
      EM.prioritySetMult[0][0] = 2.5;
      EM.prioritySetMult[1][0] = 2.5;
      cntr3 = 0;
      st.runYears(2);
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("tests1 waiting out round3 thread=" + Thread.currentThread().getName() + ", " + stateStringNames[stateConst] + EM.since("rtime",rtime) + ", cntr3=" + ++cntr3);
       EM.wasHere8 = "--rnd3 lcnt=" + (100-cntr3) + " rtime" + EM.since(rtime) + EM.mem(); 
       assert cntr3 < 101 : " stuck waiting after round3 " + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", times " + EM.since("rtime",rtime) + ":" + EM.since("startTime",startTime) + " cntr3=" + cntr3;
Thread.sleep(4000);
      }
      EM.prioritySetMult[0][0] = 2.3;
      EM.prioritySetMult[1][0] = 2.3;
      eM.difficultyPercent[0] = 60.;
      EM.clanStartFutureFundDues[0][0] = 700.;
      EM.clanStartFutureFundDues[0][1] = 700.;
      EM.clanStartFutureFundDues[1][0] = 700.;
      EM.clanStartFutureFundDues[1][2] = 700.;
      EM.clanStartFutureFundDues[1][1] = 700.;
      st.runYears(2); // higher difficult
      // SwingUtilities.invokeLater(tests1);
      cntr4 = 0;
      ttime = (new Date().getTime());
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("tests1 waiting out round4 thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + EM.since(ttime) + ", cntr4=" + cntr4++);
       EM.wasHere8 = "--rnd4 lcnt=" + (100-cntr4) + " rtime" + EM.since(rtime) + EM.mem(); 
        assert cntr4 < 101 : " stuck waiting after round4 " + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", times " + EM.since("rtime",rtime) + ":" + EM.since() + " cntr4=" + cntr3;
         Thread.sleep(4000);
      }
     // eM.maxThreads[0][0] = 7.;
      EM.prioritySetMult[0][0] = 2.8;
      EM.prioritySetMult[1][0] = 2.8;
      EM.vdifMult = 0.085;
      cntr5 = 0;
      rtime = (new Date().getTime());
      st.runYears(2);
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("----M5---waiting out round5 thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + EM.since(ttime) + ", cntr5=" + ++cntr5);
        EM.wasHere8 = "--rnd5 lcnt=" + (150-cntr5) + " rtime" + EM.since(rtime) + EM.mem(); 
        assert cntr5 < 151 : " stuck waiting after round5 cntr5=" + cntr5;
        Thread.sleep(4000);
      }
      eM.clanShipFrac[0][0] = .66; // 2/1 from .501
      eM.clanAllShipFrac[0][0] = .66;
      eM.clanShipFrac[0][3] = .66; // 2/1 from .501
      eM.clanAllShipFrac[0][3] = .66;
      EM.vdifMult = 0.09;
      cntr6 = 0;
      rtime = (new Date().getTime());
      st.runYears(2);
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("----M6----waiting out round6 testing thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + EM.since(ttime) + ", cntr6=" + ++cntr6);
        EM.wasHere8 = "--rnd6 lcnt=" + (200-cntr6) + " rtime" + EM.since(rtime) + EM.mem();
        assert cntr6 < 201 : " stuck waiting after round6 cntr6 > 201" + " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5+ " cntr6=" + cntr6 + EM.mem();
        Thread.sleep(4000);
      }
      //  double mab1[] = {.60, .60}; // resource costs planet,ship
      // double mac1[] = {.60, .60}; // staff costs planet ship 
      eM.mab1[0] = 2.;
      eM.mab1[1] = 2.;
      eM.mac1[0] = 2.;
      eM.mac1[1] = 2.;
      st.runYears(2);
      cntr7 = 0;
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("----M7----waiting out testing round7 =" + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime) + ", cntr7=" + ++cntr7);
        EM.wasHere8 = "--rnd7 lcnt=" + (200-cntr7) + " rtime" + EM.since(rtime) + EM.mem();
        assert cntr7 < 201 : " stuck waiting after round7 cntr7=" + cntr7  + EM.mem() + " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime) + ", cntr7=" + cntr7;
                Thread.sleep(4000);
      }

      eM.resourceGrowth[0] = 2.;
      eM.resourceGrowth[1] = .5;
      eM.staffGrowth[0] = 2.;
      eM.staffGrowth[1] = .5;
      st.runYears(2);
      cntr8 = 0;
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("----M8----waiting out testing round8 thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + ", msecs" + (new Date().getTime() - startTime) + ", cntr8=" + ++cntr8 + EM.mem()  + " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + " cntr7=" + cntr7);
        EM.wasHere8 = "--rnd8 lcnt=" + (210-cntr8) + " rtime" + EM.since(rtime) + EM.mem();
        assert cntr8 < 211 : " stuck waiting after round119 cntr8=" + cntr8 + EM.mem() + " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + " cntr7=" + cntr7 + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime);
      Thread.sleep(4000);
      }

      eM.randFrac[0][0] = .9;
      eM.randFrac[1][0] = .5;
      eM.staffGrowth[0] = 2.;
      eM.staffGrowth[1] = .5;
      EM.vdifMult = 0.065;
      st.runYears(2);
      cntr9 = 0; //reset cntr1 again
      // wait for runYears to finish
      while ((stateConst >= CONSTRUCTING && stateConst <= ENDYR && !EM.dfe())) {
        System.err.println("----M9----waiting out testing round9 thread=" + Thread.currentThread().getName() + ", stateConst=" + stateStringNames[stateConst] + EM.mem() + ", msecs" + (new Date().getTime() - startTime) + ", cntr9=" + ++cntr9);
        EM.wasHere8 = "--rnd9 lcnt=" + (250-cntr9) + " rtime" + EM.since(rtime) + EM.mem();
        assert cntr9 < 251 : " stuck waiting after round9 cntr9=" + cntr9 + " cntr1=" + cntr1 + " cntr2=" + cntr2 + " cntr3=" + cntr3 + " cntr4=" + cntr4 + " cntr5=" + cntr5 + " cntr6=" + cntr6 + " cntr7=" + cntr7 + " cntr8=" + cntr8 + " " + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.mem() + EM.since(startTime); //(new Date().getTime() - startTime);
        Thread.sleep(4000);
      }
      System.err.println("tests1 after testing round9 exit ok thread=" + Thread.currentThread().getName() + " " + stateStringNames[stateConst] + EM.since() + EM.since("rtime",rtime) 
              + ", cntr9=" + cntr9 + EM.mem() + " cntr1=" + cntr1 + " cntr2=" + cntr2 
              + " cntr3=" + cntr3+ " cntr4=" + cntr4
+ " cntr5=" + cntr5 + " cntr6="
              + " cntr7=" + cntr7 + " cntr8=" + cntr8);
      System.exit(0);  // success
  
    } catch (WasFatalError ex) {
      ex.printStackTrace(EM.pw);
      EM.thirdStack = EM.sw.toString();
      eM.flushes();
      System.err.println("Main3 test2 Error " + ex.toString() + " " + EM.curEconName + EM.mem() + " " + Thread.currentThread().getName() + EM.andMore());
      //ex.printStackTrace(System.err);
      System.exit(-17);
      // go to finally
    } catch (Exception | Error ex) {
      ex.printStackTrace(EM.pw);
      EM.secondStack = EM.sw.toString();
      EM.firstStack = EM.secondStack + "";
      EM.newError = true;
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      System.err.println(EM.tError = ("Main3 Error " + ex.toString() + " " + EM.curEconName + EM.mem() + " " + Thread.currentThread().getName() + ", cause=" + ex.getCause() + ",  message=" + ex.getMessage() + " " + EM.andMore()));
      // ex.printStackTrace(System.err);
      eM.flushes();
      st.setFatalError();

    }

  }// main3

}
