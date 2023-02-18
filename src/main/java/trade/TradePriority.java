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

/** TradePriority holds values that enable a decision about which planet economy is the best possibility for the next trade
 *
 * @author albert
 */
public class TradePriority {
  EM eM = EM.eM;
  Econ cn;
  String name;
  static double tmCosts = -5.; //sumTrade1YearTravelMaintCosts * light years
  int nYears=-22;
  int lastYear =-10;
  int firstYear = -11;
  int [] ixs = {-12,-11,-10}; // 3 indexs to the 3 largest strategic needs
  double firstInitialSum = -10.; // sum of the values of the 3 ixs
 // double lYears=0.;
  double lastFracIn = -11.;  // 3 ixs values/ sum of rcsg balances
  double lastFracDifIn = -11.1; // newFracIn - lastFracIn
  double lastFirstDifIn = -11.2;
  double lastInitialSum = -11.3;
  double lastIncrInitialSum = -11.4;
  double lastIncrInitialDifIn = -11.5;
  double cumFracIn = -12.; // sum of frac in for each year
  double cumFracDifIn = -14.;  // sum of difference
  double cumIncrInitialSum = -15; // ((lastInitialSum - firstInitialSum)/firstInitialSum)/ years
  double cumIncrFirstInitialDifIn = -16;
  double aveProspectiveFracInitial = -17.;
  double cumFracInitial = 0.;
  // manuals in the current offer
  
  
  /** default constructor holding values to determine selection
   * 
   */
  TradePriority() {

  }
  
  /** constructor to hold values while scanning TradeRecord.java objects
   * each availableEcon gets a TradePriority to accumulate priorities
   * 
   * @param ec the next economy assumed to be unique
   * @param ixs an array of the goods strategic indexes 
   * @param tm the cost for travel and maintenance for 1 light year
   * @param lightYears distance to the planet from the current position;
   */
  TradePriority(Econ ec, int[] ixs,double tm,double lightYears){
    this.cn = ec;
    this.tmCosts = tm * lightYears;
    this.ixs = ixs;
    this.name = ec.getName();
  }
  
   /**get the name of this TradeRecord
   * 
   * @return name
   */
  String getName(){ return name; }
  
  
  /** update the three highest strategic values for this econ 
   *   to enable calculating a final priority
   * multiply by a frac divided by number years prev to final over initial value
   * for skipped years use the current average frac per year mult as above
   * get priority will do the same skipped year process for missing years 
   * at the end of the epoc used to calc the priority
   * @param aRec the current record of a barter for this Econ
   * @param lightYears the distance a ship traveled to get to this econ
   * @param travelCostsPerYear yearly travel cost 
   */
  void updateValues(TradeRecord aRec){
    double t,u,v,w;
    int thisYear = aRec.year;
    A2Row initialBids = aRec.initialGoods;
    A2Row finalBids = aRec.lastGoods;
    double manuals = aRec.manuals;
    double yearsBias = (eM.year - thisYear)*(1.- eM.searchYearBias[cn.pors][cn.clan]);
    if(lastYear < -2){ // initialize 
      firstYear = thisYear;
      lastYear = thisYear;
      lastInitialSum = firstInitialSum = initialBids.plusSum();
      
    } else {
      double sumInitial = initialBids.plusSum();  //initial bid of barter
      double sumIxsInitial = initialBids.arrayIxPlusSum(ixs); //strategic sum
      //double sumIxFinal = finalBids.arrayIxPlusSum(ixS);
      double sumFinal = finalBids.plusSum();   // last bid of barter
      double fracFinal = sumFinal/sumInitial;
      // get projected strategic set, stratFirst/first*final/first
      nYears++;
      cumFracInitial += (fracFinal*sumIxsInitial + manuals)* yearsBias/sumInitial;
      aveProspectiveFracInitial = cumFracInitial/nYears;
      lastInitialSum = sumInitial;
      //lastFracIn = prospectiveFracInitial;
      lastYear = thisYear;
     }
  }
  /** use the updated values to create a prospective trade return for this year
   * Assume that it may be years since the last update so multiply the 
   * lastInitial sum by an increment for the missed years,
   * then multiply by the decrease in values for the final strategic values
   * then subtract the cost of transporting the current working and reserve
   * amounts for each of the financial sectors
   * If there were no updates, do no calculation, use the initial return value
   * @return -50. or a calculated prospective trade value for significant sectors
   */
  double getPriority(){
    double ret = -50.;
    if(lastYear > -1){ // return initial value if never updated
    double yearlyIncr = (lastInitialSum-firstInitialSum)/(lastYear - firstYear);;
    ret = lastInitialSum*yearlyIncr*(eM.year-lastYear)*aveProspectiveFracInitial - tmCosts;
    }
    return ret;
    }
  
}
