package org.migaman.pentaho.example.dsp;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import mondrian.i18n.LocalizingDynamicSchemaProcessor;
import mondrian.olap.Util;
import mondrian.spi.DynamicSchemaProcessor;

public class ExampleDsp extends LocalizingDynamicSchemaProcessor implements DynamicSchemaProcessor {
	private static final Logger LOGGER = Logger.getLogger(ExampleDsp.class);
	private static final String ADMIN_ROLE_NAME = "Administrator";

	@Override
	public String filter(String schemaUrl, Util.PropertyList connectInfo, InputStream stream) throws Exception {
		boolean isAdmin = false;
		String username;
		List<String> tenants = null;

		IPentahoSession session = PentahoSessionHolder.getSession();

		if (session == null) {
			//Session should never be null. However, if it happens restrict access and log
			username = null;
			LOGGER.warn("Session ist empty!");
		}
		else {
			username = session.getName();
			LOGGER.debug("Session available, username: " + username);

			//Example content from session variable "roles": [Administrator, Authenticated]
			if (session.getAttribute("roles") != null) {
				Object tmpRoles = session.getAttribute("roles");
				if (tmpRoles instanceof List) {
					@SuppressWarnings("unchecked")
					List<SimpleGrantedAuthority> roles = (List<SimpleGrantedAuthority>) tmpRoles;
					isAdmin = hasAdminRole(roles);
				}
			}

			if (!isAdmin) {
				Persistence pers = new Persistence();
				tenants = pers.getTenants(session.getName());

				LOGGER.debug(username + " has the following tenants: " + tenants);
			}
			else {
				LOGGER.debug(username + " has full access");
			}
		}

		//Return the complete Mondrian Schema (xml)
		String schema = super.filter(schemaUrl, connectInfo, stream);

		//Replace security whre clause
		schema = replaceSecurityPattern(schema, isAdmin, username, tenants);

		return schema;
	}

	/**
	 * Replaces the SECURITY_PATTERN clause in the given schema
	 */
	String replaceSecurityPattern(String schema, boolean isAdmin, String username, List<String> tenants) {
		if (isAdmin) {
			return schema.replaceAll("SECURITY_PATTERN#[^#]*#", "1 = 1");
		}
		if (tenants != null && username != null && !username.isEmpty()) {
			String tenantCondition = String.join(", ", tenants);

			Matcher matcher = Pattern.compile("SECURITY_PATTERN#([^#]*)#").matcher(schema);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String clause = matcher.group(1);
				matcher.appendReplacement(sb, clause.replaceAll("%TENANT_IDS%", tenantCondition));
			}
			return matcher.appendTail(sb).toString();
		}
		else {
			return schema.replaceAll("SECURITY_PATTERN#[^#]*#", "1 = 0");

		}
	}

	private boolean hasAdminRole(List<SimpleGrantedAuthority> roles) {
		boolean isAdmin = false;

		for (SimpleGrantedAuthority role : roles) {
			if (role.getAuthority().equalsIgnoreCase(ADMIN_ROLE_NAME)) {
				isAdmin = true;
				break;
			}
		}
		return isAdmin;
	}

}
