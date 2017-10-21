package org.migaman.pentaho.example.dsp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.migaman.pentaho.example.dsp.ExampleDsp;

public class ExampleDspTest {

	private static final String SCHEMA_EXAMPLE = "<![CDATA[SECURITY_PATTERN#ExampleCube.idTenant IN (%TENANT_IDS%)#]]>";

	
	@Test
	public void checkAdminUser() {
		ExampleDsp e = new ExampleDsp();
		String expected = "<![CDATA[1 = 1]]>";
		String actual = e.replaceSecurityPattern(SCHEMA_EXAMPLE, true, "admin", null);
		assertEquals(expected, actual);
	}

	@Test
	public void checkNoUser() {
		ExampleDsp e = new ExampleDsp();
		String expected = "<![CDATA[1 = 0]]>";
		String actual = e.replaceSecurityPattern(SCHEMA_EXAMPLE, false, null, null);
		assertEquals(expected, actual);
	}

	@Test
	public void checkMultiTenantUser() {
		ArrayList<String> tenants = new ArrayList<>();
		tenants.add("1");
		tenants.add("2");

		ExampleDsp e = new ExampleDsp();
		String expected = "<![CDATA[ExampleCube.idTenant IN (1, 2)]]>";
		String actual = e.replaceSecurityPattern(SCHEMA_EXAMPLE, false, "username", tenants);
		assertEquals(expected, actual);

	}
	
	@Test
	public void checkNullTenant() {
		ArrayList<String> tenants = null;

		ExampleDsp e = new ExampleDsp();
		String expected = "<![CDATA[1 = 0]]>";
		String actual = e.replaceSecurityPattern(SCHEMA_EXAMPLE, false, "username", tenants);
		assertEquals(expected, actual);

	}

	
}
