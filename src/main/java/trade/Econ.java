
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 *
 * @author Albert Steiner
 *
 * Documentation hints' {@link url} .name
 * <p>
 * @parm url comments
 * @parm name comments
 * @return returns This class is the root of assets for this planet or ship. The
 * Assets and AssetsYr classes hold the assets and history of an economy Class E
 * holds static tables and user changeable values and tables and enums Each
 * economy belongs to a clan, which can establish levels of friendship with
 * others Planaet economys are started before any ships are started. At
 * yearStart, planets in AssetsYr calculate a best tactics for this years health
 * and growth after a planets growth it is ready for trading, and then at
 * yearEnd health decides on death. Stars search for a Planet to trade with as a
 * function of distance and planet trading history. at each trade Ships receive
 * the planets trading history, update it with ship history and store it on the
 * planet again. Only x number of years of history are kept. Random numbers are
 * calculated for each economy before yearStart or planetSearch. The random
 * numbers remain constant until the start of the next year cycle.
 *
 */
public class Econ {
  
  static EM eM = EM.eM;
  static StarTrader st = EM.st;
  static E eE = EM.eE;
  Econ ec = this;
  String aPre = "&V";
  int lev = History.informationMinor9;
  int blev = History.dl;
  int blev1 = History.dl;  // for addOHist
  int blev2 = History.dl;  // for addHist
  protected String name;
  protected int clan;
  double xpos, ypos, zpos;
  double[] xyz = {xpos, ypos, zpos};
  double distanceMoved = 0;
  // neighbors from
  //Neighbor[] neighbors = new Neighbor[20];
  protected int pors;
  protected int year;  // EM.year of StarTrader
  protected int age = -1;   // age of this economy, increased by year start
  int dage = -1;          // years dead
  protected Assets as;
  protected int myEconCnt = 0;
  static int keepHist = 4;  // keep hist for myEconCnt up to 5;
  protected double trand[] = new double[E.lrand];
//  protected E D
  // sum of guest, trainees, workers, faculty, researchers with biases
  protected double percentDifficulty;
  double hiLoMult = 1.2; //difficulty mult for the life of the econ
  boolean hiLo; // hiLoMult <1.0 difficulty less, growth more(hi)
  protected double sourceReqBias;
  protected double health = 1.0;
  protected double resourcePri[] = {0., 0., 0., 0., 0., 0., 0.};
  protected double sumInitPri = 0, sumUserPri = 0;
  double tworth;
  double wealth;
  double knowledge;
  double colonists;
  double res;
  boolean alreadyTrading = false; //reset at start
  boolean didYearEnd = false;  //reset at start
  int visitedShipNext = -1;  //reset at start
  Econ[] visitedShipList = new Econ[10];
  protected int logM[] = {0, 0};  // initial hist row to be display per display level
  protected int logLev[] = {E.logDefaultLev[0], E.logDefaultLev[1]};
  protected int logLen[] = {E.logDefaultLen[0], E.logDefaultLen[1]};
  protected ArrayList<History>[] hists;
  protected ArrayList<History> hist;
//  protected ArrayList<Offer> offers = new ArrayList<Offer>();  // planet veriable
  // ArrayList<ArrayList<Offer>> planetOffers
  //                              = new ArrayList<ArrayList<Offer>>(); // ships list of planets
  ArrayList<TradeRecord> planetList = new ArrayList<TradeRecord>();

  ARow sectorPri;
  long myStartTime, myFirstBarterTime, myBarterTime,myYearEndTime,mySwapStart,mySwapDone,myEnder;
//  ArrayList<Offer> myPlanetOffers; // list of offers for this planet
  static int yearsKeep[] = {7, 7, 7, 7, 7};  // keep others offers by clan
  static int myYearsKeep[] = {12, 12, 12, 12, 12}; // keep my offers
  // int yearLeast = eM.year - yearsKeep[clan]; // oldest (least) year we keep
  // int myYearLeast = eM.year - myYearsKeep[clan]; // oldest on my list
  // [life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
  // see StarTrader.ConsumerNames
  NumberFormat dFrac = NumberFormat.getNumberInstance();
  NumberFormat whole = NumberFormat.getNumberInstance();
  NumberFormat exp = new DecimalFormat("0.#####E0");

  //E.clan myClan;
  /**
   * simple constructor
   *
   */
  public Econ() {
    hists = new ArrayList[1];
    hist = hists[0] = new ArrayList<History>();
  }

