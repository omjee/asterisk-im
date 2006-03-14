package org.jivesoftware.phone.database;

import org.jivesoftware.phone.PhoneUser;
import org.jivesoftware.phone.PhoneDevice;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Andrew Wright
 */
public interface PhoneDAO {

    PhoneUser getPhoneUserByDevice(String device);

    PhoneUser getByUsername(String username);

    PhoneUser getPhoneUserByID(long phoneUserID);

    void remove(PhoneUser phoneUser);

    List<PhoneUser> getPhoneUsers();

    PhoneDevice getDevice(String deviceName);

    PhoneDevice getPhoneDeviceByID(long id);

    List<PhoneDevice> getPhoneDeviceByUserID(long userID);

    void insert(PhoneUser user);

    void update(PhoneUser user);

    void update(PhoneDevice device);

    void insert(PhoneDevice device);

    void remove(PhoneDevice device);

    /**
     * Returns the primary device for a {@link PhoneUser}
     *
     * @param phoneUserID the id of the phone user
     * @return the primary device for the phone user
     */
    PhoneDevice getPrimaryDevice(long phoneUserID);

    List<PhoneDevice> getPhoneDevicesByUsername(String username);
}