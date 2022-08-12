/*
 * Copyright (C) 2015 albert steiner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package trade;

import java.util.ArrayList;

/**
 * 1/2016 this was created to more properly represent costs, remnants,
 * fertilities and health and thus, the needs for swaps.<br>
 * In addition the concepts of goal and emergency
 *
 * This is a set of methods to process 4 related ARow objects. The 4 objects
 * represent: A = resource:resource, cargo:resource B = staff:resource,
 * guests:resource C = resource:staff, cargo:staff D = staff: staff,
 * guests:staff <br>
 * The objects alternatively may contain costs/remnants/fertility/health
 *
 * @author albert Steiner
 */
public class A4Row {

  Econ ec = EM.curEcon;
  private int[] ix;
  String titl = "unset";
  int lev = History.informationMinor9;
  ARow[] A = new ARow[4];
  // balances for r,c,s,g   or rHealth sHealth rFertility sFertility
  double sum = 0.;
  int[] aCnt = {-11, -11, -11, -11, -11, -11};
  ArrayList<History> hist = EM.curEcon.getHist();

  /**
   * set object copy from 8 costs RCr set RR,CR RCs Set RS,CS SGr set SR,GR SGs
   * set SS,GS
   *
   * @param leva
   * @param titla
   * @param joint joint r,s costs
   * @param RR
   * @param RS
   * @param CR
   * @param CS
   * @param SR
   * @param SS
   * @param GR
   * @param GS
   * @return instance
   */
  public A4Row setCopy(int leva, String titla, A2Row joint, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS) {
    lev = leva;
    titl = titla;
    ec = RR.ec;
    A[0].set(RR, CR);
    A[1].set(RS, CS);
    A[2].set(SR, GR);
    A[3].set(SS, GS);
    joint.A[0].set(A[0], A[2]);
    joint.A[1].set(A[1], A[3]);
    return this;
  }

