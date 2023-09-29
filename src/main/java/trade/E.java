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
package trade;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This is an extension of the StarTrader main class. It contains data for
 * settings These settings values have no user interface to change them. E
 * contains constants and a few methods see EM for values that StarTrader can
 * change during the game
 *
 * @author Albert Steiner
 */
public class E {
// this class hold all of the constants that are used for rows and columns in req
  // it also holds tables used to calculate growth etc.
  // StarTrader working set of statsButto?nTip

  static final public String statsButton0Tip = "0: Cum Game Worths,";
  static final public String statsButton1Tip = "1: cum Favors and trade effects";
  static final public String statsButton2Tip = "2: catastrophes, deaths, randoms, forwardfund";
  static final public String statsButton3Tip = "3: deaths. trades acc, gave, got";
  static final public String statsButton4Tip = "4: deaths, Rej misd Trades, gave, got, growths swaps";
  static final public String statsButton5Tip = "5: trades accepted, gave, got, growth swaps avail  ";
  static final public String statsButton6Tip = "6: trades rejected, lost, missed growth, swaps , avail";
  static final public String statsButton7Tip = "7: Resource, staff, forwardFunds knowledge values";
  static final public String statsButton8Tip = "8: creates. growth,  swaps and costs details";
  static final public String statsButton9Tip = "9: Catastrophes, Fertility, health and effects";
  static final public String statsButton10Tip = "10: list by ages deaths with trades missed, rejected, lost";
  static final public String statsButton11Tip = "11: list by ages deaths with trades accepted ";
  static final public String statsButton12Tip = "12: list by ages  missed, rejected, lost, growth,avail";
  static final public String statsButton13Tip = "13: list by ages accepted growth avail";
  static final public String statsButton14Tip = "14: list by ages deaths catastrophies, forwardFunds ";
  static final public String statsButton15Tip = "15: list by ages live catastrophies, forwardFunds";
  static final public String statsButton16Tip = "16: list by ages worths, work,faculty,research interns";
  static final public String statsButton17Tip = "17: list by ages growths, helps, creations ";
  static final public String statsButton18Tip = "18: Swaps years xfer skips, redos and dos";
  static final public String statsButton19Tip = "19: Swaps years Forward Fund imbalance or save";
  static final public String statsButton20Tip = "20: rcsg";
  static final public String statsButton21Tip = "21: TB assigned";
  static final public String statsButton22Tip = "22: TB assigned";
  static final public String statsButton23Tip = "23: display table";

  static EM eM;
  static StarTrader st;
  /**
   * ***********************************************************************
   * START DATA
   */

  /**
   * start debug flags, it is possible if static final boolean is false the code
   * enclosed by an if on this flag will never be compiled. In any case
   * execution speeds up if the debugging code is not reached
   */
  //static final boolean noAsserts = true; // true expect not -ea in program call
  static final boolean noAsserts = false; // false expect -ea in call 
  static boolean ifassert = false; // preset for later test

  static final boolean distributable = false;  //set true before making a jar fine available to public
  static final boolean debugMaster = !distributable;// !distributable;
  static final boolean debugfalse = false;
  static final boolean debugOutput = true; //distributable;
  static final boolean outputLess = true;  // reduce the output chars in mf2
  static final boolean debugStuck = true; //error if stuck
  static final boolean debugListNewE = false && debugMaster; //list known econs
  //static final boolean debugOutput = true;
  // resetOut out = StarTraderOutput,err = StarTraderErrors
  //static final boolean resetOut = debugOutput;  //change out, err to
  static final boolean resetOut = false || distributable;  //change out, err to local files
  static final boolean debugCreateOut = debugMaster; //output messages Assets
  static final boolean debugCreateNullOut = debugMaster; //did not choose died Econ
  static final boolean debugAssetsOut = debugMaster; //output messages Assets
  static final boolean debugEconOut = debugMaster; //output messages in Econ
  static final boolean debugCashFlowOut = debugMaster; //output messages in CashFlow
  static final boolean debugTradesOut = debugMaster; //output messages in Trades
  static final boolean debugCheckBalances = debugMaster &&  false; //check balances in loops
  static final boolean debugEconCnt = debugMaster &&  false; // econCnt = porsCnt0 + porsCnt1
  static final boolean debugChangeEconCnt = debugMaster; // do  changes of econCnt
  static final boolean debugNegGrowth = debugMaster; // neg Growth made negCosts
  static final boolean debugEfficiency = debugMaster; // efficiency has double trouble
  static final boolean debugNegCosts = debugMaster; // checking for neg Costs
  static final boolean debugFutureFund = debugMaster; // checking for errors with future funds
  static final boolean debugNoTerm = debugMaster; // term undefined in assets, find whyu
  static final boolean debugTNoLastGoods = debugMaster; //error open TradeRecord
  static final boolean debugOfferCargos = debugMaster; //cargos in offer == cargo.balance
  static final boolean debugSumGrades = debugMaster; //sum of grades = sum of staff/guests
  static final boolean debugResum = debugMaster &&  false; //rc == r + c  sg == s + g
  static final boolean debugResumP = debugMaster &&  false; //rc == r + c  sg == s + g add || set
  static final boolean debugCosts = debugMaster;  // check that cost processing ok
  static final boolean debugDouble = debugMaster; //doubleTrouble and infinity or NaN
  static final boolean debugTradeRecord = debugMaster; // or false
  static final boolean debugTradeBarter = debugMaster; // in barter process
  static final boolean debugTradeSetup = debugMaster; // distance, location etc
  static final boolean debugSwaps = debugMaster; // doloops other swap tests
  static final boolean debugDidEconYearEnd = debugMaster; // StarTrader.doYear() doYearEdn
  static final boolean debugAssetsStats = debugMaster &&  false; // why stats aren't showing
  static final boolean debugStats = debugMaster; // why stats aren't showing
  static final boolean debugStatsOut = debugMaster; // stats output
  static final boolean debugScannerOut = debugMaster; // scanner output
  static final boolean debugMisc = debugMaster; // other debug tests
  static final boolean debugCanTrade = debugMaster; // planet can trade
  static final boolean debugDisplayTrade = debugMaster; // Trade System.out msgs
  static final boolean tradeDebugTerms = debugMaster;
  static final boolean debugSettingsTab = debugMaster; //errors from settings doValx
  static final boolean debugSettingsTabOut = debugMaster; //errors from settings doValx
  static final boolean debugSettingsTab2 = debugMaster; //errors from settings doValx
  static boolean debugDoRes = debugMaster; //errors from settings doValx
  static final boolean SWAPTRADESYSTEMOUT = debugMaster;  //Swap outputs
  static final boolean PAINTDISPLAYOUT = debugMaster; //outputs from StarTrader displays
  static final boolean DEBUGCALCGROWTH = debugMaster;
  static final boolean tradeInitOut = debugMaster;
  static final boolean DEBUGASSETSOUT = debugMaster;
  static final boolean debugPutValue = debugMaster; //test putValue processing
  static final boolean debugPutValue1 = outputLess; //test putValue processing
  static final boolean debugPutValue2 = outputLess; //test putValue processing
  static final boolean debugPutValue3 = debugMaster &&  false; //choose alternative for too big
  static final boolean debugPutRowsOut6 = debugMaster &&  false;
  static final boolean debugPutRows6aOut = debugMaster &&  false;
  static final boolean debugPutRows6abOut = debugMaster &&  false;
  static final boolean debugPutRows6acOut = debugMaster &&  false;
  static final boolean debugPutRows6agOut = debugMaster;
  static final boolean debugIsHere1Out = debugMaster;
  static final boolean debugIsHere2Out = debugMaster ;
  static final boolean debugIsHere3Out = debugMaster ;
  static final boolean debugPutRows2 = debugMaster && false;
  static final boolean DEBUGWAITTRACE = debugMaster &&  false;
  static final boolean debugLogsOut = debugMaster; // StarTrader logs output
  static final boolean debugThreads = debugMaster &&  false;
  static final boolean debugRsOut = debugMaster &&  false; // EM rs output
  static final boolean debugFFOut = debugMaster &&  false; // EM Assets.CashFlow.calcForwardFund output
  static final boolean debugStatsOut1 = debugMaster &&  false; // stats output1
  static final boolean debugStatsOut2 = debugMaster &&  false; // stats output1
  static final boolean debugYcalcCosts = debugMaster;
  static final boolean debugPriorityOut = debugMaster;
  static final boolean debugPutRowsOut = debugMaster;//test putValue processing
  static final boolean debugPutRowsOutUnset = debugMaster &&  false; //put out warnings of unset stats

  static final boolean debugDoStartYear = debugMaster &&  false; // output lines
  static final int ssMax = 10; // max setStats printed;
  static final boolean debugThreadsOut = debugMaster; // threads output
  static final boolean debugThreadsOut1 = debugMaster; // threads output1
  static final boolean errEconInit = debugMaster;
  static final boolean debugDoYearEndOut = debugMaster || debugFFOut; //output messages re yearEnd
  static final boolean debugAtJavaOut = debugMaster && false; // output at java locations
  static boolean doCalcCatastrophy = true; //temp disable

  /**
   * constructor for E the major repository of tables and values set by the game
   * master and the clan master. The game may have a number of instances of E
   * and EM
   */
  public E() {
    assert ifassert = true;
    // ok if ifassert true, noAsserts false, error if ifassert false noAsserts false,
    // ok if ifassert false and noAsserts true,error if ifassert true and noAsserts true;
    // allow no -ea with debug
    //   if(ifassert == noAsserts)EM.doMyErr("improper value for E.noAsserts" + (ifassert?" yes ifassert ":"no ifassert") + (noAsserts?" yes noAsserts ":" no noAsserts "));
  }

  ;

  void init(EM em, StarTrader st) {
    this.eM = em;
    this.st = st;
    for (int m = 0; m < lsecs; m++) {
      alsecs[m] = m;
      zlsecs[m] = lsecs - m;
      a2lsecs[m] = m;
      a2lsecs[m + lsecs] = m + lsecs;
      z2lsecs[m] = l2secs - 1 - m;
      z2lsecs[m + lsecs] = lsecs - 1 - m;
    }
  }
  /* Start Global data, set in tab init
   * [pors]
   */
  static int logPorSStatus = 3;  //??
  static int logClanStatus = 9;
  static int statsPorSStatus = 3;
  static int statsClanStatus = 9;
  static int initPorSStatus = 3;
  static int initClanStatus = 9;
  static final int LOGTEAMSTATUS = logClanStatus;
  static final int STATSTEAMSTATUS = statsClanStatus;
  static final int logTeamStatus = logClanStatus;
  static final int statsTeamStatus = statsClanStatus;
  static final int initTeamStatus = initClanStatus;
  static final int INITTEAMSTATUS = initClanStatus;

  public static String savedgameTextField;
  public static String savedgameTextField2;
  public static String savedgameTextField3;
  public static String savedgameTextField4;

  static NumberFormat dFrac = NumberFormat.getNumberInstance();
  static NumberFormat whole = NumberFormat.getNumberInstance();
  static NumberFormat exp = new DecimalFormat("0.######E0");

  // there are 3 value sets oringinal, user, envirn averages(function ave and years)
  // There are two contexts
  static protected enum ContextNames {

    PLANET, SHIP
  }

  static final protected String[] contextName = {"planet", "ship"};
  static final protected String[] contextNameAlpha = {"P", "S"};
  static final protected String[] cna = {"P", "S"};
  public static final int planet = 0;
  public static final int P = 0;
  public static final int ship = 1;
  public static final int S = 1;
  public static final int les = 2;
  public static final double PZERO = .000001; //for a >5  pzero
  public static final double PZERO1 = .0000000001;
  public static final double NZERO = -.000001; // for a < nzero
  public static final double PPZERO = 0.00000000000001; //for a > 13 zeros
  public static final double NNZERO = -PPZERO;  // for a < 13 zeros
  static double pzero = PZERO, nzero = NZERO;
  public static final double UNZERO = .00000000000000001;//17 zereo
  public static final double INVZERO = 1. / UNZERO;

  /**
   * test whether is number is not zero
   *
   * @param n the number for a test
   * @return true if number is a number and is not very close to 0.0
   */
  public static boolean nz(Double n) {
    return !(n.isNaN() || (n > NNZERO && n < PPZERO));
  }
  public static final int[] d2 = {0, 1}, A01 = d2;
  public static final int[] d4 = {0, 1, 2, 3}, A03 = d4;
  public static final int[] d6 = {0, 1, 2, 3, 4, 5}, A05 = d6;
  public static final int[] d8 = {0, 1, 2, 3, 4, 5, 6, 7}, A07 = d8;
  static final int lsums = 2;// rc sg
  static final int LSUMS = lsums;
  static final int BALANCESIX = lsums;
  static final int LSUBS = 4; // r c s g
  static final int lsubs = LSUBS;
  static final int ASUBS[] = {0, 1, 2, 3};

  public enum sectors {
    LIFE, STRUCT, ENERGY, PROPEL, DEFENSE, GOV, COLONIST
  };
  public final static int lsecs = sectors.COLONIST.ordinal() + 1;
  public final static int LSECS = lsecs;
  public final static int L2SECS = LSECS + LSECS;
  public final static double INVLSECS = 1 / LSECS;
  public final static double INVL2SECS = 1 / L2SECS;
  public final static int[] ASECS = {0, 1, 2, 3, 4, 5, 6};
  public final static int[] A2SECS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
  public static int pors = 0;
  public static int iClanPors = 0;  // flag for the clan tab
  public static int iClanClan = 0;  // clan flag for the clan tab
  public static int iGamePors = 0;  // flag for the game tab
  public static int doNotYears = 10;

