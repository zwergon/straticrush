/*
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA  02111-1307, USA.
 */
package no.geosoft.cc.utils;



import java.util.Iterator;



/**
 * Find "nice numbers" within an interval. The interval is given
 * by a min and a max value:
 *
 * <pre>
 * NiceNumbers numbers = new NiceNumbers (min, max, n, false);
 * for (Iterator i = numbers.iterator(); i.hasNext(); ) {
 *   NiceNumber number = (NiceNumber) i.next();
 *   :
 * }
 * </pre>
 *
 * This class is handy for producing quality annotation on graphic
 * displays, for instance along an axis.
 * 
 * @author <a href="mailto:info@geosoft.no">GeoSoft</a>
 */   
public class NiceNumbers implements Iterator
{
  private boolean  isBounded_;
  private double   fromValue_;
  private double   toValue_;
  private double   firstValue_;    // First nice number < from
  private double   lastValue_;     // First nice number > to
  private double   majorStep_;
  private double   minorStep_;
  private double   minorMinorStep_;
  private double   step_;
  private int      nValues_;
  private int      valueNo_;

  
  
  /**
   * Create nice numbers in an interval.
   *
   * @param fromValue       From value.
   * @param toValue         To value.
   * @param nNumbersApprox  Approximate number of major nice numbers to produce.
   * @param isBounded       True if fromValue/toValue should be end points
   *                        and hence reported as nice numbers.
   */
  public NiceNumbers (double fromValue, double toValue, int nNumbersApprox,
                      boolean isBounded)
  {
    fromValue_ = fromValue;
    toValue_   = toValue;
    isBounded_ = isBounded;

    if (nNumbersApprox <= 0) nNumbersApprox = 1;
    double step = (toValue_ - fromValue_) / nNumbersApprox;
    if (step == 0.0) step = 1.0;

    boolean isAscending = step > 0.0;
    
    // Scale abs(step) to interval 1 - 10
    double scaleFactor = 1.0;
    while (Math.abs (step) > 10) {
      step /= 10.0;
      scaleFactor *= 10.0;
    }
    while (Math.abs (step) < 1) {
      step *= 10.0;
      scaleFactor /= 10.0;
    }

    // Find nice major step value
    majorStep_ = Math.abs (step);
    if      (majorStep_ > 7.50) majorStep_ = 10.0;
    else if (majorStep_ > 3.50) majorStep_ =  5.0;
    else if (majorStep_ > 2.25) majorStep_ =  2.5;
    else if (majorStep_ > 1.50) majorStep_ =  2.0;
    else                        majorStep_ =  1.0;

    // Find corresponding minor step value
    if      (majorStep_ == 10.0) minorStep_ = 5.0;
    else if (majorStep_ ==  5.0) minorStep_ = 2.5;
    else if (majorStep_ ==  2.5) minorStep_ = 0.5;
    else if (majorStep_ ==  2.0) minorStep_ = 1.0;
    else                         minorStep_ = 0.1;
    
    // Find corresponding minor minor step value
    if      (minorStep_ ==  5.0) minorMinorStep_ = 1.0;
    else if (minorStep_ ==  2.5) minorMinorStep_ = 0.5;
    else if (minorStep_ ==  1.0) minorMinorStep_ = 0.1;    
    else if (minorStep_ ==  0.5) minorMinorStep_ = 0.1;
    else                         minorMinorStep_ = 0.0;

    if (step < 0) {
      majorStep_      = -majorStep_;
      minorStep_      = -minorStep_;
      minorMinorStep_ = -minorMinorStep_;      
    }
    
    majorStep_      *= scaleFactor;
    minorStep_      *= scaleFactor;
    minorMinorStep_ *= scaleFactor;    

    // Find first nice value before fromValue
    firstValue_ = ((int) (fromValue_ / majorStep_)) * majorStep_;
    
    if      ( isAscending && firstValue_ > fromValue_) firstValue_ -=majorStep_;
    else if (!isAscending && firstValue_ < fromValue_) firstValue_ -=majorStep_;

    // Find last nice value after toValue
    lastValue_ = ((int) (toValue_ / majorStep_)) * majorStep_;
    if      ( isAscending && lastValue_ < toValue_) lastValue_ += majorStep_;
    else if (!isAscending && lastValue_ > toValue_) lastValue_ += majorStep_;

    // Find total number of values
    step_ = minorMinorStep_ != 0.0 ? minorMinorStep_ :
                 minorStep_ != 0.0 ? minorStep_ : majorStep_;

    nValues_ =  (int) Math.round ((lastValue_ - firstValue_) / step_) + 1;
    
    // Move the steps from value space to count space
    majorStep_      = (double) Math.round (majorStep_      / step_);
    minorStep_      = (double) Math.round (minorStep_      / step_);
    minorMinorStep_ = (double) Math.round (minorMinorStep_ / step_);
  }


  
  /**
   * Create nice numbers in an unbound interval.
   * 
   * @param fromValue       From value.
   * @param toValue         To value.
   * @param nNumbersApprox  Approximate number of major nice numbers to produce.
   */
  public NiceNumbers (double fromValue, double toValue, int nNumbersApprox)
  {
    this (fromValue, toValue, nNumbersApprox, false);
  }