  /**
   * initialize Econ
   *
   * @param st reference StarTrader and cur in it
   * @param name economy name
   * @param clan clan membership
   * @param planetOrShip 0:planet , 1:ship
   * @param xpos
   * @param percentDifficulty difficulty for this economy
   * @param initWorth worth to be divided between cash, knowledge r c s g
   *
   */
  void init(StarTrader ast, EM aeM, String name, int clan, int econCnt, int planetOrShip,
          double xpos, double percentDifficulty, double initWorth
  ) {
    this.pors = planetOrShip;
    ec = this;
    eM = aeM;
    this.clan = clan;
    this.name = nowName = name;
    nowEc = this;
    Econ.st = ast;
    this.year = eM.year;
    this.myEconCnt = econCnt;
    
    planetList = new ArrayList<TradeRecord>();
    //double res1 = 1.0, tworth2 = 0.;
    tworth = initWorth;
    double wworth = Math.max(eM.initialWealthFrac[pors] * tworth * (eM.haveCash[0][0] > .99 ? 1.0:0.0), eM.initialWealth[pors]);

    dFrac.setMaximumFractionDigits(2);

    double sworth = (colonists = Math.max(eM.initialColonists[pors], tworth * eM.initialColonistFrac[pors]) * eM.nominalWealthPerStaff[pors]) * (1.0 + eM.initialReserve[pors]);  // 1300 + reserve by staff wealth

    double rworth = (res = Math.max(eM.initialResources[pors], tworth * eM.initialResourceFrac[pors])) * (1.0 + eM.initialReserve[pors]) * eM.nominalWealthPerResource[pors];

    knowledge = Math.max(eM.initialKnowledge[pors], tworth * eM.initialCommonKnowledgeFrac[pors]);
    wealth = Math.max(tworth - sworth - rworth - knowledge * eM.nominalWealthPerCommonKnowledge[0], wworth); // wealth now remainder
    
    System.out.println(new Date().toString() + "Pre Init year" + year + name + " clan" + clan + " econCnt=" +myEconCnt  + " worth=" +EM.mf(tworth) + " wealth=" +EM.mf(wealth) + " resources=" +EM.mf(res) + " $$ "  +EM.mf(rworth)+ " colonists=" +EM.mf(colonists)  + " $$ " +EM.mf(sworth) + " knowledge=" +EM.mf(knowledge));
    if (xpos > E.nzero) {
      this.xpos = xpos;
    }

    // now recalculate values by fractions regardless of value of initWorth
    tworth = initWorth * eM.upWorth[pors];
    double partsSum = eM.initialCommonKnowledgeFrac[pors] * eM.nominalWealthPerCommonKnowledge[0] + eM.initialResourceFrac[pors] * (1.0 + eM.initialReserve[pors]) * eM.nominalWealthPerResource[pors] + eM.initialColonistFrac[pors] * (1.0 + eM.initialReserve[pors]) * eM.nominalWealthPerStaff[pors] +(eM.initialWealthFrac[pors] * (eM.haveCash[0][0] > .9 ? 1.0:0.0) );
    double partsSumFrac = 1 / partsSum; // change to mult once instead of at each frac
    // change back to units
    knowledge = tworth * eM.initialCommonKnowledgeFrac[pors] * partsSumFrac / eM.nominalWealthPerCommonKnowledge[0];
    wealth = tworth * eM.initialWealthFrac[pors] * partsSumFrac * (eM.haveCash[0][0] > .9 ? 1.0:0.0);
    colonists = (sworth = tworth * partsSumFrac * eM.initialColonistFrac[pors] * (1.0 + eM.initialReserve[pors]))/ eM.nominalWealthPerStaff[pors];
    res = (rworth = tworth * partsSumFrac * eM.initialResourceFrac[pors] * (1.0 + eM.initialReserve[pors])) / eM.nominalWealthPerResource[pors];

    System.out.println(new Date().toString() + "Init again year" + year + (pors == E.P ? " planet " : " ship ") + name + " clan" + clan + " econCnt=" + myEconCnt + " worth=" +EM.mf(tworth) + " wealth=" +EM.mf(wealth) + " resources=" +EM.mf(res) + " $$ " +EM.mf(rworth) +  " colonists=" +EM.mf(colonists) + " $$ " +EM.mf(sworth)+ " knowledge=" +EM.mf(knowledge));
    // do not set a new position/change position if Assets was already instanted
    if (as == null) {
      //    System.out.println("137 start as == null");
      // if this.xpos > E.nzero than set to the next position
      if (E.newPlanetPosition[1] >= E.newPlanetPosition[0] && E.newPlanetPosition[2] >= E.newPlanetPosition[1]) {
        this.xpos = (E.newPlanetPosition[0] += 2.7) - 1.2 + Math.random() * 2.7;
        E.newPlanetPosition[1] = E.newPlanetPosition[2] = -2.7; // back to initial value
      } else {
        this.xpos = E.newPlanetPosition[0] - 1.2 + Math.random() * 2.7;
      }
      if (E.newPlanetPosition[2] >= E.newPlanetPosition[1]) {
        this.ypos = (E.newPlanetPosition[1] += 2.7) - 1.2 + Math.random() * 2.7;
        E.newPlanetPosition[2] = -2.7; // back to initial value
      } else {
        this.ypos = E.newPlanetPosition[1] - 1.2 + Math.random() * 2.7;
      }
      this.zpos = (E.newPlanetPosition[2] += 2.7) - 1.2 + Math.random() * 2.7;
    } else {
      System.out.println( " Econ.init mid year" + eM.year + " " + (new Date().getTime() - EM.doYearTime) + " as != null, using a previous location. xpos" + this.xpos + ", ypos" + this.ypos + ", zpos" + this.zpos);
    }
    System.out.println(" Econ.init mid year" + eM.year + " " + (new Date().getTime() - EM.doYearTime) + " xpos=" + this.xpos + " ypos=" + this.ypos + " zpos=" + this.zpos);

    hist.add(new History(20, "Start", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
    this.percentDifficulty = percentDifficulty;
    dFrac.setMaximumFractionDigits(2);
    whole.setMaximumFractionDigits(0);

    trand = newRand(trand);
    int[] apris = {0, 1, 2, 3, 4, 5, 6};
    Set<Integer> pris = new HashSet<Integer>(8);
    /**
     * populate pris with 0-6,for ensuring all 7 sectors have a priority
     */
    for (int m = 0; m < E.LSECS; m++) {
      pris.add(m);
      resourcePri[m] = 0.;
    }
    int sec = -1;
    int a22a = 0;
    double remainingPri = 100;
    double paddition[] = new double[7];
    int secs[] = {9, 9, 9, 9, 9, 9, 9};
    // set priority values
 
    String a11a= "<<<a<<<Econ.init.sec  new Integer[0].length=" + (new Integer[0]).length + "::";
    String a11b= "<<<b<<<Econ.init.pris=";
    String a11c= "<<<c<<<Econ.init.resourcePri=";
    String a11d= ">>>>>>Econ.init.sectorPriority=";
    //{23, 21, 2, 3, 5, 6, 7}
    //see EM.prorityMultMult and EM.midPrioritySetMult and EM.mPrioritySetMult
    // priorities are divided y priorityMid , settings multMult are divided by EM.midPrioritySetMult
    double priorityMid = 100./7.; //the fixed average per financial resource
    // indexes pors lower mid,more than higher mid,more
    double[][] priorityLims = {{2.0,1.5, 22.,26.},{2.5,3.0,18.,21.}};//min 1.5*.5=.75, max 21*1.5=31.5
    double difPriLims[][] = {{priorityMid - priorityLims[0][0],priorityLims[0][0] - priorityLims[0][1],priorityLims[0][3]-  priorityMid ,priorityLims[0][3] - priorityLims[0][2]},{priorityMid -  priorityLims[1][0],priorityLims[1][3] - priorityLims[1][2],priorityLims[1][3]-  priorityMid ,priorityLims[1][3] - priorityLims[1][2]}}  ;//pors, low-hi

    double difSetVals = (EM.mPrioritySetMult[pors][0] - EM.mPrioritySetMult[pors][1]);
    double fracMult = (EM.prioritySetMult[pors][0]-EM.mPrioritySetMult[pors][0])/difSetVals;
    double difPriority  = priorityMid;
    double midAdd = cRand(22+0,.7) * priorityMid;
    
    for (int m = 0; m < E.LSECS; m++) { //0 - 5
      Integer[] prar = pris.toArray(new Integer[0]);// an array of 8
      secs[m] = sec = (int) (prar[(a22a = (int)( (Math.random() * 500) % (pris.size())))]); //pick iX
      
      if(E.errEconInit){ //print??
      a11a += " m" + m + ", a22a" + a22a + ", sec" + sec + ", ";
      a11b += " m" + m + " pris.size()" + pris.size()+ ", ";
      }

     // paddition[m] = Math.random() * eM.userPriorityRanMult[m][pors][clan];// 7,6,2,2,2,3,3.5
     // paddition[m] = cRand(22,1.) * priorityMult[pors][m];
     /*
    double[][] priorityLims = {{2.0,1.5, 22.,26.},{2.5,3.0,18.,21.}};//min 1.5*.5=.75, max 21*1.5=31.5
    double difPriLims[][] = {{priorityMid - priorityLims[0][0],priorityLims[0][0] - priorityLims[0][1],priorityLims[0][3]-  priorityMid ,priorityLims[0][3] - priorityLims[0][2]},{priorityMid -  priorityLims[1][0],priorityLims[1][3] - priorityLims[1][2],priorityLims[1][3]-  priorityMid ,priorityLims[1][3] - priorityLims[1][2]}}  ;//pors, low-hi

    double difSetVals = (EM.mPrioritySetMult[pors][0] - EM.mPrioritySetMult[pors][1]);
    double fracMult = (EM.prioritySetMult[pors][0]-EM.mPrioritySetMult[pors][0])/difSetVals;
    double difPriority  = priorityMid; 
     */
      
       if(m < 2 && EM.prioritySetMult[pors][0] <= EM.midPrioritySetMult[pors]){ // low pmm<midpmm 14 - 
         difSetVals = (EM.midPrioritySetMult[pors] -  EM.mPrioritySetMult[pors][0]);// low sets dif
         fracMult = (EM.prioritySetMult[pors][0]-EM.mPrioritySetMult[pors][0])/difSetVals; // low sets <Mult <frac
         //slide:value:fracMult:pa:*rand 
        // settingss at 0.0:1.0:0.:14 :should be 14. or priorityMid * rand
        //at 75:2.5:1.0:2.0  result should be priorityLims[pors][1] * rand
        //at 85:2.7:.4:1.8 should be  (priorityLims[pors][1] - difPriLims[pors][1] + .4) * rand
        //at 95:2.9:.8:1.6 should be  priorityLims[pors][1] - difPriLims[pors][1] * rand + .8*
        resourcePri[sec] = (paddition[m] = priorityMid - difPriLims[pors][0]* fracMult) * cRand(22+m,.7); //larger set, smaller pri
       // resourcePri[sec] = resourcePri[sec] > 0.7? resourcePri[sec] : 0.7;
       } else if(m < 2){  // high zone low priority
         difSetVals = (EM.mPrioritySetMult[pors][1] - EM.midPrioritySetMult[pors] );// high zone mid < high
         fracMult = (EM.prioritySetMult[pors][0]- EM.midPrioritySetMult[pors])/difSetVals; // // high sets <Mult <frac
         // calc change from 100/7 prioityMid
         resourcePri[sec] = (paddition[m] = priorityLims[pors][0] - difPriLims[pors][1]* fracMult) * cRand(22+m,.7) ;
         //resourcePri[sec] = (lowPriority * paddition[m]);
     } else if(m < 4 ){ // max midPriority to supermax
        difSetVals = (EM.mPrioritySetMult[pors][0] - EM.midPrioritySetMult[pors]);// low dif
         fracMult = (EM.prioritySetMult[pors][0]-EM.mPrioritySetMult[pors][0])/difSetVals; // addition
        // at 1.0 rP should be 14. or priorityMid * rand
        //at 2.5 result should be dpriorityLims[pors][2] * rand
        //at 2.7 should be more * 1.2
        //at 2.9 should be moe less * 1.4
        resourcePri[sec] = (paddition[m] = priorityMid - difPriLims[pors][0]* fracMult) * cRand(22+m,.7) ;
      } else if(m < E.LSECS - 1){ // 4,5
        fracMult = 0.;
        resourcePri[sec] = (paddition[m] = remainingPri /(E.LSECS - m)) * cRand(22+m,.7) ;
      } else { // 6  the rest
        resourcePri[sec] = (paddition[m] = remainingPri);
        fracMult = 0.;
      }
       resourcePri[sec] = resourcePri[sec] > 0.3 ? resourcePri[sec] : 0.3;
      
      if(E.errEconInit){
      a11c += " m" + m + ", sec" + sec + " pa" + EM.mf(paddition[m])+ ", fm" + EM.mf(fracMult) + ", rp" + EM.mf(resourcePri[sec]) + " :::::\n";
      }
      assert resourcePri[sec] > 0.3: "!!!!!!!!Econ.init.setPiorities Error psm=" +EM.mf(EM.prioritySetMult[pors][0]) + a11a + "<<<a<<\n" + a11b + "<<<b<<\n" + a11c + "<<<c<<\n" + a11d + "<<<d<<";
      remainingPri -= resourcePri[sec]; // reduce available pri by this pri
      pris.remove(sec);
    }// for sec
  
    hiLoMult = cRand(38,EM.hiLoMult[pors][clan]);
    // set the last sec to what is left from 100.
   // resourcePri[(int) pris.toArray()[0]] = remainingPri;
    as = new Assets(); //instantiacte new Assets even if we reused econ
    //  and before instantiating any ARow or A6Rowa
    sectorPri = new ARow(this);
        
    for (int i = 0; i < resourcePri.length; i++) {
      sectorPri.set(i, resourcePri[i]);
      if(E.errEconInit){
      a11d += EM.mf(sectorPri.get(i)) + " :";
      }
    }
    if(E.errEconInit){
        System.err.print(a11a + "<<<a<<\n" + a11b + "<<<b<<\n" + a11c + "<<<c<<\n" + a11d + "<<<d<<");
    }
      // sumInitPri += resourcePri[i];
      // sumUserPri += E.userPriorityAdjustment[planetOrShip][clan][i];

    // sectorPri.divby(100. / sectorPri.sum());
    //System.out.println(new Date().toString() + "Econ.init 211 before new Assets");
    // throw away any previous Assets, the new one will be alive not dead.
    //  as = new Assets(this, st, name, clan, planetOrShip, hist, wealth, resourcePri, res, colonists, knowledge, percentDifficulty, trand);

    //   System.out.println("Econ.init 200 did new Assets");
    as.assetsInit(myEconCnt, this, st, eM, name, clan, planetOrShip, hist, tworth, wealth, sectorPri, res, colonists, knowledge, percentDifficulty, trand);
    System.out.println(" Econ.init end year" + eM.year + " " + (new Date().getTime() - eM.doYearTime) + "did assetsInit, wealth=" + EM.mf(wealth) + ", tworth=" + EM.mf(tworth));
    //  as.calcEfficiency();
  }

  Econ newCopy(EM oldEM, EM newEM, E aE, StarTrader ast) {
    Econ rtnEcon = new Econ();
    // lots more
    // Assets
    return rtnEcon;
  }

  double getXyz(int p) {
    return xyz[p];
  }

  /**
   * return value of current loop n
   *
   * @return
   */
  int getN() {
    return as.getN();
  }

  String getName() {
    return name;
  }

  /**
   * get how long Econ existed
   *
   * @return
   */
  int getAge() {
    return age;
  }

  /**
   * get how long Econ has been dead
   *
   * @return years dead
   */
  int getDAge() {
    return dage;
  }

  /**
   * return reference to Goods, force calculation of a valid goods
   *
   * @return tradingGoods value of goods to be traded and requested
   */
  A2Row getTradingGoods() {
    return as.getTradingGoods();
  }

  /**
   * get nominal worth of critical sectors of the offered trade force
   * calculation of a valid goods and worth
   *
   * @return nominal worth of critical sectors in offered trade
  
  double getTradingWorth() {
    return as.getTradingWorth();
  }
 */
  /**
   * get the number of successful trades this year
   *
   * @return number of successful trades
   */
  int getTradedSuccessTrades() {
    return as.getTradedSuccessTrades();
  }

  /**
   * get the number of trades tried this year
   *
   * @return the number of trades tried this year
   */
  int getTradedShipsTried() {
    int jjj = as.getTradedShipsTried();
    return as.getTradedShipsTried();
  }

  /**
   * decide if this planet is available for another visit to attempt a trade
   * Check the number of tradedShipsTried against several requirements. try to
   * ensure that new ships and planets are candidates for a trade. but pay
   * attention to the settings for this clan to not use more ships than the
   * number they build. There are several rounds trying to find planets to
   * trade, each round accepting more planets. check against:<ul><li>ships per
   * planet--visited ships per visited planets</li>
   * <li>clan ships per clan planets -- visited clan ships per visited clan
   * planets</li>
   * <li>clan ships per all planets -- clan ship visits per all visited
   * planets</li></ul>
   *
   * @param round 0-4 which round, increase options as round increases
   *
   * @return true if planet can have a visit to trade false planets are not
   * save, true results mean save this planet again in the list of tradable
   * planets. a planet may appear for each round.
   */
  boolean planetCanTrade(int round) {
    boolean go = canDoAnotherBarter(); // can go to trade
    go &= pors > 0; // any ship trades
    boolean go1 = EM.porsCnt[E.P] < 3;  //allow go beginnning of game
    if (E.debugCanTrade && (go || go1)) {
      E.sysmsg("in planetCanTrade " + (go ? "yes to ship" : "maybe") + (go1 ? "yes in firstYear" : "check more"));
    }
    if ((go || go1)) {
      return true;
    }
    boolean ret = false;
    int myVisits = getTradedShipsTried();
    double planetsVisitedPerEcon = EM.visitedCnt == 0 ? 0.0 : EM.porsVisited[E.P] / EM.visitedCnt;

    // double shipsVisitedPerPlanetVisited = EM.porsVisited[E.P] == 0? 0
    double planetsGoalFrac = 1.0 - eM.gameShipFrac[E.P];
    // increase allowed as round increases
    go = planetsVisitedPerEcon < (planetsGoalFrac * 1.1 + planetsGoalFrac * 0.25 * round);  // allow a little extra planets
    if (!go) {
      if (E.debugCanTrade) {
           System.out.println(name + " Not planetCanTrade planetsVisitedPerEcon=" + planetsVisitedPerEcon  + " round=" + round + " planetsFrac *1.1=" + mf(planetsGoalFrac *1.1  + planetsGoalFrac * 0.25 * round));
        return go;  // exit false
      }
    } else {
      if (E.debugCanTrade) {
           System.out.println(name + " planetCanTrade planetsVisitedPerEcon=" + planetsVisitedPerEcon  + " planetsFrac *1.1=" + mf(planetsGoalFrac *1.1  + planetsGoalFrac * 0.25 * round));
      }
    }

    // next test
    //  double shipsPerPlanets = EM.shipsPerPlanets[pors][clan];
    // go = myVisits < (int)Math.floor(shipsPerPlanets  - .7  + planetsFrac * 0.25 * round);
    if (E.debugCanTrade && !go) {
      //   E.sysmsg("in planetCanTrade myVisits=" + myVisits + "
    }

    /*
     double[] gameShipFrac = {.501, .498};
  static double[][] mGameShipFrac = {{.35, .65}, {.35, .65}};
  double[][] clanShipFrac = {{.501, .501, .501, .501, .6}, {.498, .498, .498, .498, .6}}; // .3-.7 clan choice of clan ships /econs
  static double[][] mClanShipFrac = {{.3, .7}, {.3, .7}};
  double[][] clanAllShipFrac = {{.501, .501, .501, .501, .501}, {.5, .5, .5, .5, .5}};
     */
 /*
    double visitedShipsPerPlanets = EM.porsVisited[E.P] == 0?0.0:EM.porsVisited[E.S]/EM.porsVisited[E.P];
    if(visitedShipsPerPlanets > shipsPerPlanets) return false;
    //double clanShipsPerClanPlanets = EM.porsClanCnt[E.S][clan]/EM.porsClanCnt[E.P][clan];
    double clanShipsPerClanPlanets = EM.porsClanCnt[E.S][clan] == 0?0.0:EM.porsClanCnt[E.S][clan]/EM.porsClanCnt[E.P][clan];
    if(myVisits > Math.floor(clanShipsPerClanPlanets)) return false;
    //double visitedClanShipsPerClanPlanets2 = EM.porsClanVisited[E.S][clan] /EM.porsClanVisited[E.P][clan];
    double visitedClanShipsPerClanPlanets = EM.porsClanVisited[E.S][clan] == 0?0.0:EM.porsClanVisited[E.S][clan] /EM.porsClanVisited[E.S][clan];

    if(visitedClanShipsPerClanPlanets > clanShipsPerClanPlanets) return false;
    return true; //OK past all limits
     */
    return true; //OK past all limits
  }

  ;
  ARow getNewKnowledge() {
    return as.getNewKnowledge();
  }

  ;
  ARow getCommonKnowledge() {
    return as.getCommonKnowledge();
  }

  /**
   * get the count of manuals
   *
   * @return manuals count
   */
  ARow getManuals() {
    return as.getManuals();
  }

  /**
   * getSOS value
   *
   * @return true if SOS
   */
  boolean getSOS() {
    return as.getSOS();
  }

  /**
   * get the number of trades started this year
   *
   * @return as.getYrTradesStarted()
   */
  int getYrTradesStarted() {
    return as.getYrTradesStarted();
  }

  /**
   * generate an array of random numbers using . E.gameRandomFrac and
   * E.clanRisk[pors][clan] if E.gameRandomFrac == 0, always trand[] to 1. noop
   * otherwise set to a random number between .1 &lt; random &lt; 1.9
   *
   * @param trand this is actually ignored
   *
   * @return a new trand of E.lrand length
   */
  protected double[] newRand(double[] trand) {
    trand = new double[E.lrand];
    // make range (0->.7 + 0->1*0->.5) = (0->1.2)
    //rMult = (eM.randFrac[pors][0] + eM.clanRisk[pors][clan] * eM.gameClanRiskMult[pors][0]);
    rMult = eM.randFrac[pors][0] + eM.clanRisk[pors][clan] ;
    rMult = Math.max(eM.randMin, Math.min(eM.randMax, rMult));  // set max multiplier,  .001 < random < 1.9 
  //  rCent = rMult + eM.randMin;

    for (int ii = 0; ii < E.lrand; ii++) {
      if (eM.randFrac[pors][0] == 0.0) {// if 0, set all trand values 1
        trand[ii] = 1.;
        double aaaa = trand[1];
      } else {
        double variance = rMult  * (Math.random() - .5); //- .5 *rmult < variance < +.5*rmult
        double arand = Math.max(eM.randMin, Math.min(eM.randMax,variance+ rCent)); // randMin < rand < randMax
        trand[ii] = arand; // centered around 1
      }
      double aaac = trand[0];
    }
    return trand;
  }
  double rMult = 0.;
  double rCent = 1.;

  /**
   * get a preassigned random value at randx % rand, reduce randomicity by mRand ;
   * 1.0
   *
   * @param randx index folded into the length of array trand so for length=50
   * randx 55 = 5
   * @param mRand multiplier %lt; 1.0 reducing the difference from 1.0 of the
   * random value
   * @return a random number centered around 1.0, possibly reduced by rMult
   */
  protected double cRand(int randx, double rMult) {
    if (eM.randFrac[pors][0] <= E.pzero) {  // not yet used
      return 1.;
    }
    int ix = randx % trand.length;
  // double myCent = rCent * rMult;
  //  double myRand = trand[ix] * rMult; // now centered around myCent
    double dif = rCent -trand[ix];
    double dif2 = dif * rMult;
    double rand2 = rCent - dif2;  // somewhat less dif, larger rand
    double uRand = Math.max(eM.randMin, Math.min(eM.randMax, rand2)); // randMin < rand < randMax
    return uRand;
  } // cRand

  /**
   * get a preassigned random value at randx, reduce randomicity by mRand %lt;
   * 1.0
   *
   * @param randx index folded into the length of array trand so for length=50
   * randx 55 = 5
   * @param mRand multiplier %lt; 1.0 reducing the difference from 1.0 of the
   * random value
   * @return a random number centered around 1.0, possibly reduced by rMult
   */
  protected double doRand(int randx, double rMult) {
    int ix = randx % trand.length;
    double myCent = rCent * rMult;
    double myRand = trand[ix] * rMult; // now centered around myCent
    double uRand = Math.max(eM.randMin, Math.min(eM.randMax, myRand + (1.0 - myCent))); // randMin < rand < randMax
    return uRand;
  } // cRand

  /**
   * get the rand value
   *
   * @param trand rand array
   * @param randIx index into rand array of values
   * @return
   */
  protected double cRand(double[] trand, int randIx) {
    return cRand(randIx, 1.0);
  } // cRand

  /**
   *
   * @return
   */
  protected double getHealth() {
    return as.getHealth();
  }

  /** get the multiplier of difficulty generated at Econ.init
   * 
   * @return value of hiLoMult;
   */
  double getHiLoMult(){ return hiLoMult; };
  
  /** get the Hi true when the HiLoMult will increase the worth
   * of each year by decreasing the difficulty
   * @return true if hiLoMult will increase growth by decreasing difficulty
   */
  boolean getHiLo() {return hiLoMult < 1.0;};
  /**
   * get worth of all assets from Assets
   *
   * @return total asset worth
   */
  protected double getWorth() {
    return as.getWorth();
  }

  /**
   * get die flag from Assets
   *
   * @return die flag
   */
  protected boolean getDie() {
    return as.getDie();
  }

  /**
   * get guest ARow from Assets
   *
   * @return guest ARow
   */
  ARow getGuests() {
    return as.getGuests();
  }

  /**
   * get guest grades from Assets
   *
   * @return guest grades
   */
  double[][] getGuestGrades() {
    return as.getGuestGrades();
  }

  ARow getCargo() {
    return as.getCargo();
  }

  int getPors() {
    return pors;
  }

  /** get Econ Clan
   * 
   * @return claln number
   */
  int getClan() {
    return clan;
  }

  /** get the color letter for Econ clan
   * 
   * @return color letter
   */
  String getColor() { return E.clanLetter[clan];}
  double addCash(double cash) {
    return as.addCash(cash);
  }

  protected ArrayList<History> getHist() {
    return hists[0];
  }

  /**
   * get the number of ships this planet traded with this year
   *
   * @return
   */
  int getShipOrdinal() {
    return as.getShipOrdinal();
  }

  /**
   * start the year for this economy. This includes all of the bartering, Assume
   * that ships have done select planet first, gotten light years
   *
   * @param lightYears lightYearsTraveled for a ship
   */
  protected void yearStart(double lightYears) {
    age++; // move -1 to 0 for the first year
    // age the hists file, move 4->5, 3->4, 2->3, 1->2, new 1
    // except for the first year, or if the env is dead
    year = eM.year;
    myClearHist = false;
    didYearEnd = false;
    nowName = name;
    nowEc = this;
    myStartTime = new Date().getTime();
    if (!as.getDie()) {
      if (clearHist()) {
        hist.clear();
      } else if (age > 0) { // keep the initialization hist
        // move hists up, keep a few
        for (int i = hists.length - 1; i > 0; i--) {
          if (hists[i - 1] != null) {
            hists[i] = hists[i - 1];
          }

        }

        hist = hists[0] = new ArrayList<History>(); // wipe out previous hist
        E.hist = hist; // save for later display
        if (this == eM.logEnvirn[0]) {
          eM.hists[0] = hist;
        } else if (this == eM.logEnvirn[1]) {
          eM.hists[1] = hist;
        }
        if (hists.length > 1) { //no valid only 1 hist
          hist.add(new History(2, "restart year=" + eM.year + " age=" + age, ">>>>>>>>>", "h0=" + wh(hists[0].size()), "h1=" + wh(hists[1].size()), "h2=" + wh(hists[2].size()), "h3=" + wh(hists[3].size()), "h4=" + wh(hists[4].size()), "<<<<<<<<<<"));
          int n2 = 0, n3 = 0;
          for (History hh : hists[1]) {
            int ll = hh.level;
            if (ll < 3) {
              hists[0].add(hh);
              n2++;
            } else {
              n3++;
            }
          }
          hist.add(new History(2, "copy lines=" + n2 + " skp=" + n3, ">>>>>>>>>", "h0=" + wh(hists[0].size()), "h1=" + wh(hists[1].size()), "h2=" + wh(hists[2].size()), "h3=" + wh(hists[3].size()), "h4=" + wh(hists[4].size()), "<<<<<<<<<<"));
          hist.add(new History(4, "after copy=" + n2 + " n=" + n3, ">>>>>>>>>", "h0=" + wh(hists[0].size()), "h1=" + wh(hists[1].size()), "h2=" + wh(hists[2].size()), "h3=" + wh(hists[3].size()), "h4=" + wh(hists[4].size()), "<<<<<<<<<<"));
        }
      }
      logLev[0] = E.logDefaultLev[0];
      logLev[1] = E.logDefaultLev[1];
      logLen[0] = E.logDefaultLen[0];
      logLen[1] = E.logDefaultLen[1];
    }
    trand = newRand(trand);  // generate the random array
    alreadyTrading = false; //reset at start
    didYearEnd = false;  //reset at start
    visitedShipNext = -1;  //reset at start
    as.yearStart(trand, hist);
    ArrayList<History> yy = hist;
    ArrayList<History> yz = hists[0];
  }
  /**
   * clear hist if this economy is %ge; keepHist and hist %ge; 20
   *
   * @return true if hist is not to be kept;
   */
  boolean saveHist = false;
  boolean myClearHist = false;  // set to false at yearStart
  boolean clearHist() {
    if (saveHist) {
      return false;
    }
    if (myClearHist) {
       if (hist.size() > 200) {
        hist.clear();
      }
      return true;
    }
    int iKeepMax = eM.keepHistsByYear.length - 1;
    int iXKeep = (eM.year > iKeepMax ? iKeepMax : eM.year);
  //  E.myTest(iXKeep > 2, "iXKeep=%d > 2, eM.year=%d, iKeepMax=%d, keepHistsByYear.lenth=%d", iXKeep, eM.year, iKeepMax, eM.keepHistsByYear.length);
    if (myEconCnt >= eM.keepHistsByYear[(eM.year > iKeepMax ? iKeepMax : eM.year)]) {
      if (hist.size() > 20) {
        hist.clear();
      }
      return myClearHist = true;
    }
    return false;
  }

 


  
  static int getThreadCnt(){ 
   int tCnts;
   synchronized (threadCnt) {
          tCnts = threadCnt[0];
        }
    return tCnts;
  }

  int yyyee1 = 0, yyyee2 = 0, yyyee3 = 0, yyyee4 = 0;

  /**
   * pass yearEnd on to trade.Assets But only do it once a year Ignore a second
   * or more call to yearEnd in a given year
   */
  protected void yearEnd() {
    visitedShipNext = -1; // ignore everything in the list
    nowName = name;
    nowEc = this;
    myYearEndTime = new Date().getTime();
    try {
      synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    as.yearEnd();
   synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    EM.wasHere3 = "after as.yearEnd yyyee1=" + yyyee1++;
    if (as.getDie()) {
      dage++;
      synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    }
    EM.wasHere3 = "after as.getDie() yyyee2=" + yyyee2++;
    if (clearHist()) {
      hist.clear(); // wipe out previous hist
    }
   synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    EM.wasHere3 = "at econ.yearEnd end yyyee3=" + yyyee3++;
    myClearHist = false;
    didYearEnd = false;
    nowName = name;
    nowEc = this;
    synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    } catch (Exception ex) {
      EM.flushes();
      System.err.println(Econ.nowName + st.since() + " " + Econ.nowThread + "Exception " + ex.toString() + " message=" + ex.getMessage() + " " + EM.andMore());
      ex.printStackTrace(System.err);
      EM.flushes();
      st.setFatalError();
    }
  } //yearEnd

  /**
   * add a line of History to ohist unless clearHist(), hh == null, hh.level >
   * blev1
   *
   * @param oHist name of hist file
   * @param hh result of new History(...)
   */
  void addOHist(ArrayList<History> oHist, History hh) {
    // do nothing if hist is being cleared or the hist level is > bLev highest allowed level
    if (clearHist() || hh == null || hh.level > blev1) {
      return;
    }
    oHist.add(hh);

  }

  /**
   * add a line of History to ohist unless clearHist(), hh == null, hh.level >
   * blev2
   *
   * @param oHist name of hist file
   * @param hh result of new History(...)
   */
  void addHist(ArrayList<History> hist, History hh) {
    // do nothing if hist is being cleared or the hist level is > bLev highest allowed level
    if (clearHist() || hh == null || hh.level > blev2) {
      return;
    }
    hist.add(hh);

  }

  /**
   * add a line of History to ohist unless clearHist(), hh == null, hh.level >
   * bLev
   *
   * @param oHist name of hist file
   * @param bLev do not add line if hh.level > bLev
   * @param hh result of new History(...)
   */
  void addHist(ArrayList<History> hist, int bLev, History hh) {
    // do nothing if hist is being cleared or the hist level is > bLev highest allowed level
    if (clearHist() || hh == null || hh.level > bLev) {
      int bb = hh.level;
      return;
    }
    hist.add(hh);

  }

  /**
   * obsolete selectPlanetNot
   *
   * @param wilda
   * @return
   */
  Econ selectPlanetnot(ArrayList<Econ> wilda) {
    String wildS = "in selectPlanet for:" + name + " ";
    int n = 0, r = -1;
    for (Econ ww : wilda) {
      wildS += (n++ > 0 ? ", " : "");
      wildS += " " + ww.name + "@" + calcLY(this, ww);
    }
    if (n > 0) {
      double wildar = Math.random() * 5.3 % wilda.size();
      r = (int) Math.floor(wildar);
      wildS += " selected:" + mf(wildar) + " :" + wilda.get(r).name;
    }

    Econ ret = null;
    if (n > 0) {
      E.sysmsg(wildS);
      sStartTrade(this, ret = wilda.get(r));

    } else {
      E.sysmsg("no planet available to trade");
    }
    return ret;
  }

  /*
   public static void main(String[] args) {
        final Friend alphonse =
            new Friend("Alphonse");
        final Friend gaston =
            new Friend("Gaston");
        new Thread(new Runnable() {
            public void run() { alphonse.bow(gaston); }
        }).start();
        new Thread(new Runnable() {
            public void run() { gaston.bow(alphonse); }
        }).start();
   */
  /**
   * select planet from the list of planet Econs in wilda
   *
   * @param wilda an array of econs
   * @param wLen the number of valid entries in wilda
   * @return
   */
  Econ selectPlanet(Econ[] wilda, int wLen) {
    TradePriority[] tPriority = new TradePriority[wLen];
    String[] sPriority = new String[wLen];
    if (E.debugDisplayTrade) {
      System.out.print("in econ.selectPlanet+" + name + ", wLen=" + wLen + ", planets=");
      for (int ii = 0; ii < wLen; ii++) {
        System.out.print(" " + wilda[ii].name);
      }
      System.out.println(" ");
    }
    Econ ret;
    A2Row tradeStrategicVars = as.getTradeStrategicVars();
    double sumTrade1YearTravelMaintCosts = as.getSumTrade1YearTravelMaintCosts();
    A2Row tradeGoodsNeeds = as.getTradeGoodsNeeds();
    int[] topStratSectors = {tradeStrategicVars.curMaxIx(0), tradeStrategicVars.curMaxIx(0), tradeStrategicVars.curMaxIx(0)};
    double lYears = 0.;
    String wildS = "___________in selectPlanet for:" + name + " ";
    int n = 0, r = -5, pSize = -2;
    // establish the parallel TradePriority arrays;
    for (n = 0; n < wLen; n++) {
      Econ ww = wilda[n];  // wilda is the list of Econs
      tPriority[n] = new TradePriority(ww, topStratSectors, sumTrade1YearTravelMaintCosts, (calcLY(this, ww)));
      sPriority[n] = ww.getName();
      wildS += (n > 0 ? ", " : "");
      wildS += " " + ww.name + "@" + (lYears = calcLY(this, ww));
    }
    if (n > 0) {
      if ((pSize = planetList.size()) > 0) { // if some TradeRecord s
        // scan the planetList of TradeRecord
        for (int pl = 0; pl < pSize; pl++) {
          TradeRecord tr = planetList.get(pl);
          // String trName = tr.getName();
          Econ trCn = tr.cn;
          // int nTradePriority = -3;
          // look for a tradePriority Econ in wilda, and update parallel tPriority 
          for (int nn = 0; nn < wLen; nn++) {
            if (wilda[nn] == trCn) {
              tPriority[nn].updateValues(tr);
              nn = wLen; // end loop
            } // some TradeRecords are ignored
          }// nn
        }// pl to next TradeRecord
        double tPri = -999., aPri = 0.;; // top priority found
        int nPri = -10; // count of top priority
        if (E.debugDisplayTrade) {
          System.out.print("~~~~~~~~~selecting highest pri planet=");
        }
        for (int nn = 0; nn < wLen; nn++) {
          aPri = tPriority[nn].getPriority();
          if (E.debugDisplayTrade) {
            System.out.print(" " + wilda[nn].name + "=" + EM.mf(aPri));
          }
          if (aPri > tPri) {
            tPri = aPri;
            nPri = nn;
            System.out.print(" raise pri=" + EM.mf(tPri));
          }
        }
        if (E.debugDisplayTrade) {
          if (nPri > -1) {
            System.out.println(": select " + wilda[nPri].getName());
          } else {
            System.out.println("?????? select nothing??????");
          }
        }
        r = nPri;

      } else { // some wlen but no TradeRecords
        Random random = new Random();
        r = random.nextInt(wLen);

      }
    }
    if (r >= 0) {
      wildS += " selected:" + r + " :" + wilda[r].name;
      E.sysmsg(wildS);
      sStartTrade(this, ret = wilda[r]);
    } else { // no Econs
      E.sysmsg("no planet available to trade r=" + r);
      ret = null;
    }
    return ret;
  }

  protected double calcLY(Econ cur, Econ cur2) {
    double x = (cur.xpos - cur2.xpos);
    double y = (cur.ypos - cur2.ypos);
    double z = (cur.zpos - cur2.zpos);
    double xyz = Math.pow(x, 2.) + Math.pow(y, 2.) + Math.pow(z, 2.);
    return Math.sqrt(xyz);
  }

  /**
   * format the value
   * <ol><li)v very close to 0 => 0</li>
   * <li>v at floating 0 -=> 9.9</li>
   * <li>v large => exp format</li>
   * <li>v fairly large => number.3 digit frac</li>
   * <li>v small < abs .001 => 0.7 digits frac</li>
   * </ol>
   *
   * @param v input value
   * @return value as a string
   */
  public String mf(double v) {
    return EM.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df(double v) {
    return EM.mf(v);
  }

  /**
   * return a whole number in the string
   *
   * @param n a number to convert
   * @return n as a whole number no fraction digits
   */
  protected String wh(double n) {
    return EM.mf(n);
  }

  /**
   * return a whole number in the string
   *
   * @param n a number to convert
   * @return n as a whole number no fraction digits
   */
  protected String wh(int n) {
    return n + "";
  }

  /**
   * merge lists in descending order older t0 new, z to a, return a newShipList,
   * in the new lists leave an original copy of any TradeRecord from the
   * destination previous list, (E.G. entries from the old ownerList are moved
   * to the newOwnerList but are copied from the otherList.
   *
   * @param ownerList owner econ for which list is being made.
   * @param otherList from the other Econ doing the trade
   * @param aOffer new offer to be added to list
   * @return newOwnerList only containing ships
   */
  ArrayList<TradeRecord> mergeLists(ArrayList<TradeRecord> ownerList, ArrayList<TradeRecord> otherList, Offer aOffer) {
    // construct newOwnerList
    ArrayList<TradeRecord> newOwnerList = new ArrayList<TradeRecord>();
    int lOwnerList = ownerList.size();
    int yearsTooEarly = (int) (eM.year - eM.yearsToKeepTradeRecord[0][0]);
    // put new offer at the end
    if (aOffer.term == 0 || aOffer.term == -2) {
      ownerList.add(new TradeRecord(ec, aOffer));
    }

    Iterator<TradeRecord> iterOther = otherList.iterator();
    TradeRecord otherRec;
    for (TradeRecord ownerRec : ownerList) {
      // insert older TradeRecords from the otherList before next ownerRec
      while (iterOther.hasNext() && (otherRec = iterOther.next()).isOlderThan(ownerRec)) {

        if (otherRec.year > yearsTooEarly && otherRec.cnName.startsWith("P")) {
          newOwnerList.add(otherRec);
          if (E.debugTradeRecord) {
            otherRec.listRec();
          }
        }
      }// end while
      // now for more ownerList records
      if (ownerRec.year > yearsTooEarly && ownerRec.cnName.startsWith("P")) {
        newOwnerList.add(ownerRec);
        if (E.debugTradeRecord) {
          ownerRec.listRec();
        }
      }
    } // end for on ownerList
    return newOwnerList;
  }

  /**
   * move a ship to the next planet, and report the distance it is moved
   *
   * @param planet destination planet for the ship
   * @return distance Light Years ship moved
   */
  double moveLocation(Econ planet) {
    if (E.debugTradeSetup) {
      if (planet.pors == E.S) {
        eM.doMyErr("Error cannot move to a star=" + planet.getName());
      }
      if (pors == E.P) {
        eM.doMyErr("Error cannot move a planet=" + getName());
      }
    }
    double distance = calcLY(this, planet);
    xpos = planet.xpos;
    ypos = planet.ypos;
    zpos = planet.zpos;
    return distance;
  }

  /**
   * get the distance moved by this Econ only ships move, but planets have a
   * default distance 0.0
   *
   * @return distanceMoved the light years distance in the last move
   */
  double getDistance() {
    return distanceMoved;
  }

  /**
   * ship start trade after selecting a planet with selectPlanet and getting a
   * planet econ. Do the planet first because it has the resources that the ship
   * will need, hopefully the ship will have resources/staff to trade that the
   * planet thinks will help its economy
   *
   * @see Assets.CashFlow.barter() and Assets.Cashflow.Trades.barter()
   *
   * @param ship
   * @param planet
   */
  protected void sStartTrade(Econ ship, Econ planet) {  // only called for ships
    Econ myCur = eM.curEcon;  // save eM.curEcon for after trade
    Econ cn[] = {planet, ship};
    if (!ship.getDie()) {
      eM.setCurEcon(ship);
      eM.setOtherEcon(planet);
      ship.alreadyTrading = true;  // only used in main thread
      planet.alreadyTrading = true;
      if (ship.pors == E.S && planet.pors == E.P) {
        distanceMoved = ship.moveLocation(planet); //distanceMoved to As trade
      } else if (ship.pors == E.S && planet.pors == E.S) {
        distanceMoved = 0.0; // just stay here
      }
      assert ship != planet : "cannot trade to self=" + planet.getName();
      if (E.debugTradeSetup && E.noAsserts) {
        if (ship == planet) {
          EM.doMyErr("cannot Trade to self=" + planet.getName());
        }
      }
      Offer aOffer = new Offer(eM.year, eM.barterStart, ship, eM, ship, planet);

      int bb = 0; // start barter with planet,
      // for will alternate bb++ each time to start with planet
      int bb1 = bb;

      // barter between economies planet/ship until accept or reject,
      // aoffer.term is always set in the Econ.barter ...
      // term = -3 means done 
      // term starts at eM.barterStart
      // see Assets.CashFlow.barter for the flow of term
      for (int termLoop = aOffer.getTerm(); termLoop > -3; termLoop = aOffer.getTerm()) {
        bb1 = bb; // starts at 0
        bb = ++bb % 2; // starts at 1
        //send loop to both histories cn[0] planet
        cn[0].hist.add(new History(History.loopMinorConditionals5, "T" + aOffer.getTerm() + " " + cn[bb].getName() + " loop>>>>> ", "T=" + termLoop, "bb=" + bb1, "cur name=", cn[bb1].getName(), "ship=", ship.getName(), "planet=", planet.getName(), "<<<<<<"));
        cn[1].hist.add(new History(History.loopMinorConditionals5, "T=", wh(aOffer.getTerm()), "T=" + termLoop, "bb=" + bb1, "name=", cn[bb1].getName(), "ship=", ship.getName(), "planet=", planet.getName(),"<<<<<<<<<"));
        eM.curEcon = cn[bb1];  // starts at planet
        if (termLoop > eM.barterStart - 2) {
          E.sysmsg("in " + cn[bb1].name + ".sStartTrade , " + cn[bb].name + ".barter term=" + aOffer.getTerm() + "\n");
        }
        aOffer = cn[bb1].barter(aOffer, cn[bb], termLoop); //bb1 starts at 0
        // aOffer = cn[bb1].as.barter(aOffer); // first barter with planet
        //   ship.hist.add(new History(3,"Env finish ship Trade"));
        //   planet.hist.add(new History(3,"Env oofinish planet Trade"));

      } // end  termLoops
      if (clearHist()) {
        hist.clear();
      } // for the ship
      if (planet.clearHist()) {
        planet.hist.clear();
      }
      Econ[] planetShipsVisited = {this}; // initialization required by jvm
      int shipsCnt = 0;
      // loop through other ships, if this is a ship, don't barter with self the last one
      // Only barter with a list of ships from a planet, not a ship from this loop
      if (planet.pors == E.P && ((shipsCnt = planet.visitedShipNext)) > -1) {
        int startShips = Math.max(0, shipsCnt - 5); // do only the last 4 ships
        for (; startShips < shipsCnt; startShips++) {
          eM.addlErr = "shipsCnt=" + shipsCnt + ", startShips=" + startShips + ", planet.visitedShipNext=" + planet.visitedShipNext;
          if (planet.visitedShipList[startShips] != this) {
            sStartTrade(this, planet.visitedShipList[startShips]);
          }
        }
      }
      if (planet.pors == E.P && eM.shipsPerPlanet(planet.getClan()) <= shipsCnt) {
        planet.doYearEnd();
      }

      eM.curEcon = myCur; // reset curEcon to its entry value
    }
  }

  /**
   * pass barter on to Assets but do planetList at term equal 0 or -1
   *
   * @param aOffer The current offer
   * @param otherEcon the econ with which we traded
   * @param the term of the barter loop starts at
   * @return
   */
  Offer barter(Offer aOffer, Econ otherEcon, int term) {
    // keep a list of all the visited ships, even if barter failed
    nowName = name;
    if (visitedShipNext < 0 || visitedShipList[visitedShipNext] != otherEcon && term > eM.barterStart - 2) {
      E.sysmsg(" +++++++econ.barter term=" + term + ", visitor=" + otherEcon.name + ", this=" + this.name + " visitedShipNext=" + visitedShipNext);
      if(visitedShipNext < 0){
        myFirstBarterTime = new Date().getTime();
      }
      myBarterTime = new Date().getTime();
      visitedShipList[++visitedShipNext] = otherEcon;
    }
    Offer ret = as.barter(aOffer);
    if (ret.getTerm() == 0 || ret.getTerm() == -2) {
      planetList = mergeLists(planetList, otherEcon.planetList, ret);
      E.sysmsg(" --------econ.barter " + ret.getPlanetName() + " after mergeLists length=" + planetList.size());
    }
    return ret;
  }

  /**
   * get the list of lisited ships and the count of them
   *
   * @param visitedShips return a reference to the visitedShipList
   * @return the count of entries in visitedShipList
   */
  int getShipsVisited(Econ[] visitedShips) {
    visitedShips = visitedShipList;
    int vsmax = Math.min(visitedShipList.length,visitedShips.length);
    int lvs = visitedShips.length;
    int i;
    //copy the common length
    for(i = 0; i< vsmax && visitedShipList[i] != null;i++){visitedShips[i] = visitedShipList[i];}
    vsmax = i-1; // i is first cannot copy
    // null anything more, wipe out from a previous copy
    for(i=i; i < lvs;i++){visitedShips[i] = null;}
    return vsmax;
  }

  /**
   * check whether this econ can do another trade this year
   *
   * @return whether this econ can do another trade this year
   */
  boolean canDoAnotherBarter() { // still in primary thread
    double maxShips = eM.shipsPerPlanet(clan);
    return !(alreadyTrading || didYearEnd) && visitedShipNext < maxShips;
  }
  
  /** add some Econ class variables */
  final static String[] threadFor = {"yearEnd"};
  // count started endYears and name threads
  static int maxEndYears = 20;
  static volatile int[] threadCnt = {0}; // synchronized count of endYear threads
  static volatile String[] threadNames = new String[maxEndYears];
  static volatile String[] econNames = new String[maxEndYears];
  static String nowName = "soonName";
  static Econ nowEc;
  static String nowThread = "soonThread";
  static final int lImWaitingList=10;
  // try to have the previous and the current string
  static String imWaitingList[] = new String[lImWaitingList];
  static int ixImWaitingList;
  int prev2ImwIx=0,prevImwIx=0;
  volatile boolean okEconCnt=false;
  
  /**
   * wait until a synchronized what drops to a limit or until an interrupt or
   * seconds elapsed
   *
   * @param what    ignored use threadCnt
   * @param limit   start waiting if threadCnt[0] > limit
   * @param secs  limit of seconds to wait, 
   * @param why   string of why to wait for comments
   */
 void imWaiting(int[] what, int limit, int secs, String why) {
    int tCnts = 0;
    int le = 10;
    long imStart = (new Date()).getTime();
    prev2ImwIx = prevImwIx;
    prevImwIx = ixImWaitingList;
    int atCnt=0;
    int prevCnt = what[0];
    long imMore = imStart - EM.doYearTime;
    synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    ixImWaitingList = ++ixImWaitingList%lImWaitingList; 
    imWaitingList[prevImwIx] = EM.wasHere2 = "imWaiting in thread " + Thread.currentThread().getName() + " name=" + nowName + " Since doYear" + eM.year + "=" + imMore ;
    StackTraceElement[] prevCalls = new StackTraceElement[le];
    int lstk = Thread.currentThread().getStackTrace().length-1;
    for(int ste=1;ste< le && atCnt < 5  && ste < lstk; ste++){
     prevCalls[ste] = Thread.currentThread().getStackTrace()[ste+1];
    
       if(atCnt == 0){
         imWaitingList[prevImwIx] += " from " +  prevCalls[ste].getMethodName() + " ";
       }
       imWaitingList[prevImwIx] += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
     atCnt++;
    }//for
    EM.wasHere2 = imWaitingList[prevImwIx] += " for " + why + " haveing " + what[0] + " with limit=" + limit;
   // }//if
     
     //now start loop to do waiting if count is above limit
     // what is always threadCnt, so use threadCnt[0]
    boolean dowait= threadCnt[0] > limit;
    for (int timeLoop = 0; timeLoop < secs && dowait; timeLoop++) {
      synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
      if(E.debugThreads){
      if (timeLoop % 5 == 0) {
        System.out.println("`````````````imWaiting in thread " + Thread.currentThread().getName() + " Econ.name=" + nowName + " "  + why + " have cnt " + what[0] + " limit=" + limit + " seconds=" + timeLoop);
      }
      } // debug threads
      
      if(prevCnt > threadCnt[0]){
        prevCnt = what[0];
        secs += timeLoop; // update the limit
      }
    //  synchronized (what) {tCnts = what[0];}
      if (threadCnt[0] <= limit) {
        dowait= false; // no more waiting
        synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
      } else {
        if(E.debugEconCnt){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
        if(timeLoop > secs+2){
          EM.flushes();
        long imStuck = (new Date()).getTime();
        long moreStuck = imStuck - imStart;
     System.err.println(" stuck in doYear" + eM.year + "=" + imMore + " plus " + moreStuck + " name=" + nowName + " for " + why + " over " + (secs+2) + " loops or seconds" + EM.andMore() );
        EM.flushes();
        st.setFatalError();
        } else {
        try {
          if(E.debugEconCnt){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
          Thread.sleep(1000L);
          if(E.debugEconCnt){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
        } catch (Exception ex) {
    
        EM.flushes();
     System.err.println(st.sinceEcon() + " exception=" + ex.toString() + " found " + ex.getMessage() + EM.andMore());
      ex.printStackTrace(System.err);
      
        EM.flushes();
     st.setFatalError();
    }}

      } //else
    } // timeLoop
    long imDoneT = (new Date()).getTime() - eM.doYearTime;
     EM.wasHere2 = imWaitingList[prevImwIx] = "  im done waiting " + imDoneT + " "+ nowName + " " + Thread.currentThread().getName() + " from " + imWaitingList[prevImwIx];
  } // imWaiting  stop waiting
  
  /**
   * initiate a new thread with yearEnd if no more than EM.maxThreads[0][0]
   *
   */
  void doMoreThreads(String doFor, String ecnName) {
    //   didYearEnd = true;  // flag no longer available to barter of end
    nowName = ecnName;
    if(eM.maxThreads[0][0] < 2.0){
      yearEnd();
    } else {
    imWaiting(threadCnt, (int) eM.maxThreads[0][0], 6, "doMoreThreads " + name + " {" + doFor + ")");
  //  EconThread  emm = new EconThread(doFor);
  //  emm.start();
    }

  } // doMoreThreads
  
int prevEtIx=0,prev2EtIx=0; // in Econ
static int ixETList=0;
static final int lETList = 10;
static String sETList[] = new String[10];
boolean doImw = false;
String ecThreadName = Thread.currentThread().getName();
int ecThreadPriority = Thread.currentThread().getPriority();
String dyThreadName = "tbd";
int dyThreadPriority = ecThreadPriority;
static int letTimes = 10;
long etTimes[] = new long[letTimes];
long moreTimes[] = new long[letTimes];
String iWaited = " imWaited ";

/** a siynchronized increment of the threadCnt
 * 
 */
 void incrThreadCnt(){
   synchronized (threadCnt) {threadCnt[0]++; };
 }
 /** a siynchronized decrement of the threadCnt
 * 
 */
 void decrThreadCnt(){
   synchronized (threadCnt) {threadCnt[0]--;};
 }
  /** prepare to do yearEnd possibly as a separate thread
   * 
   */  
 void doYearEnd() {
    if (!didYearEnd) {
      synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}      didYearEnd = true;
      nowName = name;
    dyThreadName = Thread.currentThread().getName();
    dyThreadPriority = Thread.currentThread().getPriority();
    int tCnts = 0;
    int le = 10;
    long etStart = (new Date()).getTime();
    long etMore = etStart - EM.doYearTime;
   // long etTimes[] = new long[letTimes];
    etTimes[0] = etStart;
    prev2EtIx = prevEtIx;
    prevEtIx = ixETList;
    int atCnt=0;
    nowName = name;
    synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    if((doImw = eM.maxThreads[0][0] >= 2.0 && threadCnt[0] > eM.maxThreads[0][0])){  // wait only if over cnt
      imWaiting(threadCnt, (int) eM.maxThreads[0][0], 6, "doYearEnd " + name );
    }
    iWaited = (doImw ? " notImWaited + ": "  + ");
//    ixETList = ((ixETList+ eM.maxThreads[0][0] >= 2.0) ? 1: 0)%lETList;
    String atList = "";
    etTimes[1] = (new Date()).getTime(); // after imWaiting
    
    if(E.DEBUGWAITTRACE) {
    StackTraceElement[] prevCalls = new StackTraceElement[le];
    //set the length of the trace
    int lstk = Thread.currentThread().getStackTrace().length-1;
    for(int ste=0;ste< le && atCnt < 5  && ste < lstk; ste++){
      // start with stackTrace[1]
     prevCalls[ste] = Thread.currentThread().getStackTrace()[ste+1];
       atList = (ste == 0?" " + prevCalls[ste].getMethodName() + " ":"");
       atList += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
     atCnt++;
    }//for
    if (E.debugStatsOut1) {
    sETList[prevEtIx] += atList;
    }
    }//DEBUGWAITTRACE
   synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    if(eM.maxThreads[0][0] >= 2.0) {
     // now in the main thread, up the assigned thread count
     incrThreadCnt();
    long afterT = etTimes[2] = (new Date()).getTime();
    moreTimes[0] = etTimes[0] - EM.doYearTime; // DYEtime
    moreTimes[1] = etTimes[1] - etTimes[0]; // imWaiting time
    moreTimes[2] = etTimes[2]- etTimes[1]; //imCounted time
    //long msecs = EM.doYearTime - etTimes[1];
    sETList[prevEtIx]= atList = "ecTh=" + ecThreadName + " pri" + ecThreadPriority + " dyT=" + dyThreadName + " pri" + dyThreadPriority + " YearEnd " + nowName +  " doYE=" + moreTimes[0] + ":" + iWaited + ":" + moreTimes[1] + " imCounted +" + moreTimes[2];
    synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    EconThread  emm = new EconThread(etTimes,atList,sETList,prevEtIx);
    synchronized(EM.econCnt){ okEconCnt = (EM.econCnt == (EM.porsCnt[0] + EM.porsCnt[1]));}
        assert okEconCnt : "Count Error EM.econCnt=" + EM.econCnt + " not equal to (EM.porsCnt[0]=" + EM.porsCnt[0] + " EM.porsCnt[1]=" + EM.porsCnt[1] + ")";
        if(E.debugEconCnt && E.noAsserts){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
    emm.setPriority(Thread.MIN_PRIORITY);
    etTimes[3] = (new Date()).getTime(); // after create
    emm.start();
    etTimes[5] = (new Date()).getTime(); // after start
    } else {  // skip threads, just yearEnd
      etTimes[2] = etTimes[3] =etTimes[4] = etTimes[5] = (new Date()).getTime(); // after create
      yearEnd();
      etTimes[6] = (new Date()).getTime();
      moreTimes[6] = etTimes[6] - etTimes[2];
    moreTimes[0] = etTimes[0] - EM.doYearTime; // DYEtime
    moreTimes[1] = etTimes[1] - etTimes[0]; // imWaiting time
    moreTimes[2] = etTimes[2]- etTimes[1]; //imCounted time
    //long msecs = EM.doYearTime - etTimes[1];
    EM.wasHere2 = sETList[prevEtIx]= atList = "ecT=" + ecThreadName + " pri" + ecThreadPriority + " dyT=" + dyThreadName + " pri" + dyThreadPriority + " YearEnd " + nowName + " doYE=" + moreTimes[0] + ":" + iWaited + ":" + moreTimes[1] + " bfor yearEnd + " +  moreTimes[2] + " aftr yearEnd +" + moreTimes[6];
    }
    
  }
  }//doYearEnd  
  
 

// this is a inner class which only runs yearEnd
  public class EconThread extends Thread {
  // now Econ.EconThread
    String atList = "none";
    long startEt;
    String etList[];
    int prevIx;
    long[] etTimes;
    long[] moreTimes = new long[letTimes];

    EconThread(long[] setTimes,String aList, String[] sETList, int prevEtIx) {
      etTimes = setTimes;
      atList = aList;
      etList = sETList;
      prevIx = prevEtIx;
      etTimes[2] = (new Date()).getTime();
    }
    public void run() {
    int tCnts = 0;
    int le = 10;
    long etStart = etTimes[6] = (new Date()).getTime(); // thread run
    etTimes[5] = etStart; // start of the new thread
    moreTimes[0] = etTimes[0] - EM.doYearTime; // DYEtime
    moreTimes[1] = etTimes[1] - etTimes[0]; // imWaiting time
    moreTimes[2] = etTimes[2] - etTimes[1]; //imCounted time
    moreTimes[3] = etTimes[3] - etTimes[2]; // doYearEnd after create
    moreTimes[4] = etTimes[4] - etTimes[3]; // thread did create
    moreTimes[5] = etTimes[5] - etTimes[4]; // doYearEnd after start
    moreTimes[6] = etTimes[6] - etTimes[5]; // thread started
    long etMore = etStart - EM.doYearTime;
    int atCnt=0;
    eM.curEcon = ec;
    nowName = ec.name;
    nowThread = Thread.currentThread().getName();
    int threadCnts = threadCnt[0];
    ixETList = ++ixETList%lETList;
    String aL = "";
    long b4 = (new Date()).getTime();
    long msecs = EM.doYearTime - b4;
 
      etList[prevIx]= aL = atList + " DYafterCreate + " + moreTimes[3] + " threadCrtd + " + moreTimes[4] + " DYafterStart + " + moreTimes[5] + " thread run +" + moreTimes[6];
              
    if(E.debugThreadsOut) {

      if(E.DEBUGWAITTRACE){
      StackTraceElement[] prevCalls = new StackTraceElement[le];
      int lstk = Thread.currentThread().getStackTrace().length-1;
      for(int ste=0;ste< le && atCnt < 5  && ste < lstk; ste++){
         prevCalls[ste] = Thread.currentThread().getStackTrace()[ste+1];
           atList = (ste == 0?" " + prevCalls[ste].getMethodName() + " ":"");
           atList += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
         atCnt++;
      }//for
      etList[prevIx] += atList;
      }//DEBUGWAITTRACE
    }
    if(E.debugEconCnt){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
          yearEnd();
          if(E.debugEconCnt){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
          //synchronized (threadCnt) {threadCnt[0]--;}  // done
          decrThreadCnt();
          if(E.debugEconCnt){
           synchronized(EM.econCnt){ if(EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])){
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }}
          if(E.debugThreadsOut){
          long b4e = (new Date()).getTime();
          etTimes[7] = b4e;
          moreTimes[7] = etTimes[7] - etTimes[6];
          long b4ee = b4e - startEt;
         // msecs = startEt - EM.doYearTime;
            etList[prevIx]= aL + " ended Year + " + moreTimes[7] + atList ;
            EM.isHere2(ec,etList[prevIx]);

           if(E.debugThreadsOut1){
             System.out.println(etList[prevIx]);
           }
          }         
  }
} // Econ Thread
}// Econ
