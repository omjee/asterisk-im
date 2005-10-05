/**
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */
package org.jivesoftware.phone;

import org.jivesoftware.messenger.Session;
import org.jivesoftware.messenger.interceptor.PacketInterceptor;
import org.jivesoftware.messenger.interceptor.PacketRejectedException;
import org.jivesoftware.phone.element.PhoneStatus;
import org.jivesoftware.phone.util.UserPresenceUtil;
import org.jivesoftware.util.Log;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Prevents presence from changing while a user is on the phone. It will however queue the presence packet to be applied
 * once the user has hung up the phone.
 *
 * @author Andrew Wright
 */
public class OnPhonePacketInterceptor implements PacketInterceptor {

    public void interceptPacket(Packet packet, Session session, boolean read, boolean processed)
            throws PacketRejectedException {


        if (!processed) {

            if (packet instanceof Presence) {


                JID from = packet.getFrom();

                if (from != null) {
                    String username = from.getNode();

                    Collection<Presence> presences = UserPresenceUtil.getPresences(username);

                    // If the user is on the phone (there are presences) then
                    // queue the new presence and reject the packet
                    if (presences != null && presences.size() > 0) {

                        // find an existing old presence packet for this full jid and replace it with the new one
                        for (Presence presence : presences) {

                            if (presence.getFrom().equals(packet.getFrom())) {
                                Log.debug("OnPhonePacketInterceptor removing old presence for jid " + packet.getFrom());
                                presences.remove(presence);
                            }

                        }
                        Log.debug("OnPhonePacketInterceptor adding presence for jid " + packet.getFrom());
                        presences.add((Presence) packet);

                        //Throw an exception to prevent the presence from being processed any further
                        Log.debug("OnPhonePacketInterceptor Rejecting presence packet for jid " + packet.getFrom());
                        throw new PacketRejectedException("Status will change after user is off the phone!");
                    }
                    else if (CallSessionFactory.getCallSessionFactory().getUserCallSessions(username).size() > 0) {

                        Log.debug("OnPhonePacketInterceptor: No existing presence, cacheing current presence setting presence to Away:On Phone");

                        // if the user is on the phone, but we don't have any sessions for them (they have just logged in)
                        presences = new ArrayList<Presence>();

                        Presence presence = (Presence) packet;

                        // Added here, in case they logged on during the phone call
                        presences.add(presence.createCopy());
                        UserPresenceUtil.setPresences(username, presences);

                        // Iterate through all of the sessions sending out new presences for each
                        //Presence presence = new Presence();
                        presence.setShow(Presence.Show.away);
                        presence.setStatus("On the phone");
                        presence.setFrom(from);


                        PhoneStatus phoneStatus = new PhoneStatus(PhoneStatus.Status.ON_PHONE);
                        presence.getElement().add(phoneStatus);

                    }
                }

            }

        }

    }

}