  /**
   * set object copy from 8 costs RCr set RR,CR RCs Set RS,CS SGr set SR,GR SGs
   * set SS,GS
   *
   * @param leva
   * @param titla
   * @param RR
   * @param RS
   * @param CR
   * @param CS
   * @param SR
   * @param SS
   * @param GR
   * @param GS
   * @return instance
   */
  public A4Row setCopy(int leva, String titla, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS) {
    lev = leva;
    titl = titla;
    ec = CR.ec;
    A[0].set(RR, CR);
    A[1].set(RS, CS);
    A[2].set(SR, GR);
    A[3].set(SS, GS);
    return this;
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values
   *
   * @param leva
   * @param titla
   * @param joint joint r,s costs
   * @param RR
   * @param RS
   * @param CR
   * @param CS
   * @param SR
   * @param SS
   * @param GR
   * @param GS
   * @param sBal staff balance
   * @param sWork staff work
   * @param mult either 1. or poorHealthEffect
   * @return instance
   *
   * public A4Row setCopy(A2Row joint, ARow RR, ARow RS, ARow CR, ARow CS, ARow
   * SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork, double mult) {
   * A[0].set(RR, CR).setMultV(mult); A[1].set(RS, CS).multBdivbyC(sBal,
   * sWork).setMultV(mult); // convert from work to staff cost A[2].set(SR,
   * GR).setMultV(mult); A[3].set(SS, GS).multBdivbyC(sBal,
   * sWork).setMultV(mult); // convert from work to staff cost
   * joint.A[0].set(A[0], A[2]); joint.A[1].set(A[1], A[3]); return this; }
   */
  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values then multiply by mult
   *
   * * @param leva
   * @param titla
   * @param joint joint r,s costs
   * @param RR
   * @param RS
   * @param CR
   * @param CS
   * @param SR
   * @param SS
   * @param GR
   * @param GS
   * @param sBal staff balance
   * @param sWork staff work
   * @param mult either 1. or poorHealthEffect
   * @return instance 0+RR+CR, 1=RS+CS, 2+SR,GR, 3=SS+BS copy of costs * mult
   *
   * public A4Row setCopy(ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS,
   * ARow GR, ARow GS, ARow sBal, ARow sWork, double mult) { A[0].set(RR,
   * CR).setMultV(mult); A[1].set(RS, CS).multBdivbyC(sBal,
   * sWork).setMultV(mult); // convert from work to staff cost A[2].set(SR,
   * GR).setMultV(mult); A[3].set(SS, GS).multBdivbyC(sBal,
   * sWork).setMultV(mult); // convert from work to staff cost return this; }
   */
  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values
   *
   * @param leva
   * @param titla
   * @param joint joint r,s costs
   * @param RR
   * @param RS
   * @param CR
   * @param CS
   * @param SR
   * @param SS
   * @param GR
   * @param GS
   * @param sBal staff balance
   * @param sWork staff work
   * @return instance 0+RR+CR, 1=RS+CS, 2+SR,GR, 3=SS+BS copy of costs
   */
  public A4Row setCopy(int leva, String titla, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork) {
    lev = leva;
    titl = titla;
    ec = RR.ec;
    for (int n : E.alsecs) {
      set(0, n, (RR.get(n) + CR.get(n)));
      set(1, n, (RS.get(n) + CS.get(n)) * sBal.get(n) / sWork.get(n));
      set(1, n, (SR.get(n) + GR.get(n)));
      set(3, n, (SS.get(n) + GS.get(n)) * sBal.get(n) / sWork.get(n));
    }
    return this;
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values
   *
   * @param leva
   * @param titla
   * @param joint joint r,s costs
   * @param RR
   * @param RS
   * @param CR
   * @param CS
   * @param SR
   * @param SS
   * @param GR
   * @param GS
   * @param sBal staff balance
   * @param sWork staff work
   * @param phe poor health effect
   * @return instance 0+RR+CR, 1=RS+CS, 2+SR,GR, 3=SS+BS copy of costs
   */
  public A4Row setCopy(int leva, String titla, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork, double phe) {
    lev = leva;
    titl = titla;
    ec = CR.ec;
    for (int n : E.alsecs) {
      set(0, n, (RR.get(n) + CR.get(n) * phe));
      set(1, n, (RS.get(n) + CS.get(n)) * phe * sBal.get(n) / sWork.get(n));
      set(1, n, (SR.get(n) + GR.get(n)) * phe);
      set(3, n, (SS.get(n) + GS.get(n)) * phe * sBal.get(n) / sWork.get(n));
    }
    return this;
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values multiply by poor health effect and years traveled
   *
   * @param leva
   * @param titla
   * @param joint joint r,s costs
   * @param RR
   * @param RS
   * @param CR
   * @param CS
   * @param SR
   * @param SS
   * @param GR
   * @param GS
   * @param sBal staff balance
   * @param sWork staff work
   * @param phe poor health effect
   * @param years years of travel
   * @return instance 0+RR+CR, 1=RS+CS, 2+SR,GR, 3=SS+BS copy of costs
   */
  public A4Row setCopy(int leva, String titla, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork, double phe, double years) {
    lev = leva;
    titl = titla;
    ec = RR.ec;
    for (int n : E.alsecs) {
      set(0, n, (RR.get(n) + CR.get(n) * phe * years));
      set(1, n, (RS.get(n) + CS.get(n)) * phe * years * sBal.get(n) / sWork.get(n));
      set(1, n, (SR.get(n) + GR.get(n)) * phe * years);
      set(3, n, (SS.get(n) + GS.get(n)) * phe * years * sBal.get(n) / sWork.get(n));
    }
    return this;
  }

  /**
   * set A4Row from the 4 SubAssetBalances or costs
   *
   * @param leva
   * @param titla
   * @param rb r.balance
   * @param cb c.balance
   * @param sb s.balance
   * @param gb g.balance
   * @return instance
   */
  public A4Row setCopy(int leva, String titla, A2Row joint, ARow rb, ARow cb, ARow sb, ARow gb) {
    lev = leva;
    titl = titla;
    ec = joint.ec;
    A[0].set(rb);  // or costs r r + c r
    A[1].set(cb);  // r s + c s
    A[2].set(sb);  // s r + g r
    A[3].set(gb);  // s s + g s
    joint.A[0].set(rb, sb);
    joint.A[1].set(cb, gb);
    return this;
  }

  public A4Row setCopy(int leva, String titla, ARow rb, ARow cb, ARow sb, ARow gb) {
    lev = leva;
    titl = titla;
    A[0].set(rb);  // or costs r r + c r
    A[1].set(cb);  // r s + c s
    A[2].set(sb);  // s r + g r
    A[3].set(gb);  // s s + g s
    return this;
  }

  public A4Row setCopy(int leva, String titla, ARow rb, ARow cb, ARow sb, ARow gb, double mult) {
    lev = leva;
    titl = titla;
    ec = rb.ec;
    A[0].set(rb);  // or costs r r + c r
    A[1].set(cb);  // r s + c s
    A[2].set(sb);  // s r + g r
    A[3].set(gb);  // s s + g s
    for (int n : E.alsecs) {
      A[0].set(n, rb.get(n) * mult);
      A[1].set(n, cb.get(n) * mult);
      A[2].set(n, sb.get(n) * mult);
      A[3].set(n, gb.get(n) * mult);
    }
    return this;
  }

  /**
   * mult each by each of instance by v
   *
   * @param v
   * @return each by each this*v
   */
  public A4Row setMultV(double v) {
    for (int m : E.d4) {
      for (int n : E.alsecs) {
        set(m, n, get(m, n) * v);
      }
    }
    return this;
  }

  /*
   public A4Row setUse(A2Row joint, ARow rb, ARow cb, ARow sb, ARow gb) {
   A[0] = (rb);  // or costs r r + c r
   A[1] = (cb);  // r s + c s
   A[2] = (sb);  // s r + g r
   A[3] = (gb);  // s s + g s
   joint.A[0].set(rb, sb);
   joint.A[1].set(cb, gb);
   return this;
   }

   public A4Row setUse(ARow rb, ARow cb, ARow sb, ARow gb) {
   A[0] = (rb);  // or costs r r + c r
   A[1] = (cb);  // r s + c s
   A[2] = (sb);  // s r + g r
   A[3] = (gb);  // s s + g s
   return this;
   }
   */
  /**
   * The ;no parameter constructor, zero'd ARow s
   */
  public A4Row() {

    for (int m : E.d4) {
      A[m] = new ARow(ec).zero();
    }
  }

  ARow getRCr() {
    return A[0];
  }

  ARow getRCs() {
    return A[1];
  }

  ARow getSGr() {
    return A[2];
  }

  ARow getSGs() {
    return A[3];
  }

  ARow aGet(int n) {
    return A[n];
  }

  ARow get(int n) {
    return A[n];
  }

  double get(int i, int j) {
    return aGet(i).get(j);
  }

  double set(int m, int n, double V) {
    return aGet(m).set(n, V);
  }

  double set(int m, int n, double V, String c) {
    return A[m].set(n, V);
  }

  A4Row set(A4Row A, A4Row B) {
    int[] d4 = {0, 1, 2, 3};
    for (int m : d4) {
      this.get(m).set(A.get(m), B.get(m));
    }
    return this;
  }

  /**
   * set each this + b*v
   *
   * @param B
   * @param mult
   * @return this + b*v
   */
  public A4Row setTaddBmultV(A4Row B, double mult) {
    for (int i = 0; i < 4; i++) {
      this.get(i).setAaddBmultV(this.get(i), B.get(i), mult);
    }
    return this;
  }

  /**
   * set A * V
   *
   * @param A
   * @param mult
   * @return each A * V
   */
  public A4Row setAmultV(A4Row A, double mult) {
    for (int i = 0; i < 4; i++) {
      this.get(i).setAmultV(A.get(i), mult);
    }
    return this;
  }

  public A4Row multV(double mult) {
    A4Row res = new A4Row();
    for (int i = 0; i < 4; i++) {
      res.get(i).setAmultV(this.get(i), mult);
    }
    return res;
  }

  A4Row add(A4Row A, A4Row B) {
    A4Row results = new A4Row();
    for (int i = 0; i < 4; i++) {
      results.get(i).set(A.get(i), B.get(i));
    }
    return results;
  }

  /**
   * send the object to hist with preString apre unless this.lev >= History.dl
   *
   * @param hist pointer to hist ArrayList
   * @param apre preString value
   */
  void sendHist(ArrayList<History> hist, String apre) {
    if (History.dl > lev && ec.myEconCnt< 5) {
      String titl = EM.curEcon.getName() + " " + this.titl;
      lev = Math.min(lev, History.auxInfo);
      hist.add(new History(apre, lev, titl, A[0]));
      hist.add(new History(apre, lev, titl, A[1]));
      hist.add(new History(apre, lev, titl, A[2]));
      hist.add(new History(apre, lev, titl, A[3]));
    }
  }

  /**
   * list object on hist with preString apre unless bLev >= History.dl
   *
   * @param hist
   * @param bLev
   * @param apre
   * @param titl0 title of row0
   * @param titl1 title of row1
   * @param titl2 title of row2
   * @param titl3 title of row3
   */
  void sendHist(ArrayList<History> hist, int bLev, String apre, String titl0, String titl1, String titl2, String titl3) {
    if (ec.myEconCnt> E.maxEconHist) {
      hist.clear();
    }
    if (History.dl > bLev && ec.myEconCnt< 5) {
      String titl = EM.curEcon.getName() + " " + this.titl;
      lev = Math.min(lev, History.auxInfo);
      hist.add(new History(apre, lev, titl, A[0]));
      hist.add(new History(apre, lev, titl, A[1]));
      hist.add(new History(apre, lev, titl, A[2]));
      hist.add(new History(apre, lev, titl, A[3]));
    }
  }
  /*
   public A4Row set4Remnants(A2Row jointRemnants, A4Row j4Costs, A2Row j2Costs, A4Row mtggRemnants, A4Row balances, A4Row costs1, A4Row costs2, A4Row rawGrowthCosts, A4Row rawGrowth, A4Row reqCosts, A6Row fertilities, double growMult, double growYears) {
   if (jointRemnants == null) {
   jointRemnants = new A2Row();
   }
   if (costs2 == null) {
   costs2 = new A4Row();
   }
   if (rawGrowthCosts == null) {
   rawGrowthCosts = new A4Row();
   }
   if (rawGrowth == null) {
   rawGrowth = new A4Row();
   }
   if (fertilities == null) {
   fertilities = new A6Row();
   }
   if (j4Costs == null) {
   j4Costs = new A4Row();
   }

   double sumR = 0, sumS = 0, a1, b1, c1, d1, fr1, fr2, fs3, fs4, rr1, rs1, sr1, ss1;
   for (int m : E.d4) {
   for (int n : E.alsecs) {
   j4Costs.set(m, n, costs1.get(m, n) + costs2.get(m, n) + rawGrowthCosts.get(m, n) * fertilities.get((m % 2), n)
   );
   }
   }
   for (int m : E.d2) {
   for (int n : E.alsecs) {
   j2Costs.set(m, n, j4Costs.get(m, n) + j4Costs.get(m + 2, n));
   }
   }

   // now calculate the A4 remnants, use balances in proportion to the costs
   for (int m : E.d4) {
   for (int n : E.alsecs) {
   this.set(m, n, balances.get(m % 2, n) * j4Costs.get(m, n) / j2Costs.get(m % 2, n) - j4Costs.get(m, n));
   }
   }

   // now calculate the A2 remnants
   for (int m : E.d2) {
   for (int n : E.alsecs) {
   jointRemnants.set(m, n, this.get(m, n) + this.get(m + 2, n));
   }
   }

   // now calculate fertility or health if reqCosts is not zero or null
   if (reqCosts != null && reqCosts.get(0, 0) != 0. && reqCosts.get(2, 0) != 0.) {
   A2Row j2ReqCosts = new A2Row();
   for (int m : E.d2) {
   for (int n : E.alsecs) {
   j2ReqCosts.set(m, n, reqCosts.get(m, n) + reqCosts.get(2 + m, n)); // 0,2 and 1,3
   }
   }
   // now use the 4frac = (j4remnants - ((reqCosts/j2Reqcosts))/reqCosts)
   }

   // now deal with possible mtgg calculation wher mtgg represents the costs per year less some fraction of that years growth and multiplied by some number of years, which may not be a whole number.  Get only the mtggRemnanst not the calculated costs
   if (growYears > E.pzero) {  // only do if growYears is more than 0
   A4Row growCosts = new A4Row();
   for (int m : E.d4) {
   for (int n : E.alsecs) {
   growCosts.set(m, n, growYears * (costs1.get(m, n) + costs2.get(m, n) + fertilities.get(m % 2, n) * rawGrowthCosts.get(m, n) - fertilities.get(m % 2, n) * rawGrowth.get(m, n) * growMult));
   }
   }
   for (int m : E.d2) {
   for (int n : E.alsecs) {
   j2Costs.set(m, n, growCosts.get(m, n) + growCosts.get(m + 2, n));
   }
   }
   for (int m : E.d4) {
   for (int n : E.alsecs) {
   mtggRemnants.set(m, n, balances.get(m % 2, n) * growCosts.get(m, n) / j2Costs.get(m % 2, n) - growCosts.get(m, n));
   }
   }
   }
   return this;
   }
   */
}