  /**
   * button1 value, which logEnvirn set by select from
   * list,0=dlen1,dlev1,1=dlen2,dlev2
   */
  static public int dN = 0; // used when StarTrader.logEnvirnNamesList
  /**
   * working value of log starts at dN = 0,1
   */
  static protected int[] logM = {0, 0};
  //static protected int[] logDirectM = {0,0};
  /**
   * size of the input history ArrayTable hists[]
   */
  static public int[] logSizeHis = {0, 0};
  /**
   * working value of logLengths (rows), 2=sum of 0,1
   */
  static protected int[] logLen = {25, 25, 50};
  /**
   * this is the working levels, calculated by displayHistory
   */
  static public int[] logLev = {3, 3};
  /**
   * default to set into each envirn at the start of a run
   */
  static protected int[] logDefaultLev = {3, 3}; // display levels 1-3,20
  static public int[] logDefaultLen = {25, 25};

  // variable planetOrShip
  // there are 7 resources and 5 groups/clans
  static final public long startTime = (new Date()).getTime();

  static double[] newPlanetPosition = {-2.7, -2.7, -2.7};
  static final String PSD = "PSD";  // Planet, Ship Dead
  static final String ROYGB = "ROYGB"; // clan characters

  //location parameters for planets and ships.
  // use only the positive section of sphere x, y,z
  public enum clan {
    RED("rodalians", 0xa92e22, 0xc61331, 0xFFCFAD), ORANGE("irgabtucs", 0xd9850e, 0xe87722, 0xFFCFAD), YELLOW("yankels", 0xffdc23, 0xe8c155, 0xFDFDC3), GREEN("groknes", 0xb0b332, 0x9ff365, 0xD7F300), BLUE("brogles", 0x9eced7, 0x84b0d8, 0x00849A), GAMEMASTER("gameMaster", 0xbbbbbb, 0x666666, 0xFFD55F), COMPARE("Compare Clans", 0x888888, 0x888888, 0xFFD55F);
    private final String name;
    private final Color colorPlanet;
    private final Color colorShip;
    private final Color colorBackGround;
    private final Color invPlanet;
    private final Color invShip;

    clan(String name, int planet, int ship, int backGround) {
      this.name = name;
      this.colorPlanet = new Color(planet);
      this.colorShip = new Color(ship);
      this.colorBackGround = new Color(backGround);
      invShip = new Color(-ship);
      invPlanet = new Color(-planet);
    }

    public Color getColor(int ipors) {
      if (ipors == E.P) {
        return colorPlanet;
      }
      else {
        return colorShip;
      }
    }

    public Color getPlanetColor() {
      return colorPlanet;
    }

    public Color getBackgroundColor() {
      return colorBackGround;
    }

    public Color getShipColor() {
      return colorShip;
    }

    public Color getInvColor(int ipors) {
      if (ipors == E.P) {
        return invPlanet;
      }
      else {
        return invShip;
      }
    }
  }
  static protected String[] groupNames = {"red", "orange", "yellow", "green", "blue"};
 // public static final String[] clanNames = {"rodalians", "organtics", "yankels", "groknes", "brogles"};
  public static final String[] clanNames = {"red","orange", "yellow","green","blue"};
  static final String[] clanLetter = {"r", "o", "y", "g", "b"};
  public static final int lclans = clanNames.length;
  public static final int LCLANS = lclans;
  Color ccc = new Color(255, 204, 204);
  public static int backGroundColors[] = {0xFF6666, 0xd9850e, 0xffdc23, 0xb0b332, 0x9eced7};
  public Enum clans[] = {clan.RED, clan.ORANGE, clan.YELLOW, clan.GREEN, clan.BLUE};

  /**
   * foreground colors for planet,ship clan 0-4, 5 = all clans
   */
  //static protected int[][] clanColors = {{0xa92e22, 0xbf6204, 0xffdc23, , 0x2f5cb4,0xffffff}, {0xa92e22, 0xbf6204, 0xffdc23, 0xb0b332, 0x2f5cb4,0xffffff}};
  static protected ArrayList<String> planetsDisplay;
  static protected ArrayList<String> starsDisplay;
  static protected ArrayList<Econ> stars;
  static protected ArrayList<Econ> planets;
  static protected ArrayList<Econ> clanEnvirns;

  /**
   * the number of grade rows for staff type SubAssets
   */
  public static final int lgrades = 16;
  public static final int LGRADES = lgrades;
  static protected double[] cStartNewEnv = {2000., 2000., 2000., 2000., 2000.};
  static int hcnt = 9; // count of history values for assetsYr layaway ...
  /**
   * layaway is the cash available to buy new ships or planets resources and
   * staff are converted to cash at their nominal value layaway may be used in
   * swaps to reduce high value sectors and reduce costs so that health and
   * fertility can be increased
   */
  /**
   * values for processing clanPanel input
   */
  static int clanPanelClan = 0;
  static int clanPanelPorS = 0;
  static int clanPanelRow = 1;
  static double clanPanelValue = .5;

  static protected int printlnLimit = 3;
  static boolean alternateNoTrade = false; // evaluate growth without trade

  static final String[] aChar = {"r", "c", "s", "g"};
  //static String[] rcsq = aChar;
  static String[] rcsg = aChar;
  static String[] aNames = {"resource", "cargo", "staff", "guests"};
  static String[] rcsqName = aNames;
  static int[] spluss = {0, 0, LSECS, LSECS};
  static String[] sChar = {"r", "r", "s", "s"};
  static String[] rcNsq = {"rc", "sq"};
  static String[] rNc = {"r", "c"};
  static String[] sNg = {"s", "g"};
  static String[] rNs = {"r", "s"};
  static String[] cNg = {"c", "g"};
  static int[] rorss = {0, 0, 2, 2};  // resorce or staff, also place or costs see
  static int[] d01 = {0, 1};
  static int[] balsIxA = {0, 1, 2, 3};
  static final int[] IA01 = d01;
  static final int[] MR = {0, LSECS};
  static int[] dlsecs = E.alsecs;
  static int[] d2lsecs = E.a2lsecs;
  static final int[] I2ASECS = E.a2lsecs;
  static int[] d25 = {2, 3, 4, 5};
  static final int[] IA25 = d25;
  static final int[] IA03 = {0, 1, 2, 3};
  static final int[] IA4 = IA03;

  /**
   * generate string of r or s source and index scrIx
   *
   * @param ixSrc 0,1 index of source r or s
   * @param srcIx index of sector
   * @return rNs[ixSrc] + srcIx
   */
  static String rNsIx(int ixSrc, int srcIx) {
    return rNs[ixSrc] + srcIx;
  }

  /**
   * generate String of r or s source and index
   *
   * @param n
   * @return
   */
  static String rNsIx(int n) {
    return rNs[(int) n / LSECS] + n % LSECS;
  }
  /**
   * the following variable control generation of manuals during a trade
   *
   */
  /**
   * Min cash must be reserved in a trade
   * req[E.iu1cashReservedForSOS][E.jsrcUsr1]
   */
  static public double[][] cashReservedForSOS = {{20., 20., 20., 20., 20.}, {20., 20., 20., 20., 20.}};

  /**
   * max fraction of worth that may be offered as cash in a trade
   * req[E.iu1cashMaxTradeFracCash][E.jsrcUsr1]
   */
  static public double[][] cashMaxTradeFracCash = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};


 // public enum SwpCmd {
 //   NOT("nothing assigned"), NONE("no Cmd"), NOOP("no operation"), GROW("in grow section"), HEALTH("Health swapping"), UINCR("un Incr"), RSINCR("pre Incr"), SINCR("increase staff guest to staff"), SINCR1("second incr staff G to S"), SINCR2("3 incr staff G to S"), SINCR3("4 incr staff G to S"), RINCR("increase available from cargo"), RINCR1("another increase available C to A"), RINCR2("another increase available C to A"), RINCR3("another increase available C to A"), RSINCR1("seconPre Incr"), /* AXINCR("increase available by transmute from another resource Cargo or Avail"), */ UDECR("un Decr"), RSDECR("pre DECR"), SDECR("decrease a large staff to Guests to help another resource staff"), RDECR("decrease large resource to help another resource"), RDECR1, RDECR2, RDECR3, SDECR1, SDECR2, SDECR3, UNXDECR("un XDECR"), RFUTUREFUND, REMERGFUTUREFUND, SFUTUREFUND, SEMERGFUTUREFUND, RFUTUREFUND1, REMERGFUTUREFUND1, SFUTUREFUND1, SEMERGFUTUREFUND1, RFUTUREFUND2, REMERGFUTUREFUND2, SFUTUREFUND2, SEMERGFUTUREFUND2, RFUTUREFUND3, REMERGFUTUREFUND3, SFUTUREFUND3, SEMERGFUTUREFUND3, RSXDECR("pre XDECR"), RXDECR("swap resources/staff between sectors, high resource cost"), RXDECR1("Second RXDECR"), RXDECR2("Second RXDECR"), RXDECR3("Second RXDECR"), SXDECR("swap resource with high staff costs"), SXDECR1("Second SXDECR"), SXDECR2("Second SXDECR"), SXDECR3("Second SXDECR"), TXDECR("reverse charge r sector"), TXDECR1, TXDECR2, TXDECR3, UXDECR, UXDECR1, UXDECR2, UXDECR3, TRADE("prepare for Trade"), TRADEG("Trade Guests between Planet and Ship"), TRADEC("Trade cargo between Ship and Planeet");
//    private String desc;
//    private double val;
//    private double low, high;
 //   private String desTip;

  //  private SwpCmd() {
  //    this.desc = "";
  //  }

  //  private SwpCmd(String desc) {
  //    this.desc = desc;
   // }

   // private SwpCmd(String desc, double ival, double plow, String dtip, Consumer sav) {
    //  sav.accept(ival);
    //}

  //  public int n() {
   //   return ordinal();
   // }

//  }

/*  SwpCmd[][] tst = incrs;
  static SwpCmd[][] incrs = {{SwpCmd.RINCR, SwpCmd.RINCR1, SwpCmd.RINCR2, SwpCmd.RINCR3}, {SwpCmd.SINCR, SwpCmd.SINCR1, SwpCmd.SINCR2, SwpCmd.GROW.SINCR3}};
  static SwpCmd[][] decrs = {{SwpCmd.RDECR, SwpCmd.RDECR1, SwpCmd.RDECR2, SwpCmd.RDECR3}, {SwpCmd.SDECR, SwpCmd.SDECR1, SwpCmd.SDECR2, SwpCmd.SDECR3}};
  static SwpCmd[][] futureFunds = {{SwpCmd.RFUTUREFUND, SwpCmd.REMERGFUTUREFUND, SwpCmd.RFUTUREFUND1, SwpCmd.REMERGFUTUREFUND1, SwpCmd.RFUTUREFUND2, SwpCmd.REMERGFUTUREFUND2, SwpCmd.RFUTUREFUND3, SwpCmd.REMERGFUTUREFUND3}, {SwpCmd.SFUTUREFUND, SwpCmd.SEMERGFUTUREFUND, SwpCmd.SFUTUREFUND1, SwpCmd.SEMERGFUTUREFUND1, SwpCmd.SFUTUREFUND2, SwpCmd.SEMERGFUTUREFUND2, SwpCmd.SFUTUREFUND3, SwpCmd.SEMERGFUTUREFUND3}};
  static SwpCmd[][][] dlecrs = {decrs, futureFunds};
  static SwpCmd[][] xdecrs = {{SwpCmd.RXDECR, SwpCmd.RXDECR1, SwpCmd.RXDECR2, SwpCmd.RXDECR3}, {SwpCmd.SXDECR, SwpCmd.SXDECR1, SwpCmd.SXDECR2, SwpCmd.SXDECR3}};
  static SwpCmd[][] udecrs = {{SwpCmd.TXDECR, SwpCmd.TXDECR1, SwpCmd.TXDECR2, SwpCmd.TXDECR3}, {SwpCmd.UXDECR, SwpCmd.UXDECR1, SwpCmd.UXDECR2, SwpCmd.UXDECR3}};
  static SwpCmd[][][] uxdecrs = {xdecrs, udecrs};

  EnumSet<SwpCmd> setTst = setIncr;
  static EnumSet<SwpCmd> setIncr = EnumSet.of(SwpCmd.RINCR, SwpCmd.SINCR, SwpCmd.RINCR1, SwpCmd.SINCR1, SwpCmd.RINCR2, SwpCmd.SINCR2, SwpCmd.RINCR3, SwpCmd.SINCR3);
  static EnumSet<SwpCmd> setDecr = EnumSet.of(SwpCmd.SDECR, SwpCmd.SDECR1, SwpCmd.SDECR2, SwpCmd.SDECR3, SwpCmd.RDECR, SwpCmd.RDECR1, SwpCmd.RDECR2, SwpCmd.RDECR3);
  static EnumSet<SwpCmd> setRDecr = EnumSet.of(SwpCmd.RDECR, SwpCmd.RDECR1);
  static EnumSet<SwpCmd> setSDecr = EnumSet.of(SwpCmd.SDECR, SwpCmd.SDECR1);
  static EnumSet<SwpCmd> setXXdecr = EnumSet.of(SwpCmd.RXDECR, SwpCmd.RXDECR1, SwpCmd.RXDECR2, SwpCmd.RXDECR3, SwpCmd.SXDECR, SwpCmd.SXDECR1, SwpCmd.SXDECR2, SwpCmd.SXDECR3, SwpCmd.TXDECR, SwpCmd.TXDECR1, SwpCmd.TXDECR2, SwpCmd.TXDECR3, SwpCmd.UXDECR, SwpCmd.UXDECR1, SwpCmd.UXDECR2, SwpCmd.UXDECR3);
  static EnumSet<SwpCmd> setRXdecr = EnumSet.of(SwpCmd.RXDECR, SwpCmd.RXDECR1);
  static EnumSet<SwpCmd> setSXdecr = EnumSet.of(SwpCmd.SXDECR, SwpCmd.SXDECR1);
  public static final int swpIncrv[] = {SwpCmd.RINCR.n(), SwpCmd.SINCR.n(), SwpCmd.RINCR1.n(), SwpCmd.SINCR1.n()};
  static final int swpXXdecr[] = {SwpCmd.RXDECR.n(), SwpCmd.RXDECR1.n(), SwpCmd.SXDECR.n(), SwpCmd.SXDECR1.n()};
  static final int swpSXdecr[] = {SwpCmd.SXDECR.n(), SwpCmd.SXDECR1.n()};
  static final int swpRXdecr[] = {SwpCmd.RXDECR.n(), SwpCmd.RXDECR1.n()};
  public static final int swpSIncrv[] = {SwpCmd.SDECR.n()};
  public static final int swpDecrv[] = {SwpCmd.SDECR.n()};
  public static final int swpADecrv[] = {SwpCmd.RINCR.n(), SwpCmd.RINCR1.n()};
  public static final int swpSDecrv[] = {SwpCmd.SDECR.n(), SwpCmd.SINCR.n(), SwpCmd.SINCR1.n()};
  // !swpXXdecrs.contains(cmd.n())
  static Set swpXXdecrs = new HashSet(Arrays.asList(swpXXdecr));
  static Set swpSXdecrs = new HashSet(Arrays.asList(swpSXdecr));
  static Set swpRXdecrs = new HashSet(Arrays.asList(swpRXdecr));
  public static Set swpIncrs = new HashSet(Arrays.asList(swpIncrv));
  public static Set swpDecrs = new HashSet(Arrays.asList(swpDecrv));
  */
  public static ArrayList<History> hist;
  public static final int[] forwardFundSwapNs = {0, 1, 2, 3, 4, 5, 11, 12, 15, 16, 20, 25, 30, 31, 35, 36, 37};
  static final Set ffSwapNs = new HashSet(Arrays.asList(forwardFundSwapNs));

  /**
   * list of reportable values r7
   */
  static int r7 = 0;
  // current year values
  static public final int rptInitWorth = r7++;  // initail worth for year
  static public final int rptEcons = r7++;  // created live or dead
  static public final int rptDead = r7++;
  static public final int rptLive = r7++;
  static public final int rptTrdRcv = r7++;   // received trade
  static public final int rptTrdSnd = r7++;   // send trade this yr
  static public final int rptTrds = r7++;   // total number of trades
  static public final int rptWTraded = r7++;  // worth after trades

  static public final int rptWCostGswaps = r7++;
  static public final int rptWGrowed = r7++;
  static public final int rptWCostTravel = r7++;
  static public final int rptWCostMaint = r7++;
  static public final int rptWCostGrow = r7++;

  static public final int rptWCostHSwaps = r7++;
  static public final int rptWEnd = r7++;
  static public final int rptPcntGrow = r7++;
  static public final int rptWNoTrade = r7++;
  static public final int rptNWCostGswaps = r7++;
  static public final int rptNNWCostGswaps = r7++;
