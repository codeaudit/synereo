// -*- mode: Scala;-*- 
// Filename:    Continuation.scala 
// Authors:     lgm                                                    
// Creation:    Mon Sep 19 09:24:56 2011 
// Copyright:   Not supplied 
// Description: 
// ------------------------------------------------------------------------

package com.biosimilarity.lift.lib.monad

class Continuation[A,B,C]( val k : ( A => B ) => C ) {
  def apply( f : A => B ) : C = k( f )
  def map( f : A => B ) : C = this( f )
  def foreach( f : A => B ) : C = map( f )
  override def equals( o : Any ) : Boolean = {
    o match {
      case that : Continuation[A,B,C] => {
	k.equals( that.k )
      }
      case _ => {
	false
      }
    }
  }
  override def hashCode( ) : Int = {
    37 * k.hashCode
  }
}
object Continuation {
  def apply [A,B,C] (
    k : ( A => B ) => C
  ) : Continuation[A,B,C] = {
    new Continuation[A,B,C]( k )
  }
  def unapply [A,B,C] (
    kc : Continuation[A,B,C]
  ) : Option[( ( A => B ) => C )] = {
    Some( ( kc.k ) )
  }
}  

class ContinuationM( )
extends PMonad[Continuation] {
  override def fmap [S1,S2,T,U] (
    f : S1 => S2
  ) : Continuation[S1,T,U] => Continuation[S2,T,U] = {
    ( ctxt : Continuation[S1,T,U] ) => {	
      Continuation[S2,T,U](
	( s22t : S2 => T ) => {
	  for( s1 <- ctxt ) yield { s22t( f( s1 ) ) }
	}
      )
    }
  }
  override def unit [S,T] ( s : S ) : Continuation[S,T,T] = {
    Continuation[S,T,T]( ( k : S => T ) => k( s ) )
  }
  override def mult [S,T,U,V] (      
    kks : Continuation[Continuation[S,V,U],U,T]
  ) : Continuation[S,V,T] = {      
    Continuation[S,V,T]( 
      ( s2v : S => V ) => {
	kks.map(
	  ( csvu : Continuation[S,V,U] ) => {
	    csvu.map( s2v )
	  }
	)
      }
    )
  }
  override def strength [S1,S2,T,U](
    s1 : S1, cs2tu : Continuation[S2,T,U] 
  ) : Continuation[Tuple2[S1,S2],T,U] = {
    Continuation[Tuple2[S1,S2],T,U] (
      ( s1s22t : (Tuple2[S1,S2] => T) ) => {
	cs2tu.map(
	  ( s2 : S2 ) => {
	    val s1s2 : Tuple2[S1,S2] = ( s1, s2 )
	      s1s22t( s1s2 )
	  }
	)
      }
    )
  }
}

class DelimitedContinuation( )
extends ContinuationM( ) {
  def reset [A,B,C] (
    c : Continuation[A,A,B]
  ) : Continuation[B,C,C] = {
    Continuation[B,C,C](
      ( b2c2c : ( B => C ) ) => {
	b2c2c( c.map( ( a : A ) => a ) )
      }
    )
  }
  def shift [A,B,C,D,E] (
    a2cbcc2cdde : ( A => Continuation[B,C,C] ) => Continuation[D,D,E]
  ) : Continuation[A,B,E] = {
    Continuation[A,B,E](
      ( a2b : A => B ) => {
	a2cbcc2cdde(
	  ( a : A ) => {
	    unit[B,C]( a2b( a ) )
	  }
	).map( ( d : D ) => d )
      }
    )
  }
}

package usage {
  object TryDelC {
    val dc1 = new DelimitedContinuation()
    def plus31 = {
      dc1.reset[Int,Int,Int](
	dc1.fmap( ( x : Int ) => { 3 + x } )(
	  dc1.shift[Int,Int,Int,Int,Int](
	    ( c : Int => Continuation[Int,Int,Int] ) => {
	      Continuation[Int,Int,Int](
		( k : Int => Int ) => {
		  c( 0 ).map(
		    ( a : Int ) => {
		      c( 1 ).map( 
			( b : Int ) => {
			  k( a + b )
			}
		      )
		    }
		  )
		}
	      )
	    }
	  )	  
	)
      )
    }
    def plus32 = {
      dc1.reset[Int,Int,Int](
	dc1.fmap( ( x : Int ) => x )(
	  dc1.shift[Int,Int,Int,Int,Int](
	    ( c : Int => Continuation[Int,Int,Int] ) => {
	      Continuation[Int,Int,Int](
		( k : Int => Int ) => {
		  c( 0 ).map( k ) + c( 1 ).map( k )
		}
	      )
	    }
	  )
	)
      )
    }
  }
}
