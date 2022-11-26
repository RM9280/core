/*
 *	Copyright 2019, Continual.io
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *	
 *	http://www.apache.org/licenses/LICENSE-2.0
 *	
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */

package io.continual.util.data.json;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import io.continual.util.data.json.JsonVisitor.ArrayOfObjectVisitor;
import junit.framework.TestCase;

public class JsonUtilTest extends TestCase
{
	@Test
	public void testReadStringArray ()
	{
		final JSONObject base = new JSONObject ()
			.put ( "str", "string" )
			.put ( "arr", new JSONArray ().put ( "one" ).put ( "two" ).put ( "three" ) )
		;

		List<String> result = JsonUtil.readStringArray ( base, "str" );
		assertEquals ( 1, result.size () );
		assertEquals ( "string", result.iterator ().next () );
		
		result = JsonUtil.readStringArray ( base, "arr" );
		assertEquals ( 3, result.size () );
		
		final Iterator<String> it = result.iterator ();
		assertEquals ( "one", it.next () );
		assertEquals ( "two", it.next () );
		assertEquals ( "three", it.next () );
	}

	private static class IntegerHolder
	{
		public IntegerHolder ( int i ) { fValue = i; }
		public int getAndIncr () { return fValue++; }
		private int fValue;
	}

	@Test
	public void testArraySort ()
	{
		final JSONArray a = new JSONArray ()
			.put ( new JSONObject ().put ( "foo", 9 ) )
			.put ( new JSONObject ().put ( "foo", 6 ) )
			.put ( new JSONObject ().put ( "foo", 4 ) )
			.put ( new JSONObject ().put ( "foo", 3 ) )
			.put ( new JSONObject ().put ( "foo", 1 ) )
			.put ( new JSONObject ().put ( "foo", 2 ) )
			.put ( new JSONObject ().put ( "foo", 7 ) )
			.put ( new JSONObject ().put ( "foo", 5 ) )
			.put ( new JSONObject ().put ( "foo", 8 ) )
		;

		JsonUtil.sortArrayOfObjects ( a, "foo" );

		final IntegerHolder ih = new IntegerHolder ( 1 );
		JsonVisitor.forEachObjectIn ( a, new ArrayOfObjectVisitor() {

			@Override
			public boolean visit ( JSONObject t ) throws JSONException
			{
				assertEquals ( ih.getAndIncr(), t.getInt ( "foo" ) );
				return false;
			}
		} );
	}

	@Test
	public void testArraySort2 ()
	{
		final String arrayData = "[\n"
			+ "  {\n"
			+ "    \"op\": 20,\n"
			+ "    \"system\": \"CIN-13DIGITAL-TEMP\",\n"
			+ "    \"qty\": 316.4375,\n"
			+ "    \"category\": \"WASTE\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 20,\n"
			+ "    \"system\": \"CIN-13DIGITAL-TEMP\",\n"
			+ "    \"qty\": 100,\n"
			+ "    \"category\": \"INK\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 20,\n"
			+ "    \"system\": \"CIN-13DIGITAL-TEMP\",\n"
			+ "    \"qty\": 25521.25,\n"
			+ "    \"category\": \"LABOR\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 20,\n"
			+ "    \"system\": \"CIN-13DIGITAL-TEMP\",\n"
			+ "    \"qty\": 100,\n"
			+ "    \"category\": \"MATERIAL\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 30,\n"
			+ "    \"system\": \"CIN-ABG\",\n"
			+ "    \"qty\": 266.5234375,\n"
			+ "    \"category\": \"WASTE\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 30,\n"
			+ "    \"system\": \"CIN-ABG\",\n"
			+ "    \"qty\": 25521.25,\n"
			+ "    \"category\": \"LABOR\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 40,\n"
			+ "    \"system\": \"CIN-SLITINSPECT\",\n"
			+ "    \"qty\": 86.609375,\n"
			+ "    \"category\": \"WASTE\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 40,\n"
			+ "    \"system\": \"CIN-SLITINSPECT\",\n"
			+ "    \"qty\": 25521.25,\n"
			+ "    \"category\": \"LABOR\"\n"
			+ "  },\n"
			+ "  {\n"
			+ "    \"op\": 10,\n"
			+ "    \"system\": \"CIN-STAGING\",\n"
			+ "    \"qty\": 0.25,\n"
			+ "    \"category\": \"LABOR\"\n"
			+ "  }\n"
			+ "]";
		
		final JSONArray a = new JSONArray ( new JSONTokener ( arrayData ) );

		JsonUtil.sortArrayOfObjects ( a, "op" );

		assertEquals ( 10, a.getJSONObject ( 0 ).getInt ( "op" ) );
	}

	@Test
	public void testOverlay ()
	{
		final JSONObject target = new JSONObject ()
			.put ( "foo", 123 )
		;

		final JSONObject overlay1 = new JSONObject ()
			.put ( "foo", new JSONObject () )
		;

		final JSONObject updated = JsonUtil.overlay ( target, overlay1 );
		assertTrue ( updated == target );
		assertEquals ( 0, updated.getJSONObject ( "foo" ).keySet ().size () );

		final JSONObject overlay2 = new JSONObject ()
			.put ( "foo", new JSONObject ()
				.put ( "bar", 123.0 )
				.put ( "monkey", new JSONObject ()
					.put ( "bars", 3 )
					.put ( "brains", 50 )
				)
			)
		;
		JsonUtil.overlay ( updated, overlay2 );

		final JSONObject overlay3 = new JSONObject ()
			.put ( "foo", new JSONObject ()
				.put ( "monkey", new JSONObject ()
					.put ( "bars", 4 )
				)
			)
		;
		JsonUtil.overlay ( updated, overlay3 );

		assertEquals ( 4, updated.getJSONObject ( "foo" ).getJSONObject ( "monkey" ).getInt ( "bars" ) );
	}

	@Test
	public void testDateUtil ()
	{
		final Date d = new Date ( 1669151673000L );
		final JSONObject obj = new JSONObject ();
		JsonUtil.writeDate ( obj, "date", d );
		assertEquals ( "2022-11-22", obj.getString ( "date" ) );

		final LocalDate e = JsonUtil.readDate ( obj, "date" );
		assertEquals ( 2022, e.getYear () );
		assertEquals ( Month.NOVEMBER, e.getMonth () );
		assertEquals ( 22, e.getDayOfMonth () );
	}
}