//  static public final int rptNWGrowed = r7++;
//  static public final int rptNWCostTravel = r7++;
//  static public final int rptNWCostMaint = r7++;
//  static public final int rptNWCostGrow = r7++;

  // static public final int rptNWCostHSwaps = r7++;
  static public final int rptNWEnd = r7++;
  static public final int rptNNWEnd = r7++;
  static public final int rptNPcntGrow = r7++;
  static public final int rptWlayAway = r7++;
  static public final int rpt5YrG = r7++;
  static public final int rpt20YrG = r7++;
  static public final int rpt50YrG = r7++;
  static public final int rpt100YrG = r7++;

  static public final int rptL1 = r7;

  /**
   * list of reportable subvalues r8
   */
  static int r8 = 0;
  // total over all clans
  static public final int rpxTotal = r8++;
  static public final int rpxPTotal = r8++;
  static public final int rpxSTotal = r8++;
  // now total P + S for each clan
  static public final int rpxR = r8++;
  static public final int rpxO = r8++;
  static public final int rpxY = r8++;
  static public final int rpxG = r8++;
  static public final int rpxB = r8++;
  // totals for clan planets
  static public final int rpxPR = r8++;
  static public final int rpxPO = r8++;
  static public final int rpxPY = r8++;
  static public final int rpxPG = r8++;
  static public final int rpxPB = r8++;
  // totals for clan ships
  static public final int rpxSR = r8++;
  static public final int rpxSO = r8++;
  static public final int rpxSY = r8++;
  static public final int rpxSG = r8++;
  static public final int rpxSB = r8++;

  static public final int rpxL2 = r8;

  static public int[][] report;

  /*
  static void newRpt() {
    report = new int[rptL1][rpxL2];
    for (int i8 = 0; i8 < rptL1; i8++) {
      for (int i9 = 0; i9 < rpxL2; i9++) {
        report[i8][i9] = 0;
      }
    }
  }
   */
  /**
   * enum to store odd values in req
   *
   * @param icol define i column usually EXTRA or EXTRA2
   * @param jrow rows with an unused EXTRA or EXTRA2
   * @param isInt true if integer result, false if double result value * enum
   * reqs {
   *
   * AGE(reqc.EXTRA, reqv.WORKING, true), WORTHP1(reqc.EXTRA, reqv.AFERTILITY,
   * false), WORTHP2(reqc.EXTRA, reqv.AGCOST, false), WORTHP3(reqc.EXTRA,
   * reqv.AGEFFICIENCY, false), WORTHP4(reqc.EXTRA, reqv.AGROWTH, false),
   * WEALTH(reqc.EXTRA, reqv.AGROWTHISYEAR, false), SCORE(reqc.EXTRA,
   * reqv.AHEALTH, false), SCORE1(reqc.EXTRA, reqv.AMCOST, false),
   * SCORE2(reqc.EXTRA, reqv.AMEFFICIENCY, false); boolean isInt; // otherwise
   * double int icol; int jrow;
   *
   * private reqs() { isInt = false; icol = 0; jrow = 0; }
   *
   * private reqs(reqc icol, reqv jrow, boolean isInt) { this.isInt = isInt;
   * this.icol = icol.ordinal(); this.jrow = jrow.ordinal(); }
   *
   * public double put(double[][] req, double val) { myTest(isInt == true,
   * "attempt to store double int an int value"); req[icol][jrow] = val; return
   * val; }
   *
   * public int put(double[][] req, int val) { myTest(isInt == false, "attempt
   * to store int into double value"); req[icol][jrow] = val + 0.; return val; }
   *
   * public double get(double[][] req) { myTest(isInt == true, "attempt to get
   * double for an int value"); return req[icol][jrow]; }
   *
   * public int iget(double[][] req) { myTest(isInt == false, "attempt to get
   * double value for a int get"); return (int) req[icol][jrow]; }
   *
   * }
   */
  // public final static double invLsecs = 1 / lsecs;
  @Override
  protected void finalize() throws Throwable {
    super.finalize(); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * the req row or i index names, resource names, aux names mostly for related
   * tables total of 18 rows followed by sum,min,max,ave of the 7 resource
   * columns only, calculated by minMax Extra holds sums calculated other ways
   * often
   *
   * static public enum ReqI { // * LIFE, STRUCT, ENERGY, PROPEL, DEFENSE, GOV,
   * COLONIST, STAFFCONSUMERS, GUESTS, CARGO, SUM, EXTRA, MIN, MINIX, MIN2,
   * MIN2IX,MAX, MAXIX, AVE, COUNT; // 19 public int n() { return ordinal();} };
   *
   * protected enum Workers {
   *
   * LIFEWORKER, STRUCTWORKER, ENERGYWORKER, PROPELWORKER, DEFENSEWORKER,
   * GOVWORKER, COLONISTWORKER, SUM, COUNT };
   *
   *
   *
   * public enum SwpHist {
   *
   * RN, RFUTV, RHLTHV, AGROWV, RFUTMIN, RHLTHMIN, AGROWMIN, RESIX, RMINIX,
   * RFUTSUM, RHLTHSUM, AGROWSUM, RMOV, RCMD, RFORCECMD, RFORCEVAL, RAVOIDCMD,
   * AGROWTH, AGROWTH2, RLIM, RHEALTH, SN, SFUTV, SHLTHV, SGROWV, SFUTMIN,
   * SHLTHMIN, SGROWMIN, STAFFIX, SMINIX, SFUTSUM, SHLTHSUM, SGROWSUM, SMOV,
   * SCMD, SFORCECMD, SFORCEVAL, SAVOIDCMD, SGROWTH, SGROWTH2, SLIM, SHEALTH,
   * COUNT;
   *
   * public int n() { return ordinal(); } }
   */
  //  static protected double defaultFacultyForUpgrade = 3; // indepentent of knowledge
// static protected double nominalEngineerEquivWorkers = 9;
  // static protected double knowledgeCreatedPerResearcher = 5.;
  // static protected double FLearnManuals = .3;  // m to k per FacultyEqv
  // static protected double ELearnManuals = .1;  // m to k per Engineer
  // static protected double Trainee3ManualsFrac = .3;  // E and F trainee3 councount as frac of E or
  /**
   * This vector controls the upgrades for staff and guests when an update is
   * done. A number greater than 1 means a jump upward by that number, the jump
   * is to a full staff member. Full staff are colonist(3), engineer(7),
   * faculty(11), researcher(15). In particular only a fraction of full staff
   * upgrade as indicated by that fraction. In addition, more staff upgrade
   * depending on the effective faculty before the upgrade.
   */
  // static double[] fractionStaffUpgrade = {2.3, 2.4, 1., .6, 3.3, 2.5, 1., .25, 3.25, 2.5, 1.5, .25, 2.25, 1.5, 1., 0.};
  // all0w more upgrades between types
  static double[] fractionStaffUpgrade = {2.3, 2.4, 2.0, .6, 3.3, 2.5, 2.3, 2.25, 3.25, 2.5, 2.5, 2.25, 2.25, 1.5, 1., 0.};
//  static protected int[][] limitJumpsPerFaculty = {{15, 12, 10, 8}, {15, 12, 10, 8}, {15, 12, 10, 8}, {15, 12, 10, 8}, {15, 12, 10, 8}};
//  static protected double[] knowledgeRequiredPerFacultyForJumping = {75., 75., 75., 75., 100., 100., 100., 100., 125., 125., 125., 125., 150., 150., 150., 150.};

  // [sIx][p,s]
  //static final protected double growthEfficiencyDivMaint[] = {1.2,1,2};
  // this table used to multiply against the staff array to get sWorker staff worker value calc swork;
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
  }
  ;
 
  /**
   * CalcReq growth fraction of SG ave to keep as S to reserve after growth
   * before swap to guests
   */
  static public double[][] staffGrowSAveMin = {{.003, .003, .003, .003, .003},
  {.003, .003, .003, .003, .003}};
  /**
   * specify the minimum fraction of a resource to be reserved kept and not
   * sweapped. emin is an additional factor further reducing the required
   * reserve. at CalcReq.swaps minADecr minSDecr swap ADECR SDECR minRIncr
   * minSIncr for swap RINCR SINCR minRXIncr minSXIncr for swap AXINCR SXINCR
   * the 1-3 characters before Max represent which mode flags: h=health always
   * true swap to increase health g=growth swap to increase growth f=future swap
   * to increase probable future when fMaxn,hMaxn,gMaxn [pors][group]
   */

  /**
   * the following reserve numbers represent the remnant working value, + an
   * additional fraction. That fraction is kept aside for additional trade, or
   * health swapping.
   */
  static double[][] emergRTrade = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  static double[][] emergSTrade = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  static double[][][] emergTrade = {emergRTrade, emergSTrade};
  static double[][] regRTrade = {{.2, .2, .2, .2, .2}, {.2, .2, .2, .2, .2}};
  static double[][] regSTrade = {{.2, .2, .2, .2, .2}, {.2, .2, .2, .2, .2}};
  static double[][][] regTrade = {regRTrade, regSTrade};
  static double[][] regSReserve = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  static double[][] regRReserve = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  //   [rors][pors][clan]
  static double[][][] regReserve = {regRReserve, regSReserve};
  static double[][] emergRReserve = {{.02, .02, .02, .02, .02}, {.02, .02, .02, .02, .02}};
  static double[][] emergSReserve = {{.02, .02, .02, .02, .02}, {.02, .02, .02, .02, .02}};
  static double[][][] emergReserve = {emergRReserve, emergSReserve};

  /**
   * multiply the jsrcASwap > 0., to increase S need when R was swapped to
   */
  static public double[][] swpAmultGrowthSGP = {{1.75, 1.75, 1.75, 1.75, 1.75}, {.15, .15, .15, .15, .15}};
  /**
   * multiply the jsrcASwap &lt; 0., to decrease S need when R was swap from
   */
  static public double[][] swpAmultGrowthSGN = {{.05, .05, .05, .05, .05}, {.02, .02, .02, .02, .02}};
  /**
   * multiply the jsrcCSwap &gt; 0., to increase S need when C is swapto
   */
  static public double[][] swpCmultGrowthSGP = {{.25, .25, .25, .25, .25}, {.05, .05, .05, .05, .05}};
  /**
   * multiply the jsrcCSwap < 0., decrease S need when C was swapfrom
   */
  static public double[][] swpCmultGrowthSGN = {{.05, .05, .05, .05, .05}, {.02, .02, .02, .02, .02}};
  /**
   * multiply the jsrcCSwap &lt; 0., increase S need when RX was swapto
   */
  static public double[][] swpXAmultGrowthSGP = {{.25, .25, .25, .25, .25}, {.05, .05, .05, .05, .05}};
  /**
   * multiply the jsrcCSwap &lt; 0., decrease S need when RX was swapfrom
   */
  static public double[][] swpXAmultGrowthSGN = {{.05, .05, .05, .05, .05}, {.02, .02, .02, .02, .02}};
  /**
   * multiply the jsrcCSwap &lt; 0., increase S need when CX was swapto
   */
  static public double[][] swpXCmultGrowthSGP = {{.25, .25, .25, .25, .25}, {.05, .05, .05, .05, .05}};
  /**
   * multiply the jsrcCSwap <&lt; 0., decrease S need when C was swapfrom
   */
  static public double[][] swpXCmultGrowthSGN = {{.05, .05, .05, .05, .05}, {.02, .02, .02, .02, .02}};
  /**
   * multiply the jsrcSSwap &lt; 0., increase S need when S was swapto
   */
  static public double[][] swpSmultGrowthSGP = {{1.25, 1.25, 1.25, 1.25, 1.25}, {.05, .05, .05, .05, .05}};
  /**
   * multiply the jsrcSSwap &lt; 0., increase S need when S was swapfrom
   */
  static public double[][] swpSmultGrowthSGN = {{.05, .05, .05, .05, .05}, {.02, .02, .02, .02, .02}};
  /**
   * multiply the jsrcXSSwap < 0., increase S need when CX was swapto
   */
  static public double[][] swpXSmultGrowthSGP = {{1.25, 1.25, 1.25, 1.25, 1.25}, {.05, .05, .05, .05, .05}};
  /**
   * multiply the jsrcXSSwap &lt; 0., increase S need when C was swapfrom
   */
  static public double[][] swpXSmultGrowthSGN = {{.05, .05, .05, .05, .05}, {.02, .02, .02, .02, .02}};

  /**
   * at CalcReq.maxMin maxSXIncr reserve fraction of SG ave in swap
   *
   */
  //static public double[] staffIncReqSAveMin = {.05, .05};
  /**
   * the fraction of mov lt maxAvail*fracReqForStaffGrowth[planetOrShip][clan]
   * for staff growth
   */
  static public double[][] fracReqForStaffGrowth = {{1.3, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  static public double[][] resFracReqForStaffGrowth = {{1.3, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  static public double[][] staffFracReqForStaffGrowth = {{1.3, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};

  /**
   * compute stratWorth the strategic unit worth of each resource and staff
   *
   */
  static final public double stratTmp = 0.;
  static final public double myStratAvec = .2;
  static final public double effStratMax = 22;
  static final public double myStratGBias = .4;
  static final public double stratGFrac = .2;
  static final public double myStratMinG = .15;
  static final public double gStratMax = 22;
  static final public double myMaxxxMinBias = .3;
  static final public double myMaxxxMaxBias = .4;
  static final public double myMaxxxBias = .4;
  static final public double stratMaxFrac = .2;
  static final public double maxxStratMax = 22;
  static final public double[] strategyLYTravelBias = {0, 15};

  static public double swapMin = .01;
  static final public double[] healthRRemBias = {0., 0.};
  static final public double[] healthSRemBias = {0., 0.};
  static final public double[] warnRRemBias = {-.4, -.4};
  static final public double[] warnSRemBias = {-.4, -.4};
  static final public double[] wellnessRRemBias = {-1., -1.};
  static final public double[] wellnessSRemBias = {-1., -1.};
  static final public double[] healthMultRem = {4., 4.};
  static final public double[] healthRemBias = {.3, .3};
  static final public double[] healthMaxFrac = {1.3, 1.3};
  static final public double[] healthMinFrac = {.2, .2};
  static final public double[] healthMaintMinFrac = {.4, .4};
  static final public double[] healthMaintMaxFrac = {1.5, 1.5};
  static final public double[] growthMultRem = {4., 4.};
  static final public double[] growthRemBias = {.3, .3};
  static final public double[] growthMaxFrac = {1.3, 1.3};
  static final public double[] growthMinFrac = {.2, .2};
  static public double[][] swapsGrowBias = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  static public double[][] minsGrowBias = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  static public int Av = 0, St = 1, high = 0, mid = 1, low = 2;
  //fracBelowMin[pors][clan][R.yearPhase3][E.Av][E.high]
  static public double fracBelowMin[][][][][]
          = {{{{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}}},
          {{{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}},
          {{{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}},
          {{.5, .3, .2}, {.5, .3, .2}}, {{.5, .3, .2}, {.5, .3, .2}}}
          }
          };
  /**
   * now define user variables [planetOrShip][clan] affecting health and growth
   * future ...Run affects the swap options ...Trade affects the TRD options
   * ...Ave are the average of all Environ s of the clan
   */
  static public double[][] rHealthEmergency = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  static public double[][] sHealthEmergency = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  static public double[][] rFertilityEmergency = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  static public double[][] sFertilityEmergency = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};

  static public double[][] sSwapMore = {{1.2, 1.2, 1.2, 1.2, 1.2}, {1.2, 1.2, 1.2, 1.2, 1.2}};

  /**
   * ADecrMult and SDecrMult is the multiplier to the need (maxAvail or
   * maxStaff) that determines a ?DECR, users may change the value as the game
   * proceeds * static public double[][] ADecrMult = {{1.3, 1.3, 1.3, 1.3, 1.3},
   * {1.3, 1.3, 1.3, 1.3, 1.3}}; static public double[][] SDecrMult = {{1.3,
   * 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
   *
   ***
   * AXincrMult and SXincrMult is the multiplier to the need (maxAvail or
   * maxStaff) that determines a ?XINCR, users may change the value as the game
   * proceeds
   *
   * static public double[][] AXIncrMult = {{.65, .65, .65, .65, .65}, {.65,
   * .65, .65, .65, .65}}; static public double[][] SXIncrMult = {{.65, .65,
   * .65, .65, .6}, {.65, .65, .65, .65, .65}};
   */
  // the multiple of maxAvail or maxStaff required before willing move to C or G
//  static protected double[][] maxAvailToCargo= {{1.6,1.6,1.6,1.6,1.6},{1.6,1.6,1.6,1.6,1.6}};
//  static protected double[][] maxStaffToGuests= {{1.6,1.6,1.6,1.6,1.6},{1.6,1.6,1.6,1.6,1.6}};
  /**
   * The multiple of maxAvail to remain after a T swap
   * req[E.iu2usrSwpTAToCMultMaxAvail][D.jsrcUsr2]
   */
  static final public double[][] usrSwpTAToCMultMaxAvail = {{1.1, 1.1, 1.1, 1.1, 1.1}, {1.1, 1.1, 1.1, 1.1, 1.1}};
  /**
   * the multiple of maxStaff to remain after a T swap
   * req[E.iu2usrSwpXSToGMultMaxStaff][D.jsrcUsr2]
   */
  static final public double[][] usrSwpTSToGMultMaxStaff = {{1.1, 1.1, 1.1, 1.1, 1.1}, {1.1, 1.1, 1.1, 1.1, 1.1}};
  static final public double[][] usrSwpXAToCMultMaxAvail = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  static final public double[][] usrSwpXSToGMultMaxStaff = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  static final public double[][] usrSwpAToCMultMaxAvail = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  static final public double[][] usrSwpSToGMultMaxStaff = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  /**
   * user priority adjustments priorityAdjustmentMultiplierFrac[]
   * userPriorityAdijustment[pors][clan][sectors]
   */
//  static public double priorityAdjustmentMultiplierFrac[] = {1., 1.};
  // user Priority 1-35 integer
  /* static public double userPriorityAdjustment[][][]
                             = {
            {
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01}
            },
            {
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01},
              {.01, .01, .01, .01, .01, .01, .01}
            }
          }; */

  /**
   * pick the multipliers of need to apply in swapCalcValues to calculate move
   * [pors][clan]
   */
  static public double[][] usrAvailGrowMult = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static public double[][] usrStaffGrowMult = {{3.5, 3.5, 3.5, 3.5, 3.5}, {3.5, 3.5, 3.5, 3.5, 3.5}};
  static public double[][] usrAvailIncrMult = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static public double[][] usrStaffIncrMult = {{3.5, 3.5, 3.5, 3.5, 3.5}, {3.5, 3.5, 3.5, 3.5, 3.5}};
  static public double[][] usrAIncrMultS = {{.4, .4, .4, .4, .4}, {.4, .4, .4, .4, .4}};
  static public double[][] usrMultAveFucundity = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static public double[][] usrMultAveFertility = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  /**
   * the multiple of maxStaff to remain after grow
   */
  static final public double[][] usrSwpGrowGToSMultMaxStaff = {{1.04, 1.04, 1.04, 1.04, 1.04}, {1.04, 1.04, 1.04, 1.04, 1.04}};
  /**
   * the fraction of G+S to be left in S when decr Swap
   */
  static final public double[][] usrSwpMidGToSMultMaxStaff = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  static final public double[][] usrSwpGrowCToAMultMaxAvail = {{1.04, 1.04, 1.04, 1.04, 1.04}, {1.04, 1.04, 1.04, 1.04, 1.04}};
  static final public double[][] usrSwpMidCToAMultMaxAvail = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  static final public double[][] usrSwpMidXCToAMultMaxAvail = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  /**
   * less tight multiple of maxAvail or maxStaff to remain after a swap to C or
   * G
   */
  static final public double[][] usrSwpLowCToAMultMaxAvail = {{1.2, 1.2, 1.2, 1.2, 1.2}, {1.2, 1.2, 1.2, 1.2, 1.2}};
  static final public double[][] usrSwpLowGToSMultMaxStaff = {{1.2, 1.2, 1.2, 1.2, 1.2}, {1.2, 1.2, 1.2, 1.2, 1.2}};
  static final public double[][] usrSwpLowXCToAMultMaxAvail = {{1.2, 1.2, 1.2, 1.2, 1.2}, {1.2, 1.2, 1.2, 1.2, 1.2}};
  static final public double[][] usrSwpLowXGToSMultMaxStaff = {{1.2, 1.2, 1.2, 1.2, 1.2}, {1.2, 1.2, 1.2, 1.2, 1.2}};
  static final public double[][] usrSwpLowTCToAMultMaxAvail = {{1.2, 1.2, 1.2, 1.2, 1.2}, {1.2, 1.2, 1.2, 1.2, 1.2}};
  static final public double[][] usrSwpLowTGToSMultMaxStaff = {{1.2, 1.2, 1.2, 1.2, 1.2}, {1.2, 1.2, 1.2, 1.2, 1.2}};
  /**
   * fraction of maxAvail or maxStaff allowed to swap
   */
  static final public double[] sSwapToGFracRem = {.9, .9};
  static final public double[] rSwapToCFracRem = {.9, .9};
  /**
   * fraction of incr or grow res, that includes grow staff
   */
  static protected double incrFracStaffForRes[][] = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};
  static protected double growthFracStaffForRes[][] = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};
  static protected double usrMaintBias1[][] = {{2, 2, 2, 2, 2}, {2, 2, 2, 2, 2}};
  static protected double usrMaintBias2[][] = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  static protected double usrMaintBias3[][] = {{4, 4, 4, 4, 4}, {4, 4, 4, 4, 4}};
  static protected double usrMaintBias4[][] = {{5, 5, 5, 5, 5}, {5, 5, 5, 5, 5}};
  static protected double usrSTravBias[][] = {{2, 2, 2, 2, 2}, {2, 2, 2, 2, 2}};
  static protected double usrAGrowthBias[][] = {{2, 2, 2, 2, 2}, {2, 2, 2, 2, 2}};
  static protected double usrSGrowthBias[][] = {{2, 2, 2, 2, 2}, {2, 2, 2, 2, 2}};
  // adjust emergency transmute one resource to another,
  // but the cost is very high
  /**
   * max fraction of ave R resources A[12] to transmute in 1 year
   */
  static protected double[][] rXSwpMaxByResAve = {{1.3, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  /**
   * max fraction of ave R do not limit trades
   */
  static protected double[][] rTSwpMaxByResAve = {{5., 5., 5., 5., 5.}, {5., 5., 5., 5., 5.}};
  /**
   * max fraction of ave R resources to swap in 1 year
   */
  static protected double[][] rSwpMaxByResAve = {{.7, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  /**
   * max fraction of ave S resources to transmute in 1 year
   */
  static protected double[][] sXSwpMaxByStaffAve = {{.7, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  /**
   * max fraction of ave S resources to Trade in 1 year
   */
  static protected double[][] sTSwpMaxByStaffAve = {{5., 5., 5., 5., 5.}, {5., 5., 5., 5., 5.}};
  /**
   * max fraction of ave S resources to swap in 1 year
   */
  static protected double[][] sSwpMaxByStaffAve = {{.7, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  /**
   * max fraction of ave resources for a resource to receive Xswap
   */
  static protected double[][] rXSwpMaxRcvResAve = {{.7, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  /**
   * max fraction of ave resources for a resource to receive Tswap
   */
  static protected double[][] NOTrTSwpMaxRcvResAve = {{5., 5., 5., 5., 5.}, {5., 5., 5., 5., 5.}};
  /**
   * max fraction of ave resources for a resource to receive swap
   */
  static protected double[][] rSwpMaxRcvResAve = {{.7, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  /**
   * don't Xswap so that staff start with more than this value
   */
  static protected double[][] NOTsXSwpMaxRcvStaffAve = {{.7, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};
  /**
   * don't Tswap so that staff start with more than this value
   */
  static protected double[][] sTSwpMaxRcvStaffAve = {{5., 5., 5., 5., 5.}, {5., 5., 5., 5., 5.}};
  /**
   * don't swap so that staff start with more than this value
   */
  static protected double[][] sSwpMaxRcvStaffAve = {{.7, 1.3, 1.3, 1.3, 1.3}, {1.3, 1.3, 1.3, 1.3, 1.3}};

  /**
   * from sum (avail+cargo) max frac which may be traded per year
   */
  static public double[][] rTSwpMaxFracResInputSum = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};

  /**
   * output from (staff+guest) max frac which may be traded per year
   */
  static public double[][] sTSwpMaxFracInputSum = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};
  /**
   * output from (staff+guest) max frac which may be swapped per year
   */
//  static  public double[][] sSwpMaxFracInputSum= {{.7,.7,.7,.7,.7},{.7,.7,.7,.7,.7}};
  /**
   * output from (staff+guest) max frac which may be transmuted per year
   */
  static public double[][] sXSwpMaxFracInputSum = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};
  /**
   * Maximum faction of maxAvail can be transmuted per year
   */
  static public double[][] rXSwpMaxFracRes = {{.95, .95, .95, .95, .95}, {.95, .95, .95, .95, .95}};
  /**
   * Maximum faction of maxAvail0 can be traded per year first
   */
  static public double[][] rTSwpMaxFracRes = {{.8, .8, .8, .8, .8}, {.8, .8, .8, .8, .8}};
  /**
   * Maximum faction of maxAvail0 can be traded per year final value
   */
  static public double[][] rTSwpMax2FracRes = {{.95, .95, .95, .95, .95}, {.95, .95, .95, .95, .95}};
  /**
   * Maximum faction of maxAvail can be swapped per year
   */
  static public double[][] rSwpMaxFracRes = {{.95, .95, .95, .95, .95}, {.95, .95, .95, .95, .95}};
  /**
   * Maximum faction of maxStaff can be Transmuted per year
   */
  static public double[][] sXSwpMaxFracRes = {{.95, .95, .95, .95, .95}, {.95, .95, .95, .95, .95}};
  /**
   * Maximum faction of maxStaff can be Traded per year
   */
  static public double[][] sTSwpMaxFracRes = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};
  /**
   * Maximum faction of maxStaff can be swapped per year
   */
  static public double[][] sSwpMaxFracRes = {{.95, .95, .95, .95, .95}, {.95, .95, .95, .95, .95}};
  /**
   * Max fraction of maxStaff0 which may be traded, first value
   */
  static public double[][] sTSwapMaxFracStaff = {{.8, .8, .8, .8, .8}, {.8, .8, .8, .8, .8}};
  /**
   * Max fraction of maxStaff0 which may be traded, final value
   */
  static public double[][] sTSwapMax2FracStaff = {{.95, .95, .95, .95, .95}, {.95, .95, .95, .95, .95}};

  /**
   * the Fertility and Health Bias are subtracted from the Fertility and Health
   * values for available and staff resources of a economy sector. The result is
   * the minAvail and minStaff values that indicate the need of a particular
   * sector. These values may be set by players
   */
  static public double[][] aFertilityBiasHigh = {{0., 0., 0., 0., 0.}, {0., 0., 0., 0., 0.}};
  static public double[][] sFertilityBiasHigh = {{0., 0., 0., 0., 0.}, {0., 0., 0., 0., 0.}};
  static public double[][] aHealthBiasHigh = {{0., 0., 0., 0., 0.}, {0., 0., 0., 0., 0.}};
  static public double[][] sHealthBiasHigh = {{0., 0., 0., 0., 0.}, {0., 0., 0., 0., 0.}};
  static public double[][] aFertilityBiasMiddle = {{1., 1., 1., 1., 1.}, {1., 1., 1., 1., 1.}};
  static public double[][] sFertilityBiasMiddle = {{1., 1., 1., 1., 1.}, {1., 1., 1., 1., 1.}};
  static public double[][] aHealthBiasMiddle = {{1., 1., 1., 1., 1.}, {1., 1., 1., 1., 1.}};
  static public double[][] sHealthBiasMiddle = {{1., 1., 1., 1., 1.}, {1., 1., 1., 1., 1.}};
  static public double[][] aFertilityBiasLow = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static public double[][] sFertilityBiasLow = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static public double[][] aHealthBiasLow = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static public double[][] sHealthBiasLow = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static public double[][] need0 = {{0., 0., 0., 0., 0.}, {0., 0., 0., 0., 0.}};

  /**
   * biases to be applied after the need is determined, and subtracted from the
   * resource or staff ARow rneed, sneed; Temporarily use just Middle
   * [resource,staff][health,fertility][high,middle,low,none][pors][clan]
   */
  static public double[][][][][] biases = {{{aHealthBiasHigh, aHealthBiasMiddle, aHealthBiasLow},
  {aFertilityBiasHigh, aFertilityBiasMiddle, aFertilityBiasLow}}, {{sHealthBiasHigh, sHealthBiasMiddle, sHealthBiasLow}, {sFertilityBiasHigh, sFertilityBiasMiddle, sFertilityBiasLow}}};
  public static int r = 0, resource = 0, staff = 1, s = 1, top = 0, middle = 1, bottom = 2, none = 3;
  public static int H = 0, F = 1, Health = 0, Fertility = 1;
  static public double[][] rFertilityHigh = {{1.1, 1.1, 1.1, 1.1, 1.1}, {1.1, 1.1, 1.1, 1.1, 1.1}};
  static public double[][] sFertilityHigh = {{1.1, 1.1, 1.1, 1.1, 1.1}, {1.1, 1.1, 1.1, 1.1, 1.1}};
  static public double[][] rHealthHigh = {{1.1, 1.1, 1.1, 1.1, 1.1}, {1.1, 1.1, 1.1, 1.1, 1.1}};
  static public double[][] sHealthHigh = {{1.1, 1.1, 1.1, 1.1, 1.1}, {1.1, 1.1, 1.1, 1.1, 1.1}};
  static public double[][] rFertilityMiddle = {{.6, .6, .6, .6, .6}, {.6, .6, .6, .6, .6}};
  static public double[][] sFertilityMiddle = {{.6, .6, .6, .6, .6}, {.6, .6, .6, .6, .6}};
  static public double[][] rHealthMiddle = {{.6, .6, .6, .6, .6}, {.6, .6, .6, .6, .6}};
  static public double[][] sHealthMiddle = {{.6, .6, .6, .6, .6}, {.6, .6, .6, .6, .6}};
  static public double[][] rFertilityLow = {{.15, .15, .15, .15, .15}, {.15, .15, .15, .15, .15}};
  static public double[][] sFertilityLow = {{.15, .15, .15, .15, .15}, {.15, .15, .15, .15, .15}};
  static public double[][] rHealthLow = {{.15, .15, .15, .15, .15}, {.15, .15, .15, .15, .15}};
  static public double[][] sHealthLow = {{.15, .15, .15, .15, .15}, {.15, .15, .15, .15, .15}};
  /**
   * triggers are trigger levels associated with need
   * [r,s][Health,Fertility][top,Middle,bottom][p,s][clan]
   */
  static public double[][][][][] trigger = {{{rHealthHigh, rHealthMiddle, rHealthLow},
  {rFertilityHigh, rFertilityMiddle, rFertilityLow}}, {{sHealthHigh, sHealthMiddle, sHealthLow}, {sFertilityHigh, sFertilityMiddle, sFertilityLow}}};

  static public double[][] sSwapMaxFracStaff = {{1., 1., 1., 1., 1,}, {1., 1., 1., 1., 1,}};
  static public double[][] sXSwapMaxFracStaff = {{1., 1., 1., 1., 1,}, {1., 1., 1., 1., 1,}};
  static public double[][] rTSwapMaxFracStaff = {{1., 1., 1., 1., 1,}, {1., 1., 1., 1., 1,}};
  static public double[][] rSwapMaxFracStaff = {{1., 1., 1., 1., 1,}, {1., 1., 1., 1., 1,}};
  static public double[][] rXSwapMaxFracStaff = {{1., 1., 1., 1., 1,}, {1., 1., 1., 1., 1,}};

  /**
   * the swpRCmultGrowthSG? , swpXRCmultGrowthSG? are growth multipliers that
   * translate resource swaps into changes in maxStaff negative swaps (from)
   * translate into increased maxAvail) positive swaps (to) translate into
   * decreased maxAvail
   */
  static public double[] iswpRCmult = {.85, .85};
  static public double[] iswpXRCmult = {.45, .45};
  static public double[] oswpRCmult = {.75, .75};
  static public double[] oswpXRCmult = {.25, .25};
  static public double[] iswpSGmult = {.85, .85};
  static public double[] iswpXSGmult = {.45, .45};
  static public double[] oswpSGmult = {.75, .75};
  static public double[] oswpSGXmult = {.25, .25};
  // the following go into reducing swap amounts at steps, and transition to real year
  static public double[] swpRCSGmultStep = {.85, .85};
  static public double[] swpXRCSGmultStep = {.65, .65};
  static public double[] swpRCSGGTransSage = {.85, .85};
  static public double[] swpXRCSGGTransSage = {.65, .65};

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone(); //To change body of generated methods, choose Tools | Templates.
  }
  /**
   * old ones replaced above static final public double[]
   * sXSwapInputResCostPerUnit = {.2, .2}; static final public double[]
   * gTradeInputCargoCostPerUnit = {0., 0.}; static final public double[]
   * cTradeInputCargoCostPerUnit = {0., 0.}; static final public double[]
   * sSwapInputResCostPerUnit = {.01, .01}; static final public double[]
   * gTradeInputGuestsCostPerUnit = {.0, .0}; static final public double[]
   * cTradeInputGuestsCostPerUnit = {.0, .0}; static final public double[]
   * sXSwapInputStaffCostPerUnit = {.02, .02}; static final public double[]
   * gXSwapInputGuestsCostPerUnit = {.02, .02}; static final public double[]
   * cXSwapInputGuestsCostPerUnit = {.02, .02}; static final public double[]
   * sSwapInputStaffCostPerUnit = {.01, .01}; static final protected double[]
   * rXSwapInputResCostPerUnit = {17., 17.}; static final protected double[]
   * cXSwapInputCargoCostPerUnit = {17., 17.}; static final protected double[]
   * gXSwapInputCargoGuestCostPerUnit = {17., 17.}; static final protected
   * double[] rTradeInputResCostPerUnit = {0., 0.}; static final protected
   * double[] cTradeInputResCostPerUnit = {0., 0.}; static final protected
   * double[] rSwapInputResCostPerUnit = {.1, .1}; static final protected
   * double[] cSwapInputResCostPerUnit = {.1, .1}; static final protected
   * double[] rXSwapInputStaffCostPerUnit = {2., 4.}; static final protected
   * double[] cXSwapInputStaffCostPerUnit = {2., 4.}; static final protected
   * double[] rTradeInputStaffCostPerUnit = {0., 0.}; static final protected
   * double[] cTradeInputStaffCostPerUnit = {0., 0.}; static final protected
   * double[] rSwapInputStaffCostPerUnit = {.01, .01}; static final protected
   * double[] cSwapInputStaffCostPerUnit = {.01, .01};
   */
  /**
   * myRequested > myMult*myOffered myRequested/myOffered > myMult oRequested <
   * oMult*oOffered oRequested/oOffered < oMult(high) oMult represents to ceiling bid
   * value I will accept at various stages ships must request more than they offer, to
   * account for transportation and planet must offer more than they request First =
   * initial bid each side 60 for initial, 50 for ship, 40 for planet planets and ships
   * offer all they have to offer, and request their needs so first myMult is genereally
   * the highest value I think can be accepted, quite high middle = between terms 40 - 10
   * planets and ships indicate what they really request and limits on offer so mult is
   * somewhat lower final = 10 to -1 oMult final (with modifiers) is now the highest bid
   * oReq/oOffered acceptable myMult final (with modifiers) is the lowest I think
   * myReq/myOffered acceptable favor runs from 0 to 100 myFav = (1 + favFrac* myFav =
   * myFav > favMin?favMult < favMax?favMult:favMax:favMin if(sos) sosfrac > 1.
   * is sosMult = sos?sosfrac:1. myMult =
   * first?req[E.iu3tradeFirstBidLowFrac][E.jsrcUsr3]:middle?
   * req[E.iu3tradeMiddleBidLowFrac][E.jsrcUsr3]
   * :req[E.iu3tradeFinalBidLowFrac][E.jsrcUsr3] myTradMult =
   * myMult/(myfavMult*ososMult) myfavMult(my favor to bidder) decreases
   * myRequest ososMult decreases myoffered req ofavMult and mysosMult do not
   * affect myoffered, only affect the other so if myrequested/myoffered < tradLowMult request is ok this is bidoffered/bidrequested
   * >
   * L
   * owFrac bid is ok if myrequested/myoffered > HighFrac probably ok, but the
   * other must decide if bidoffered/bidrequested < HighFrac I don't care, I'm
   * glad if it is higher
   */
  static protected double favFrac = 1.2;
  static protected double favMax = 2.5;
  static protected double favMin = .7;
  /**
   * tradeFirstBidLowFrac[pors][clan] This is the lowest request/offer that is
   * acceptable
   */
  static protected double tradeFirstBidLowFrac[][] = {{.6, .6, .6, .6, .6}, {1.1, 1.1, 1.1, 1.1, 1.1}};
  /**
   * middle low bid value fraction of strategic worth
   */
  static final protected double tradeMiddleBidLowFrac[][] = {{.7, .7, .7, .7, .7}, {1.2, 1.2, 1.2, 1.2, 1.2}};
  /**
   * final low bid value fraction of strategic worth
   * req[E.iu3tradeFinalBidLowFrac][E.jsrcUsr3]
   */
  static protected double tradeFinalBidLowFrac[][] = {{.8, .8, .8, .8, .8}, {1.4, 1.4, 1.4, 1.4, 1.4}};
  /**
   * first high bid value fraction of strategic worth my requested/offered =
   * mult, high means the limit on what I will request in terms of what my offer
   * can be, high must always be higher than low, and higher than final low.
   * req[E.iu4tradeFirstBidHighFrac][E.jsrcUsr4]
   */
  static protected double tradeFirstBidHighFrac[][] = {{1., 1., 1., 1., 1.}, {1.6, 1.6, 1.6, 1.6, 1.6}};
  /**
   * middle bid value fraction of strategic worth
   * req[E.iu4tradeMiddleBidHighFrac][E.jsrcUsr4]
   */
  static protected double tradeMiddleBidLHighFrac[][] = {{1.1, 1.1, 1.1, 1.1, 1.1}, {1.7, 1.7, 1.7, 1.7, 1.7}};
  /**
   * final bid value fraction of strategic worth
   * req[E.iu4tradeFinalBidHighFrac][E.jsrcUsr4]
   */
  static protected double tradeFinalBidHighFrac[][] = {{1.15, 1.15, 1.15, 1.15, 1.15}, {1.75, 1.75, 1.75, 1.75, 1.75}};

  static protected double createPORS[] = {3000., 3000., 3000., 3000., 3000.};
  static protected double createShipClanFrac[] = {1.5, 1.5, 1.5, 1.5, 1.5};
  static protected double createShipGameFrac[] = {1.5, 1.5, 1.5, 1.5, 1.5};

  // [planet,ship][life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
  //    [life,struct,energy,propel,defense,gov,colonist,lifeWorkers,structWorkers,energyWorkers,
  //     propelWorkers,defenseWorkers,govWorkers,colonistWorkers]
  static final protected double[] maintReqGrossAdj = {1.5, 1.4};  // maintenance requirement bias > 1
  // multiply projected Maint costs by maintCostGrossAdj when calculating costs  of maint
  static final protected double[] maintCostGrossAdj = {.9, .9};
  // multiply projected costs by travs when predicting costs  of maint,travel
  static final protected double[] travelCostGrossAdj = {.3, .3};

  // [planet,ship][colonist,engineer,faculty,researcher,guestC,guestE,guestF,guestR]
  // [planet,ship][life,struct,energy,propel,defense,gov,colonist]*[bias,-randomMultiplier]
  // general bias to broadly adjust maintenance costs
  // adjust emergency growth costs for resources, this allows growth outside the normal growth
  // but the cost is very high
  /**
   * the table of required maint available and staff, assuming full growth *
   * [planet,ship] (context pors)
   * [life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
   * (consumers)
   * [life,struct,energy,propel,defense,gov,colonist,lifeWorkers,structWorkers,energyWorkers,propelWorkers,defenseWorkers,govWorkers,colonistWorkers,
   * (servers, suppliers of resource guests,cargo]
   * iAConsumerRequiredForMaintenance.add(i, t1 = balance.get(i) *
   * mReqs[pors][i][j] * E.maintReqAdj[pors][k] * (E.tableRow[k] == 0 ? 1. :
   * mReqs[pors][E.tableRow[k]][j]) / maintEfficiency.get(i)); so the row staff,
   * guests or cargo values modify the SubAsset value values summed across j are
   * the requirements for consumer i as a service, if the service does not
   * contain the required units reserve SubAssets cargo and guests require much
   * less than the same units of working SubAssets Resource and Staff the health
   * of the economy suffers. The health represents the service with the least
   * ability to serve, it is the weakest link.
   *
   * to interpret the table; the .002 in column1, row 1 is the amount of units
   * of life resources required for the health of the life sector
   *
   * the .002 in column 1 row2 is the amount of units of life sector required
   * for units of the struct economic sector.
   *
   * so on through the next 5 rows are units of life sector required for the
   * health of the corresponding consuming sector. The actual number of units in
   * each case is also a function of a random number, a server sector multiplier
   * for gross changes, a multiplier for the efficiency of the life service
   * center. Eventually the 7 rows are summed for the life resource service.
   * Then the sums for the 4 SubAssets are added for the cost of that service.
   *
   * The requirements of the service is subtracted from the available units in
   * the life sector. Than that remnant is divided by the requirements of the
   * life sector, the result is the health of that economic sector.
   *
   * Both the resource and staff SubAssets health are calculated. The overall
   * health is the minimum of any of the results. Health of 1 or more means no
   * increase in the cost of services Health between 0 and 1 cause all costs to
   * increase by a calculated penalty. Health of 0 or less for any sector means
   * you are dead, this economy did not have enough food, shelter etc to prevent
   * the death of all staff. The ship or planet is out of the game o
   *
   */
  static final protected double[][][] maintRequired = {
    {
      {.002, .003, .001, .002, .002, .002, .005,
        .0005, .0003, .0003, .0003, .0003, .0002, .0003, .0, .0}, //plNXWR life
      {.002, .007, .003, .003, .003, .002, .001,
        .0002, .0004, .0003, .0003, .0002, .0002, .0003, .0, .0}, // struct
      {.003, .004, .007, .005, .002, .002, .002,
        .0003, .0003, .0004, .0003, .0002, .0002, .0003, .0, .0},//energy
      {.002, .005, .004, .004, .003, .002, .002,
        .0003, .0003, .0003, .0003, .0003, .0002, .0003, .0, .0},//propel
      {.005, .001, .001, .003, .005, .002, .002,
        .0003, .0001, .0003, .0001, .0002, .0002, .0003, .0, .0},//defense
      {.002, .004, .001, .003, .003, .001, .005, .001,
        .0003, .0002, .0003, .0003, .0004, .0002, .0003, .0, .0}, // gov
      {.001, .003, .004, .003, .003, .001, .005,
        .0003, .0003, .0003, .0003, .0003, .0002, .0003, .0, .0}, //colonistServices
      {.7, 1.2, 1.3, .8, .9, 1.2, 1.1,
        1.1, 1.3, .8, .7, .9, 1.2, 1.1, .0, .0}, // staff 7
      {1.3, .8, .9, .7, 1.2, 1.3, .8,
        .9, 1.2, 1.1, 1.3, .7, .9, 1.2, 1.1, .0}, //guest 8
      {.8, .7, .7, 1.2, 1.3, 1.3, .8, .9,
        1.3, .8, .9, 1.1, 1.3, .8, .7, .0, .0}, // cargo 9
      {.7, .3, .3, .3, .4, .2, .1, .5, .5,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, // staff 7
      {.7, .3, .3, .3, .4, .2, .1,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, //guest 8
      {.1, .3, .2, .3, .3, .2, .1,
        .1, .3, .5, .1, .2, .2, .1, .0, .0}, // cargo 9
    }, {
      {.001, .001, .005, .005, .001, .002, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0}, // ship life
      {.001, .001, .001, .001, .005, .005, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.005, .005, .001, .001, .001, .005, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.005, .001, .001, .001, .005, .005, .007,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.001, .001, .002, .001, .001, .002, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.005, .001, .001, .005, .005, .001, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.001, .005, .001, .005, .005, .005, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.7, 1.2, 1.3, .8, .9, 1.2, 1.1,
        1.1, 1.3, .8, .7, .9, 1.2, 1.1, .0, .0}, // staff 7
      {1.3, .8, .9, .7, 1.2, 1.3, .8,
        .9, 1.2, 1.1, 1.3, .7, .9, 1.2, 1.1, .0}, //guest 8
      {1.3, .8, .9, .8, .7, .7, 1.2,
        .9, 1.1, 1.3, 1.3, .8, .8, .7, .0, .0}, // cargo 9
      {.7, .3, .3, .3, .4, .2, .1, .5, .5,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, // consumers
      {.7, .3, .3, .3, .4, .2, .1,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, //guest
      {.1, .3, .2, .3, .3, .2, .1,
        .1, .3, .5, .1, .2, .2, .1, .0, .0}, // cargo
    }};

  //  [sIx]
// static int[] tableRow = {0,9,7,8};
  /**
   * cost of maintenance per service per consumer include guests and cargo as
   * pseudo sources because both experience wear/death
   * [planet,ship][life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
   * [life,struct,energy,propel,defense,gov,colonist,lifeWorkers,structWorkers,energyWorkers,propelWorkers,defenseWorkers,govWorkers,colonistWorkers,
   * guests,cargo]
   */
  // deaths/wear per year
  static final protected double[][][] maintCost = {
    {
      {.002, .001, .002, .004, .001, .002, .005,
        .0003, .0001, .0008, .0001, .0002, .0002, .0008, .0, .0}, // life
      {.001, .004, .002, .005, .001, .005, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0}, // struct
      {.005, .0025, .005, .005, .002, .003, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},//energy
      {.0005, .001, .001, .001, .005, .005, .004,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},//propel
      {.001, .004, .006, .003, .004, .002, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},//defense
      {.001, .002, .005, .002, .005, .005, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0}, // gov
      {.001, .001, .001, .001, .001, .001, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0}, //colonistServices
      {.7, 1.2, 1.2, 1.1, 1.3, .8, .9,
        1.1, .7, .9, 1.3, .8, .7, .9, 1.2, 1.1, .0, .0}, // staff 7
      {1.3, .8, .9, .7, 1.2, 1.3, .8,
        .9, 1.2, 1.1, 1.3, .7, .9, 1.2, 1.1, .0}, //guest 8
      {.8, .7, 1.2, 1.3, .7, 1.3, .8, .9,
        1.3, .8, 1.3, .8, .9, 1.1, .7, .0, .0}, // cargo 9
      {.7, .3, .3, .3, .4, .2, .1, .5, .5,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, // consumers
      {.7, .3, .3, .3, .4, .2, .1,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, //guest
      {.1, .3, .2, .3, .3, .2, .1,
        .1, .3, .5, .1, .2, .2, .1, .0, .0}, // cargo
    }, {
      {.001, .001, .001, .001, .001, .005, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.001, .001, .001, .001, .001, .005, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.001, .001, .001, .001, .001, .005, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.005, .004, .006, .006, .004, .005, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.005, .004, .006, .006, .004, .005, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.005, .005, .005, .005, .005, .005, .005, .005,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.005, .005, .003, .002, .001, .001, .004,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0},
      {.7, .9, 1.2, 1.1, 1.2, 1.3, .8,
        1.1, .9, 1.2, 1.3, .8, .7, 1.1, .0, .0}, // staff 7
      {1.3, .8, .9, .7, 1.2, 1.3, .8,
        .9, 1.2, 1.1, 1.3, .7, .9, 1.2, 1.1, .0}, //guest 8
      {1.3, .8, .9, .8, .7, .7, 1.2,
        .9, 1.1, 1.3, 1.3, .8, .8, .7, .0, .0}, // cargo 9
      {.7, .3, .3, .3, .4, .2, .1, .5, .5,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, // consumers
      {.7, .3, .3, .3, .4, .2, .1,
        .7, .3, .3, .3, .4, .2, .1, .0, .0}, //guest
      {.1, .3, .2, .3, .3, .2, .1,
        .1, .3, .5, .1, .2, .2, .1, .0, .0}, // cargo
    }};

  // [planet,ship][life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
  //    [life,struct,energy,propel,defense,gov,colonist,lifeWorkers,structWorkers,energyWorkers,propelWorkers,defenseWorkers,govWorkers,colonistWorkers,  guests,cargo]
  /**
   * cost of travel for ships with guests and cargo.
   */
  static final protected double[][][] shipTravelLightyearCostsBySourcePerConsumer = {
    {
      {.001, .001, .005, .001, .005, .002, .001,
        .0002, .0009, .0002, .0009, .0002, .0002, .0003, .0, .0}, //life
      {.001, .005, .001, .005, .005, .001, .005,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, //struct
      {.001, .002, .005, .001, .005, .001, .001,
        .0003, .0002, .0003, .0003, .0003, .0002, .0003, .0, .0}, // energy
      {.001, .005, .002, .005, .001, .001, .001,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, // propel
      {.001, .002, .005, .005, .001, .002, .001,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, //defense
      {.001, .001, .005, .005, .002, .001, .002,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, // gov
      {.001, .001, .005, .005, .001, .002, .001,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, // col serv
      {.7, 1.2, .9, 1.2, 1.1, 1.3, .8,
        1.1, .7, .9, 1.3, .8, 1.2, 1.1, .0, .0}, // staff 7
      {.9, .7, 1.2, 1.3, .8, 1.3, .8,
        .9, 1.2, .9, 1.2, 1.1, 1.3, .7, 1.1, .0}, //guest 8
      {.7, 1.2, 1.3, .8, .7, 1.3, .8, .9,
        .9, 1.1, 1.3, .8, 1.3, .8, .7, .0, .0}, // cargo 9
      {.1, .1, .4, .4, .1, .2, .1, .1, .1,
        .1, .1, .4, .4, .1, .2, .1, .1, .1, .0, .0}, // consumers
      {.1, .1, .4, .4, .1, .2, .1, .1, .1,
        .1, .1, .4, .4, .1, .2, .1, .1, .1, .0, .0}, //guest
      {.1, .1, .4, .4, .1, .2, .1, .1, .1,
        .1, .1, .4, .4, .1, .2, .1, .1, .1, .0, .0}},
    {
      {.001, .001, .005, .005, .001, .002, .001,
        .0002, .0002, .0009, .0009, .0002, .0002, .0003, .0, .0}, //life
      {.001, .001, .005, .005, .001, .005, .005,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, //struct
      {.001, .002, .005, .005, .001, .001, .001,
        .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0}, // energy
      {.001, .002, .005, .005, .001, .001, .001,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, // propel
      {.001, .002, .005, .005, .001, .002, .001,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, //defense
      {.001, .001, .005, .005, .002, .001, .002,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, // gov
      {.001, .001, .005, .005, .001, .002, .001,
        .0003, .0003, .0009, .0009, .0002, .0002, .0003, .0, .0}, // col serv
      {.7, 1.2, .9, 1.2, 1.1, 1.3, .8,
        1.1, .7, .9, 1.3, .8, 1.2, 1.1, .0, .0}, // staff 7
      {.9, .7, 1.2, 1.3, .8, 1.3, .8,
        .9, 1.2, .9, 1.2, 1.1, 1.3, .7, 1.1, .0}, //guest 8
      {.7, 1.2, 1.3, .8, .7, 1.3, .8, .9,
        .9, 1.1, 1.3, .8, 1.3, .8, .7, .0, .0}, // cargo 9
      {.1, .1, .4, .4, .1, .2, .1, .1, .1,
        .1, .1, .4, .4, .1, .2, .1, .1, .1, .0, .0}, // consumers
      {.1, .1, .4, .4, .1, .2, .1, .1, .1,
        .1, .1, .4, .4, .1, .2, .1, .1, .1, .0, .0}, //guest
      {.1, .1, .4, .4, .1, .2, .1, .1, .1,
        .1, .1, .4, .4, .1, .2, .1, .1, .1, .0, .0}}};      // cargo
  // actual yearly maintenance as a fraction of requirements
  static final double availGrowthReqGrossAdj[] = {.5, .5};
  static final double staffGrowthReqGrossAdj[] = {.5, .5};

  /**
   * requirements for growth, reserves required but not used
   */
  // limits on growth, takes crowding into account, limits growth, not cost of growth
  // [planet,ship][life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
  //    [life,struct,energy,propel,defense,gov,colonist,lifeWorkers,structWorkers,energyWorkers,
  //     propelWorkers,defenseWorkers,govWorkers,colonistWorkers]
  // first planet,each resource requirement for lifeService, then structService, ...
  static final protected double[][][] resourceGrowthRequirementBySourcePerConsumer = {
    {
      {.003, .003, .003, .003, .002, .006, .003, // planet
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.4, .3, .3, .3, .4, .2, .1, .5, .5,
        .4, .3, .3, .3, .4, .2, .1, .0, .0}, // staff
      {.4, .3, .3, .3, .4, .2, .1,
        .4, .3, .3, .3, .4, .2, .1, .0, .0}, //guest
      {.1, .3, .2, .3, .3, .2, .1,
        .1, .3, .5, .1, .2, .2, .1, .0, .0}// cargo
    },
    {
      {.003, .003, .003, .003, .002, .006, .003, // ship
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.003, .003, .003, .003, .002, .006, .003,
        .003, .003, .003, .003, .002, .002, .003, .0, .0},
      {.4, .3, .3, .3, .4, .2, .1, .5, .5,
        .4, .3, .3, .3, .4, .2, .1, .0, .0}, // consumers
      {.4, .3, .3, .3, .4, .2, .1,
        .4, .3, .3, .3, .4, .2, .1, .0, .0}, //guest
      {.1, .3, .2, .3, .3, .2, .1,
        .1, .3, .5, .1, .2, .2, .1, .0, .0}}};      // cargo

  /**
   * actual costs of growth against each source by each consumer
   * [planet,ship][life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
   * [life,struct,energy,propel,defense,gov,colonist,lifeWorkers,structWorkers,energyWorkers,
   * propelWorkers,defenseWorkers,govWorkers,colonistWorkers] first planet,each
   * resource requirement for lifeService, then structService,
   */
  static protected double[][] gCostMult = {{1., 1., .9, .9}, {1., 1., .9, .9}};
  static final protected double[][][] resourceGrowthCostBySourcePerConsumer
          = // planet
          {
            {
              {.0003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.7, .3, .3, .3, .4, .2, .1, .5, .5,
                .7, .3, .3, .3, .4, .2, .1, .0, .0}, // consumers
              {.7, .3, .3, .3, .4, .2, .1,
                .7, .3, .3, .3, .4, .2, .1, .0, .0}, //guest
              {.1, .3, .2, .3, .3, .2, .1,
                .1, .3, .5, .1, .2, .2, .1, .0, .0}
            }, // cargo
            {
              {.003, .003, .003, .003, .002, .001, .003, // ship higher growth costs
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003},
              {.003, .003, .003, .003, .002, .001, .003,
                .0003, .0003, .0003, .0003, .0002, .0002, .0003, .0, .0}, //staffConsumers
              {.002, .002, .002, .002, .002, .001, .002,
                .0002, .0002, .0002, .0002, .0002, .0002, .0002, .00002, .0}, //guests
              {.002, .002, .002, .002, .002, .001, .002,
                .0002, .0002, .0002, .0002, .0002, .0002, .0002, .0, .0}} // cargo
          };
  static double groCostCargoFracWorking = 1.;
  static double groCostGuestsFracStaff = 1.;

  public static final int lrand = 100;

  static int send(String pre, int blev, String title, double... dargs) {
    if (blev <= History.mostDl) {
      EM.curEcon.hist.add(new History(pre, blev, title, dargs));

    }
    return blev;
  }

  /**
   * make a new A2Row and make new the 2 ARows from the invoking A2Row
   *
   * @param aa the input A2Row
   * @return the copy of aa including the 2 new copied ARow
   */
  static public A2Row copy(A2Row aa) {
    ARow nCargo = new ARow(aa.ec).set(aa.getARow(0));
    ARow nGuests = new ARow(aa.ec).set(aa.getARow(lsecs));
    A2Row tmp = new A2Row(nCargo, nGuests);
    return tmp;
  }

  /**
   * new an ARow if it was not instantiated save space in auxiliary AssetsYr
   * only using a few fields
   *
   * @param a ARow if it already exists
   * @return a or a new ARow
   */
  public static ARow makeNew(ARow a) {   //Assets.makeNew
    if (a != null) {
      return a;
    }
    ARow AA = new ARow(a.getEc());
    return AA;
  }

  /**
   * make a copy of the old ARow or a zero ARow if old is null
   *
   * @param old
   * @return copy of ARow old or a zero ARow if old is null
   */
  public static ARow copy(ARow old) {
    if (old == null) {
      return new ARow(EM.curEcon).zero();
    }
    else {
      return new ARow(old.getEc()).set(old);
    }

  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  static public String mf(double v) {
    if (v % 1 > E.NZERO && v % 1 < E.PZERO) {  //very close to zero
      return whole.format(v);
    }
    dFrac.setMaximumIntegerDigits(7);
    dFrac.setMinimumIntegerDigits(1);
    if (v == .0 || v == -0) {  // actual zero
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(1);

      return dFrac.format(v);
    }
    else if ((v > -999999. && v < -.001) || (v > .001 && v < 999999.)) {
      dFrac.setMinimumFractionDigits(2);
      dFrac.setMaximumFractionDigits(3);
      return dFrac.format(v);
    }
    else if ((v > -.001 && v < -.0000001) || (v > .0000001 && v < .001)) {
      dFrac.setMinimumFractionDigits(2);
      dFrac.setMaximumFractionDigits(7);
      return dFrac.format(v);
    }
    else {
      return exp.format(v);
    }
  }

  /**
   * moved to Econ test of double NaN or Infinite skip testing if not debugDouble
   *
   * @param trouble value to be tested
   * @param vs description of current situation
   * @return if debugDouble (if NaN 0, if Infinite 100.0) otherwise trouble
   */
  static double doubleTroublemoved(Double trouble, String vs) {
    Econ ec = EM.curEcon;
    if (trouble.isNaN()) {
      if (E.debugDouble) {
        Assets as = ec.as;
        int asTerm = as.term; // force possible null ec
        throw new MyErr("Not a number found" + vs + " term" + as.term + " i" + as.i + " j" + as.j + " m" + as.m + " n" + as.n);
        //  eM.doMyErr(String.format(" Not a number found, %s term%d, i%d, j%d, m%d, n%d", vs, as.term, as.i, as.j, as.m, as.n));
      }
      else {
        return 0.0;
      }
    }
    if (trouble.isInfinite()) {
      if (E.debugDouble) {
        Assets as = ec.as;
        int asTerm = as.term; // force possible null ec
        throw new MyErr("Infinite number found" + vs + " term" + as.term + " i" + as.i + " j" + as.j + " m" + as.m + " n" + as.n);
        //  eM.doMyErr(String.format(" Not a number found, %s term%d, i%d, j%d, m%d, n%d", vs, as.term, as.i, as.j, as.m, as.n));

      }
      else {
        return 100.0;
      }
    }
    return (double) trouble;
  }

  /**
   * test if a number is infinite or not a number'
   *
   * @param myD number to test
   * @param nD name of the number being tested
   */
  static public void myTestDouble(Object amyD, String nD) {
    Double myD = (Double) amyD;

    if (myD.isInfinite()) {
      myTest(true, "Found %s infinite ", nD);
    }
    else if (myD.isNaN()) {
      myTest(true, "Found %s not a number", nD);
    }
  }

  /**
   * test if a number is infinite or not a number'
   *
   * @param myD number to test
   * @param nD name of myD variable
   * @param form output form
   * @param oargs arguments for form
   */
  static public void myTestDouble(Object amyD, String nD, String form, Object... dargs) {
    Double myD = (Double) amyD;
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < dargs.length && i < v.length; i++) {
      v[i] = dargs[i];
    }
    if (myD.isInfinite()) {
      myTest(true, "Found %s = \"%s\" infinite " + form, nD, myD, v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    }
    else if (myD.isNaN()) {
      myTest(true, "Found %s = \"%s\" is not a number " + form, nD, myD, v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    }
  }

  /**
   * second test if a number is infinite or not a number'
   *
   * @param myD number to test
   * @param nD name of myD variable
   * @param form output form
   * @param oargs arguments for form
   */
  static public void myTestDouble2(Object amyD, String nD, String form, Object... dargs) {
    Double myD = (Double) amyD;
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < dargs.length && i < v.length; i++) {
      v[i] = dargs[i];
    }
    if (myD.isInfinite()) {
      myTest(true, "Found %s = \"%s\" infinite " + form, nD, myD, v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    }
    else if (myD.isNaN()) {
      myTest(true, "Found %s = \"%s\" is not a number " + form, nD, myD, v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    }
  }
  /**
   * flag of done
   *
   */
  public static boolean myTestDone = false;

  /**
   * test truth of tt, invoke a formated response
   *
   * @param tt test for true
   * @param form form for output of true tt
   * @param dargs arguments for form
   */
  static public void myTest(boolean tt, String form, Object... dargs) {
    if (tt) {
      // StringBuffer m = "Exception";
      //throw MyTestException()
      Object v[] = new Object[40];
      for (int i = 0; i < v.length; i++) {
        v[i] = 0.;
      }
      for (int i = 0; i < dargs.length && i < v.length; i++) {
        v[i] = dargs[i];
      }
      System.out.flush();
      System.out.flush();
      System.out.flush();
      System.err.flush();
      System.err.flush();
      System.err.flush();
      System.err.format((EM.curEcon == null ? "" : EM.curEcon.name) + ":" + form  + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20], v[21], v[22], v[23], v[24], v[25], v[26], v[27], v[28], v[29], v[30], v[31], v[32], v[33], v[34], v[35], v[36], v[37], v[38], v[39]);
      System.err.println(EM.andMore());
      new Throwable().printStackTrace(System.err);
      System.err.flush();
      System.out.flush();

      myTestDone = true;
      throw new MyTestException();
      // System.exit(5);
      //return 0.;
    }
  }

  public static boolean sysmsgDone = false;
  static public int dmsgs = 5900;
  static public int msgs = dmsgs;
  static public int msgcnt = 0;

  static void resetMsgs() {
    msgs = (int) (eM.econLimits3[0] * 500);
    msgcnt = 0;
  }

  static public Object sysmsg(String form, Object... dargs) {

    Object v[] = new Object[31];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.; //preset all v to 0
    }
    for (int i = 0; i < dargs.length && i < v.length; i++) {
      v[i] = dargs[i]; // move object
    }
    // StringBuffer m = "Exception";
    //throw MyTestException()
    StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
    int ss = stackTraces.length;
    //System.out.println(" length Thread.currentThread().StackTraceLength=" + ss);
    StackTraceElement aa = (ss < 4 ? stackTraces[2] : stackTraces[3]);
    StackTraceElement ab = (ss < 5 ? aa : stackTraces[4]);
    StackTraceElement ac = (ss < 6 ? aa : stackTraces[5]);
    StackTraceElement ad = (ss < 7 ? aa : stackTraces[6]);
    StackTraceElement ae = (ss < 8 ? aa : stackTraces[7]);

    String Fname = aa.getFileName();
    String Cname = aa.getClassName();
    String Mname = aa.getMethodName();
    int Fline = aa.getLineNumber();
    String Fname2 = ab.getFileName();
    int Fline2 = ab.getLineNumber();
    String Mname2 = ab.getMethodName();
    String Fname3 = ac.getFileName();
    int Fline3 = ac.getLineNumber();
    String Mname3 = ac.getMethodName();

    String aDate = ":" + new Date().toString();
    int year = EM.year;
    //System.out.println(EM.st.since() + ">>>>>>>>>sysmsg" + EM.st.since());
    // test true debugs to allow 
    if ((debugOutput || debugAssetsOut || debugEconOut || debugDoYearEndOut || debugCashFlowOut || debugTradesOut || debugFutureFund || debugThreadsOut) && !sysmsgDone) {
      msgcnt++;
      System.out.format(">>>>>>>>>sysmsg>>" + msgcnt + "<" + msgs + aDate + EM.st.since() + "Y" + EM.year + " " + EM.curEconName + ":" + Fname + "." + Fline + ";" + Cname + "." + Mname
                        + "<<<<<<<<<<\n>>>>>>>>>> " + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20], v[21], v[22], v[23], v[24], v[25], v[26], v[27], v[28], v[29], v[30]);
    }
    if (msgcnt > msgs) {
      new Throwable().printStackTrace();
      sysmsgDone = true;
      eM.doMyErr(">>>>>>>> ERR msgcnt=" + msgcnt + " exceeds limit msgs" + msgs);
    }
    // System.exit(5);
    //return 0.;

    return v[0];
  }
  static public int displayLevel = 4;
  static public int displayHistoryStartVal = 0;
  static public int displayHistoryDisplayLevelVal = 4;
  static public int displayHistoryFirstBunch = 50;
  static public int displayHistorySelectedRow = 0;
  static public int[] displayHistoryRowToM;
  static public int displayHistoryRows = 50;
  static public ArrayList<Integer> displayHistoryMHist = new ArrayList<Integer>();
  static public javax.swing.JTable displayHistoryTable;
  static public ArrayList<History> redHist1;
  static public ArrayList<History> displayHistoryHist;
  static public int displayHistoryLastM = 0;
  static public int displayHistoryStartM = 0;
  static public int displayHistoryDirection = 0;
  static final public int stC = 100;  //limit of saved env defs

  /**
   * index of used env definition entries
   */
  static public int[] alsecs = new int[lsecs], a2lsecs = new int[2 * lsecs];
  static public int[] zlsecs = new int[lsecs], z2lsecs = new int[2 * lsecs];
  static public int l2secs = 2 * lsecs;
  static public double invL2secs = 1. / l2secs;
  static public double invLsecs = 1. / lsecs;

  /**
   * for my unfavored clans, the strategicFrac increases for their unfavor
   * myclan the strategicFrac increases by ok if requests/offers > strategicFrac
   * strategicFrac = tradeFrac[pors][clan] *
   *
   * && fav[oclan][myclan]>3.?sosFrac:1.)) *(1 - barterTimes*barterMult) values
   * 0. - 5.
   */
  static boolean trade2HistOutputs = false;
  static int trade1PlanetOverrideShipGoods = 6;
  static int tradePlanetAcceptHigherOffer = 7;

  // penalty in move to earlier position if swapped across resources
  static public int[] sXSwapPenalty = {2, 2};
  // swap to same Resource
  static public int[] sSwapPenalty = {0, 0};
  // trade to the same resource in a different env use sSwapPenalty
  static public int[] sTSwapPenalty = {0, 0};
  // swapPenalty[TR,TX,TT][P, S];
  static public int[][] iSwapPenalty = {sSwapPenalty, sXSwapPenalty, sTSwapPenalty};

  static final public double[] swapTRtoRRcost = {.5, .5};
  static final public double[] swapTRtoCRcost = {.5, .5};
  static final public double[] swapTRtoRScost = {.02, .02};
  static final public double[] swapTRtoCScost = {.02, .02};
  static final public double[] swapTCtoRCcost = {.1, .1};
  static final public double[] swapTCtoCCcost = {.01, .01};
  static final public double[] swapTCtoRGcost = {.02, .02};
  static final public double[] swapTCtoCGcost = {.005, .005};
  static final public double[] swapTStoSScost = {.1, .1};
  static final public double[] swapTStoSRcost = {.5, .5};
  static final public double[] swapTStoGRcost = {.5, .5};
  static final public double[] swapTStoGScost = {.1, .1};
  static final public double[] swapTGtoSCcost = {.5, .5};
  static final public double[] swapTGtoSGcost = {.1, .1};
  static final public double[] swapTGtoGCcost = {.005, .005};
  static final public double[] swapTGtoGGcost = {.1, .1};
  static final public double[] swapXRtoRRcost = {17., 17.};
  static final public double[] swapXRtoCRcost = {18., 18.};
  static final public double[] swapXRtoRScost = {.05, .05};
  static final public double[] swapXRtoCScost = {.05, .05};
  static final public double[] swapXCtoRCcost = {18., 18.};
  static final public double[] swapXCtoCCcost = {17., 17.};
  static final public double[] swapXCtoRGcost = {.05, .05};
  static final public double[] swapXCtoCGcost = {.05, .05};
  static final public double[] swapXStoSScost = {.001, .001};
  static final public double[] swapXStoSRcost = {.005, .005};
  static final public double[] swapXStoGRcost = {.005, .005};
  static final public double[] swapXStoGScost = {.001, .001};
  static final public double[] swapXGtoSCcost = {.005, .005};
  static final public double[] swapXGtoSGcost = {.001, .001};
  static final public double[] swapXGtoGCcost = {.005, .005};
  static final public double[] swapXGtoGGcost = {.001, .001};
  static final public double[] swapRtoRRcost = {.005, .005};
  static final public double[] swapRtoCRcost = {.005, .005};
  static final public double[] swapRtoRScost = {.002, .002};
  static final public double[] swapRtoCScost = {.002, .002};
  static final public double[] swapCtoRCcost = {.001, .001};
  static final public double[] swapCtoRRcost = {.001, .001};
  static final public double[] swapCtoCCcost = {.001, .001};
  static final public double[] swapCtoRGcost = {.002, .002};
  static final public double[] swapCtoRScost = {.002, .002};
  static final public double[] swapCtoCGcost = {.002, .002};
  static final public double[] swapStoSScost = {.01, .01};
  static final public double[] swapStoSRcost = {.005, .005};
  static final public double[] swapStoGRcost = {.005, .005};
  static final public double[] swapStoGScost = {.001, .001};
  static final public double[] swapGtoSCcost = {.005, .005};
  static final public double[] swapGtoSScost = {.005, .005};
  static final public double[] swapGtoSGcost = {.001, .001};
  static final public double[] swapGtoGCcost = {.0005, .0005};
  static final public double[] swapGtoSRcost = {.0005, .0005};
  static final public double[] swapGtoGGcost = {.0001, .0001};
  /**
   * index [iEl][oEl][pors] costs of trade Rswap
   */
  static final public double[][][] swapRtradeRcost = {{swapTRtoRRcost, swapTRtoCRcost}, {swapTCtoRCcost, swapTCtoCCcost}};

  /**
   * index [iEl][oEl][pors] costs of trade Sswap
   */
  static final public double[][][] swapStradeScost = {{swapTStoSScost, swapTStoGScost}, {swapTGtoSGcost, swapTGtoGGcost}};
  /**
   * in growth phase, multiply the min positive by the following numbers this
   * should cause some resources to violate a growthReq, and cause either .INCR
   * or .XINCRH
   */

  // P=0  planet
  // S=1; ship
  static final public int W = 0; // to working
  static final public String[] Els = {"W", "R"};
  static final public int R = 1; // to reserve

  // static final public int[] oswpr = {swpMaxAvailTo,swpCargoTo};
  // static final public int[] oswps = {swpMaxStaffTo,swpGuestsTo};
  /**
   * index [iEl][oEl][pors] costs of regular Rswap
   */
  static final public double[][][] swapRregRcost = {{swapRtoRRcost, swapRtoCRcost}, {swapCtoRCcost, swapCtoCCcost}};
  /**
   * index [iEl][oEl][pors] costs of xmute (transmute) Rswap
   */
  static final public double[][][] swapRtransRcost = {{swapXRtoRRcost, swapXRtoCRcost}, {swapXCtoRCcost, swapXCtoCCcost}};

  /**
   * index [iEl][oEl][pors] costs of regular Sswap
   */
  static final public double[][][] swapSregScost = {{swapStoSScost, swapStoGScost}, {swapGtoSGcost, swapGtoGGcost}};
  /**
   * index [iEl][oEl][pors] costs of Xmute Sswap
   */
  static final public double[][][] swapStransScost = {{swapXStoSScost, swapXStoGScost}, {swapXGtoSGcost, swapXGtoGGcost}};

  static double rawHealthsSOS = .05;  // rawHealths below this cause sos
  static int maxEconHist = Econ.keepHist; // Econs later than 5 null hist to save heap space
  static int maxClrHist = 200000; // don't clear hist until it reach this number
  static final public int TR = 0;  // regular W R of same resource
  static final public int TX = 1;  // Transmute W R of different resources
  static final public int TT = 2;  // Trade  W R of different environment
  /**
   * [TR,TX,TT][iW,iR][oW,oR][P,S]
   */
  static final public double[][][][] swapRrxtcost = {swapRregRcost, swapRtransRcost, swapRtradeRcost};  // res cargo cost
  static final public double[][][][] swapSrxtcost = {swapSregScost, swapStransScost, swapStradeScost};  // staff guests cost
  static final public int CR = 0;  // class resource
  static final public int CS = 1;  // class staff
  /**
   * [CRes,CStaf][TR,TX,TT][iW,iR][oW,oR][Plan,Ship]
   */
  static final public double[][][][][] swapcosts = {swapRrxtcost, swapSrxtcost};

  static double swapResourcesAveMinMult = .3;  //use AssetsYr inline values
  static double swapSubAssetMinSwap = .01;
  static double[] minSwapIncrAveMult = {.03, .001};
  static double[] minSwapDecrAveMult = {.5, .3};
  static double[] minXferAveMult = {.03, .001};

  /**
   * The following manual variable govern the transformation of manuals to
   * knowledge No manuals are created here, only in trades, if all manuals are
   * transformed, than no increase of knowledge occurs.
   */
  static protected double[] knowledgeRequiredPerFacultyForJumping = {7., 7., 7., 7., 10., 10., 10., 10., 12., 12., 12., 12., 15., 15., 15., 15.};
  // static protected int[] jumpStageWithEffectiveFaculty = {2, 3, 2, 2};
  // static int[] allowedJumpsPerEffectiveFaculty = {15, 15, 15, 15, 12, 12, 12, 12, 10, 10, 10, 10, 8, 8, 8, 8};
  // 4 trainee, 4 engineer, 4 faculty, 4 researcher
  static final double[] sumWorkerMults = {.2, .3, .5, .9, 1.6, 3.8, 5.6, 10., 7.0, 7.0, 6.0, 5., 4., 4., 3., 3.};
  // for facultyEqv,  used to promote staff to next position
  static final double[] sumFacultyMults = {0., 0., 0., 0., 0., 0., 0.1, 0.2, .3, .4, .7, 1., .7, .5, .5, .5, .3};
  static final double[] staffPromotePerFaculty = {20., 15., 13., 12., 10., 9., 8., 7., 1., .6, .5, .3, .3, .3, .2, .2};
  // 4 trainee, 4 engineer, 4 faculty, 4 researcher
  static final double[] staffPromotePerResearcher = {0., 0., 0., 0., 0., 0., 0., 0., 1.8, 1.7, 1.6, 1.5, 1.4, 1., .8, .6};
  // use multiplier for the sum of Knowledge can be created,and researcher equiv for staff permotion above
  static final double[] sumResearchMults = {0., 0., 0., 0., 0., 0., 0., 0., 0., 0, 0., .3, .4, .5, .5, 1.};
  static final double[] sumManualToKnowledgeByStaff = {0., 0., 0., 0., .0, 0., 0.2, 0.4, 0.5, 0.7, 0.9, 1.1, 1.3, 1.5, 1.9, 2.5};
  static protected double[] deathPerYear = {.9, .97, 1.05, 1.20, .9, .97, 1.05, 1.20, .9, .97, 1.05, 1.20, .9, .97, 1.05, 1.20, .9, .97, 1.05, 1.20};
  // 4 trainee, 4 engineer, 4 faculty, 4 researcher
  // used to calculate worth of staff
  static protected double[] staffWorthBias = {.4, .5, .7, .8, .9, 1., 1.1, 1.2, 1.4, 1.6, 1.8, 2., 2.1, 2.2, 2.3, 2.5};

  static public double additionalWorkerBonusForKnowledge = .02;
  static public double aGrowthBias[] = {1.0, 1.0};
  static public double sGrowthBias[] = {1.0, 1.0};

  // [planet,ship][colonist,engineer,faculty,researcher,guest]
  static final protected double[][] upgradeToNextTypeByContext = {{40, .1, .1, .2, 0}, {2., .1, .05, .2, 0}};
// multiply table guest cost by guestBias when calculating Maint Travel Growth Req costs and worth
  static protected double guestBias = .3;
  // multity table cargo costs by cargoBias when calculating Maint Travel Growth Req cargo costs
  static protected double cargoBias = .3;
  // static final protected double calcShortBias = .3;

  /**
   * effBias efficiencyBias[pors]
   */
  static protected double knowledgeForPriority = .40; //init assign commonknowledge
  static protected double knowledgeByDefault = .60;  //init assign commonknowledge
  static protected double commonKnowledgeTradeManualFrac = .10;
  static protected double newKnowledgeTradeManualFrac = .8;
  static protected double manualTradeManualFrac = .05;
  static protected double commonKnowledgeDifTradeManualFrac = .5;

  static final protected double fatalShortfall = .3;  // of available source

  static double[][] initStaffAssignmentPerEnvir = {{.5, .4, .4, .7, .5, .4, .4, .4, .7, .2, .2, .2, .3, .05, .05, .05, .1}, {.5, .4, .4, .7, .5, .4, .4, .4, .7, .2, .2, .2, .3, .05, .05, .05, .1}};
  static protected double resourceDepreciationPercent[] = {.002, .001};
  static protected double minResourcePercentOfStaff = 1;
  static protected double maxResourcePercentOfStaff = 500;
  static protected double workingStaffPercentOfResourceConsumption = 600;
  static protected double staffDeathRate[] = {.015, .015};
  static protected double guestDeathRate[] = {.02, .02};  // ignore out of maintenance and growth
  static protected double resourceWear[] = {.02, .02};
  static protected double maxStaffPercentOfWorkingStaff = 500;
  static protected double minStaffPercentOfWorkingStaff = 5;
  static protected double nominalResourcePriorityPercent = 40;

  static double mReqMult[][] = {{1., .25, .9, .2}, {1., .2, .8, .15}};
  static double mReqEffMult = 500.;
  static double gReqMult[][] = {{1., .2, 1., .2}, {1., .2, 1., .2}};
  static double gReqEffMult = 1200.;

  static double[] initPriorityBias = {.0001, .0001};  // add to each initial priority, lowest value for yrPriority
  static public double priorityAdjustmentMultiplierFrac[] = {1., 1.};
  static double[] initStaffGrossAdjustmentPerEcon = {5., 5.};
  static double[] initGuestGrossAdjustmentPerEcon = {0.2, 0.7};
  static double[][] initStaffAssignmentPerEcon = {{.5, .4, .4, .7, .5, .4, .4, .4, .7, .2, .2, .2, .3, .05, .05, .05, .1}, {.5, .4, .4, .7, .5, .4, .4, .4, .7, .2, .2, .2, .3, .05, .05, .05, .1}};

  /**
   * limits at setting initial asset values
   */
  static double resourceMin[] = {.05, .05};
  static double resourceMult[] = {6.7, 6.7};
  static double cargoMult[] = {.005, .005};
  //3/15/15 decrease min  static double staffMin[] = {.005, .005};
  static double staffMin[] = {.0005, .0005};
  static double staffMult[] = {.1, .1};
  static double guestsMult[] = {.0005, 0.0005};

} // end of class E