  /**
   * Initiate the iteration and return the iterator object
   * 
   * @return  The iterator (which is this).
   */
  public Iterator iterator()
  {
    valueNo_ = 0;
    return this;
  }
  
  
  
  /**
   * Retur true if there are more nice numbers, false otherwise.
   * 
   * @return  True if there are more nice numbers, false otherwise.
   */
  public boolean hasNext()
  {
    return valueNo_ < nValues_;
  }


  
  /**
   * Return the first nice number of the interval.
   * 
   * @return  First nice number of the interval.
   */
  public double getFirstValue()
  {
    return isBounded_ ? fromValue_ : firstValue_;
  }


  
  /**
   * Return the last nice number of the interval.
   * 
   * @return  Last nice number of the interval.
   */
  public double getLastValue()
  {
    return isBounded_ ? toValue_ : lastValue_;
  }


  
  /**
   * Return number of nice values in this interval.
   * 
   * @return  Total number of nice numbers in the interval.
   */
  public int getNValues()
  {
    return nValues_;
  }
  
  

  private boolean equals (double a, double b)
  {
    double limit = a == 0.0 ? 0.001 : Math.abs (a) * 0.001;
    return b > a - limit && b < a + limit;
  }

  
  
  /**
   * Return the next nice number of the sequence.
   * 
   * @return  Next nice number.
   */
  public Object next()
  {
    // Solve the bounded case
    if (isBounded_) {
      if (valueNo_ == 0) {
        double value = firstValue_;

        while ((fromValue_ < toValue_ && value <= fromValue_) ||
               (fromValue_ > toValue_ && value >= fromValue_)) {
          valueNo_++;
          value = firstValue_ + valueNo_ * step_;
        }

        int rank = equals (firstValue_, fromValue_) ? 0 : 1;
        return new NiceNumber (fromValue_, rank, 0.0);
      }
      else {
        double value = firstValue_ + valueNo_ * step_;
        if ((fromValue_ < toValue_ && value >= toValue_) ||
            (fromValue_ > toValue_ && value <= toValue_)) {
          valueNo_ = nValues_;

          int rank = equals (lastValue_, toValue_) ? 0 : 1;
          return new NiceNumber (toValue_, rank, 1.0);
        }
      }
    }
    
    double value = firstValue_ + valueNo_ * step_;

    int rank;
    if      (valueNo_ % (int) majorStep_ == 0.0) rank = 0;
    else if (valueNo_ % (int) minorStep_ == 0.0) rank = 1;
    else                                         rank = 2;

    // Find position
    double first = getFirstValue();
    double last  = getLastValue();
    double position = first == last ? 0.0 : (value - first) / (last - first);
    
    NiceNumber niceNumber = new NiceNumber (value, rank, position);

    valueNo_++;
    
    return niceNumber;
  }



  /**
   * From Iterator. Removing nice numbers are not possible, so this method
   * is left empty.
   */
  public void remove()
  {
    // Not possible
  }


  
  /**
   * Testing this class.
   * 
   * @param args  Not used.
   */
  public static void main (String[] args)
  {
    NiceNumbers numbers = new NiceNumbers (1.01, -1.0, 4, true);

    for (Iterator i = numbers.iterator(); i.hasNext(); ) {
      NiceNumber niceNumber = (NiceNumber) numbers.next();
      if (niceNumber.getRank() < 2)
        System.out.println (niceNumber);
    }
  }
}

