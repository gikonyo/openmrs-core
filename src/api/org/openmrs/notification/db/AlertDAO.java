package org.openmrs.notification.db;

import java.util.List;
import java.util.Set;

import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.notification.Alert;

public interface AlertDAO {
	public void createAlert(Alert alert) throws DAOException;

	public Alert getAlert(Integer alertId) throws DAOException;

	public void updateAlert(Alert alert) throws DAOException;

	public List<Alert> getAlerts(User user, Set<Role> userRoles, boolean includeRead, boolean includeVoided) throws DAOException;
	
	public List<Alert> getAllAlerts(boolean includeExpired) throws DAOException;
	
}
