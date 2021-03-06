/*
 The MIT License (MIT)

 Copyright (c) 2016 Martin Braun

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.github.s4ke.moar.regex;

import java.util.regex.Pattern;

import com.github.s4ke.moar.moa.Moa;
import com.github.s4ke.moar.util.GenericMatcher;
import com.github.s4ke.moar.util.GenericMoaMatcher;
import com.github.s4ke.moar.util.PatternMatcher;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Martin Braun
 */
public class VSJavaPattern {

	@Test
	public void testSimple() {
		time( gen( Regex.str( "a" ) ), "a", true );
		time( gen( Pattern.compile( "a" ) ), "a", true );
		System.out.println();

		time( gen( Regex.str( "test" ) ), "test", true );
		time( gen( Pattern.compile( "test" ) ), "test", true );
		System.out.println( "---------------------" );
	}

	@Test
	public void testBackRef() {
		String testStr = "aaaaaaaaaaaaaaaaaaa|aaaaaaaaaaaaaaaaaaa";

		time(
				gen(
						Regex.str( "a" )
								.plus()
								.bind( "x" )
								.and( "|" )
								.and( Regex.reference( "x" ) )
				), testStr
				, true
		);
		time(
				gen(
						Pattern.compile( "(a+)\\|\\1" )
				), testStr, true
		);
	}

	private GenericMatcher gen(Object obj) {
		if ( obj instanceof Moa ) {
			return new GenericMoaMatcher( (Moa) obj );
		}
		if ( obj instanceof Regex ) {
			return new GenericMoaMatcher( (Regex) obj );
		}
		if ( obj instanceof Pattern ) {
			return new PatternMatcher( (Pattern) obj );
		}
		return null;
	}

	public void time(GenericMatcher matcher, String string, boolean expectedResult) {
		for ( int i = 0; i < 1000000; ++i ) {
			assertEquals( expectedResult, matcher.check( string ) );
		}
		//warm up
		long totalDiff = 0;
		for ( int i = 0; i < 100000; ++i ) {
			long pre = System.nanoTime();
			assertEquals( expectedResult, matcher.check( string ) );
			long after = System.nanoTime();
			long diff = after - pre;
			totalDiff += diff;
		}
		System.out.println( matcher + " took " + totalDiff / 100000 + "ns" );
	}

}
