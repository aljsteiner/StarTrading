/*
 Copyright (C) 2012 Albert Steiner
 Copyright (C) 2015 Albert Steiner

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

import java.util.ArrayList;

/**
 *
 * @author albert steiner
 */
public class Difhist extends History {

  /**
   * Class that extends the History information for the hist logs It displays
   * differences between 2 req years The History class generates entries that
   * are added to a economy/environments current hist file. Up to 5 years of
   * files are kept. The files are displayed in StarTrader with the displayLog
   * method using E.logM[0] = E.logEnvirn[0].logM[0]; E.logM[1] =
   * E.logEnvirn[1].logM[1]; as pointers to the 2 log files that can be
   * displayed.
   *
   * @parm lev level for display Display Levels: see class History
   * @param title a longer string title for the row
   * @param reqnew the new later version of the array reflecting growth
   * @param reqold the old original version of the req array before growth
   * @param x2 the index of the row being compared
   *
   * public Difhist(int lev, String title, double[][] reqnew, double[][] reqold,
   * int x2) { level = lev; this.title = title; S1 = df.format(reqnew[0][x2] -
   * reqold[0][x2]); S2 = df.format(reqnew[1][x2] - reqold[1][x2]); S3 =
   * df.format(reqnew[2][x2] - reqold[2][x2]); S4 = df.format(reqnew[3][x2] -
   * reqold[3][x2]); S5 = df.format(reqnew[4][x2] - reqold[4][x2]); S6 =
   * df.format(reqnew[5][x2] - reqold[5][x2]); S7 = df.format(reqnew[6][x2] -
   * reqold[6][x2]); if (reqnew.length > E.iconsMax && reqold.length >
   * E.iconsMax && reqnew[E.iconsMax].length > x2 && reqold[E.iconsMax].length >
   * x2) { S8 = df.format(reqnew[(int) reqnew[E.iconsMinIx][x2]][x2] -
   * reqold[(int) reqold[E.iconsMinIx][x2]][x2]); //min S9 =
   * df.format(reqnew[(int) reqnew[E.iconsMaxIx][x2]][x2] - reqold[(int)
   * reqold[E.iconsMaxIx][x2]][x2]); //max // S9 =
   * df.format(reqnew[E.iconsMax][x2] - reqold[E.iconsMax][x2]); //max S10 =
   * df.format(reqnew[E.iconsSum][x2] - reqold[E.iconsSum][x2]); //Sum } }
   *
   * public Difhist(int lev, String title, double[][] req, E.Years oldYear,
   * E.Years newYear, int x2) { level = lev; int ix = E.iOffset(newYear); int ox
   * = E.iOffset(oldYear); this.title = title;
   *
   * S1 = df.format(req[0 + ix][x2] - req[0 + ox][x2]); S2 = df.format(req[1 +
   * ix][x2] - req[1 + ox][x2]); S3 = df.format(req[2 + ix][x2] - req[2 +
   * ox][x2]); S4 = df.format(req[3 + ix][x2] - req[3 + ox][x2]); S5 =
   * df.format(req[4 + ix][x2] - req[4 + ox][x2]); S6 = df.format(req[5 +
   * ix][x2] - req[5 + ox][x2]); S7 = df.format(req[6 + ix][x2] - req[6 +
   * ox][x2]); S8 = df.format(req[(int) req[E.iconsMinIx + ix][x2]][x2] -
   * req[(int) req[E.iconsMinIx + ox][x2]][x2]); //min S9 = df.format(req[(int)
   * req[E.iconsMaxIx + ix][x2]][x2] - req[(int) req[E.iconsMaxIx +
   * ox][x2]][x2]); //max S10 = df.format(req[E.iconsSum + ix][x2] -
   * req[E.iconsSum + ox][x2]); //Sum }
   *
   * /* public Difhist(int lev, String title, double[][] req, E.Years oldYear,
   * E.Years newYear, E.reqv vvv) { level = lev; int x2 = vvv.ordinal(); int ix
   * = E.iOffset(newYear); int ox = E.iOffset(oldYear); this.title = title;
   *
   * S1 = df.format(req[0 + ix][x2] - req[0 + ox][x2]); S2 = df.format(req[1 +
   * ix][x2] - req[1 + ox][x2]); S3 = df.format(req[2 + ix][x2] - req[2 +
   * ox][x2]); S4 = df.format(req[3 + ix][x2] - req[3 + ox][x2]); S5 =
   * df.format(req[4 + ix][x2] - req[4 + ox][x2]); S6 = df.format(req[5 +
   * ix][x2] - req[5 + ox][x2]); S7 = df.format(req[6 + ix][x2] - req[6 +
   * ox][x2]); CalcReq R = StarTrader.cur.R; S8 = df.format(req[(int)
   * req[E.iconsMinIx + ix][x2]][x2] - req[(int) req[E.iconsMinIx +
   * ox][x2]][x2]); //min S9 = df.format(req[(int) req[E.iconsMaxIx +
   * ix][x2]][x2] - req[(int) req[E.iconsMaxIx + ox][x2]][x2]); //max S10 =
   * df.format(req[E.iconsSum + ix][x2] - req[E.iconsSum + ox][x2]); //Sum }
   *
   * public Difhist(ArrayList<History> hist, int lev, String title, double[][]
   * req, E.Years oldYear, E.Years newYear) { hist.add(new History(lev, title,
   * "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col",
   * "Min", "Sum", "Ave")); hist.add(new Difhist(lev, "dif Working", req,
   * oldYear, newYear, E.reqv.WORKING)); hist.add(new Difhist(lev, "dif Staff",
   * req, oldYear, newYear, E.reqv.STAFF)); hist.add(new Difhist(lev, "dif
   * Cargo", req, oldYear, newYear, E.reqv.CARGO)); hist.add(new Difhist(lev,
   * "dif Guests", req, oldYear, newYear, E.reqv.GUESTS)); hist.add(new
   * Difhist(lev, "dif SWork", req, oldYear, newYear, E.reqv.SWORK));
   * hist.add(new Difhist(lev, "dif ADeprec", req, oldYear, newYear,
   * E.reqv.AFRACDEPRECIATEPERYEAR)); hist.add(new Difhist(lev, "dif
   * SGroReduce", req, oldYear, newYear,
   * E.reqv.SFRACTIONALYEARLYGROWTHREDUCTION)); hist.add(new Difhist(lev, "dif
   * Health", req, oldYear, newYear, E.reqv.HEALTH)); hist.add(new Difhist(lev,
   * "dif Fertility", req, oldYear, newYear, E.reqv.FERTILITY)); hist.add(new
   * Difhist(lev, "dif TotWorth", req, oldYear, newYear, E.reqv.TOTWORTH));
   *
   * double[][] treq = CalcReq.getReq(req, newYear); hist.add(new History(lev,
   * treq, E.reqv.WORKING)); hist.add(new History(lev, treq, E.reqv.STAFF));
   * hist.add(new History(lev, treq, E.reqv.CARGO)); hist.add(new History(lev,
   * treq, E.reqv.GUESTS)); hist.add(new History(lev, treq, E.reqv.SWORK));
   * hist.add(new History(lev, treq, E.reqv.AFRACDEPRECIATEPERYEAR));
   * hist.add(new History(lev, treq, E.reqv.SFRACTIONALYEARLYGROWTHREDUCTION));
   * hist.add(new History(lev, treq, E.reqv.HEALTH)); hist.add(new History(lev,
   * treq, E.reqv.FERTILITY)); hist.add(new History(lev, treq,
   * E.reqv.TOTWORTH)); //new History(20, "++Current", "0Life", "1Struct",
   * "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave");
   * level = 20; this.title = "++Current"; S1 = "0Life"; S2 = "1Struct"; S3 =
   * "2Energy"; S4 = "3Propel"; S5 = "4Defense"; S6 = "5Gov"; S7 = "6ColSrv"; S8
   * = "Min"; S9 = "Sum"; S10 = "Ave"; }
   */
  public Difhist(ArrayList<History> hist, int lev, String title, Assets.CashFlow cur, Assets.CashFlow.HSwaps prev) {
    hist.add(new History(lev, title, "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));// the references below to r s c g possibly wrong
    // this class is unused I think
  //  hist.add(new Difhist(lev, "difStaff", cur.staff.balance, prev.sys[0].balance));
  //  hist.add(new Difhist(lev, "difRes", cur.resource.balance, prev.r.balance));
  //  hist.add(new Difhist(lev, "difCargo", cur.c.balance, prev.c.balance));
   // hist.add(new Difhist(lev, "difGuests", cur.g.balance, prev.g.balance));
   // hist.add(new Difhist(lev, "dif SWork", cur.s.work, prev.s.work));
   // hist.add(new Difhist(lev, "difRHealth", cur.r.health, prev.r.health));
   // hist.add(new Difhist(lev, "difSHealth", cur.s.health, prev.s.health));
   // hist.add(new Difhist(lev, "difRFertility", cur.r.fertility, prev.r.fertility));
   // hist.add(new Difhist(lev, "difSFertility", cur.s.fertility, prev.s.fertility));
    //hist.add(new History(lev, "dif cur,prev", "worth=", df.format(cur.as.sumTotWorth), df.format(prev.sumTotWorth), "H=" + df.format(prev.health), "=>" + df.format(cur.as.health), "F=" + df.format(prev.fertility), "=>" + df.format(cur.fertility)));

    //new History(20, "++Current", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave");
    level = 20;
    this.title = "++Current";
    Ss[0] = "0Life";
    Ss[1] = "1Struct";
    Ss[2] = "2Energy";
    Ss[3] = "3Propel";
    Ss[4] = "4Defense";
    Ss[5] = "5Gov";
    Ss[6] = "6ColSrv";
    Ss[7] = "Min";
    Ss[8] = "Sum";
    Ss[9] = "Ave";
  }

  public Difhist(Econ ec,int lev, String title, ARow anew, ARow aold) {
    level = lev;
    this.title = title;
    int i = 0;
    ARow dd = new ARow(ec).setAsubB(anew, aold);
    Ss[0] = df.format(dd.get(0));
    Ss[1] = df.format(dd.get(1));
    Ss[2] = df.format(dd.get(2));
    Ss[3] = df.format(dd.get(3));
    Ss[4] = df.format(dd.get(4));
    Ss[5] = df.format(dd.get(5));
    Ss[6] = df.format(dd.get(6));

    Ss[7] = df.format(dd.min());  //min
    Ss[8] = df.format(dd.sum());  //sum
    Ss[9] = df.format(dd.ave());  //Ave

  }

  double getMin(double[] ar) {
    return ar[getMinIx(ar)];
  }

  int getMinIx(double[] ar) {
    makeOrderIx(ar);
    return (int) ar[7];
  }

  void makeOrderIx(double[] ar) {
    if (ar[7] > -.00001 && ar[7] < 6.00001) {
      return;
    }
    double[] min = new double[7];
    // int[] maxIx = new int[4];
    int[] minIx = new int[7];
    double minC, minO;
    double minOIx, minCIx;

    for (int g = 0; g < 7; g++) {
      minC = ar[g];
      minCIx = g;
      for (int k = 0; k < g; k++) {
        if (minC < min[k]) {
          minO = min[k];
          minOIx = ar[k + 7];
          ar[k + 7] = minCIx;
          minCIx = minOIx;
          min[k] = minC;
          minC = minO;
        }
      }
      min[g] = minC;
      ar[g + 7] = minCIx;
    }
  }
}
