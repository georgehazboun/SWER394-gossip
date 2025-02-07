/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.gossip.examples;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.RemoteGossipMember;

/**
 * This class is an example of how one could use the gossip service. Here we
 * start multiple gossip
 * clients on this host as specified in the config file.
 * 
 * @author harmenw
 */
public class GossipExample extends Thread {
  /** The number of clients to start. */
  private static final int NUMBER_OF_CLIENTS = 1000;

  /**
   * @param args
   */
  public static void main(String[] args) {
    new GossipExample();
  }

  /**
   * Constructor. This will start the this thread.
   */
  public GossipExample() {
    start();
  }

  /**
   * @see java.lang.Thread#run()
   */
  public void run() {
    try {
      GossipSettings settings = new GossipSettings();

      List<GossipService> clients = new ArrayList<>();

      // Get my ip address.
      String myIpAddress = InetAddress.getLocalHost().getHostAddress();

      String cluster = "My Gossip Cluster";

      // Create the gossip members and put them in a list and give them a port number
      // starting with
      // 2000.
      List<GossipMember> startupMembers = new ArrayList<>();
      for (int i = 0; i < NUMBER_OF_CLIENTS; ++i) {
        startupMembers.add(new RemoteGossipMember(cluster, myIpAddress, 2000 + i, ""));
      }

      // Lets start the gossip clients.
      // Start the clients, waiting cleaning-interval + 1 second between them which
      // will show the
      // dead list handling.
      for (GossipMember member : startupMembers) {
        GossipService gossipService = new GossipService(cluster, myIpAddress, member.getPort(), "",
            startupMembers, settings, null);
        clients.add(gossipService);
        gossipService.start();
        // sleep(settings.getCleanupInterval() + 1000);
      }

      // After starting all gossip clients, first wait 10 seconds and then shut them
      // down.
      sleep(10000);
      System.err.println("Going to shutdown all services...");
      // Since they all run in the same virtual machine and share the same executor,
      // if one is
      // shutdown they will all stop.
      clients.get(0).shutdown();
      clients.get(1).shutdown();
      clients.get(2).shutdown();
      clients.get(3).shutdown();

    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
