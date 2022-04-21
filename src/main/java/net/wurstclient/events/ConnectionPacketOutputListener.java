/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.events;

import net.minecraft.network.IPacket;
import net.wurstclient.event.CancellableEvent;
import net.wurstclient.event.Listener;
import net.wurstclient.events.PacketOutputListener.PacketOutputEvent;

import java.util.ArrayList;

/**
 * Similar to {@link PacketOutputListener}, but also captures packets that are
 * sent before the client has finished connecting to the server. Most hacks
 * should use {@link PacketOutputListener} instead.
 */
public interface ConnectionPacketOutputListener extends Listener
{
	public void onSentConnectionPacket(ConnectionPacketOutputEvent event);
	
	/**
	 * Similar to {@link PacketOutputEvent}, but also captures packets that are
	 * sent before the client has finished connecting to the server. Most hacks
	 * should use {@link PacketOutputEvent} instead.
	 */
	public static class ConnectionPacketOutputEvent
		extends CancellableEvent<ConnectionPacketOutputListener>
	{
		private IPacket<?> packet;
		
		public ConnectionPacketOutputEvent(IPacket<?> packet)
		{
			this.packet = packet;
		}
		
		public IPacket<?> getPacket()
		{
			return packet;
		}
		
		public void setPacket(IPacket<?> packet)
		{
			this.packet = packet;
		}
		
		@Override
		public void fire(ArrayList<ConnectionPacketOutputListener> listeners)
		{
			for(ConnectionPacketOutputListener listener : listeners)
			{
				listener.onSentConnectionPacket(this);
				
				if(isCancelled())
					break;
			}
		}
		
		@Override
		public Class<ConnectionPacketOutputListener> getListenerType()
		{
			return ConnectionPacketOutputListener.class;
		}
	}
}