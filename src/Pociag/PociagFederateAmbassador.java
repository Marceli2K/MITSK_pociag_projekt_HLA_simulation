/*
 *   Copyright 2012 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL)
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package Pociag;

import Pasazer.Pasazer;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Time;

import java.util.Random;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link PociagFederate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback information.
 */
public class PociagFederateAmbassador extends NullFederateAmbassador {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private PociagFederate federate;

    // these variables are accessible in the package
    protected double federateTime = 0.0;
    protected double federateLookahead = 1.0;

    protected boolean isRegulating = false;
    protected boolean isConstrained = false;
    protected boolean isAdvancing = false;

    protected boolean isAnnounced = false;
    protected boolean isReadyToRun = false;

    protected boolean isRunning = true;
    protected int numberOfxVariable;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public PociagFederateAmbassador(PociagFederate federate) {
        this.federate = federate;
    }

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------
    private void log(String message) {
        System.out.println("FederateAmbassador: " + message);
    }

    //////////////////////////////////////////////////////////////////////////
    ////////////////////////// RTI Callback Methods //////////////////////////
    //////////////////////////////////////////////////////////////////////////
    @Override
    public void synchronizationPointRegistrationFailed(String label,
                                                       SynchronizationPointFailureReason reason) {
        log("Failed to register sync point: " + label + ", reason=" + reason);
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    @Override
    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(PociagFederate.READY_TO_RUN))
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(PociagFederate.READY_TO_RUN))
            this.isReadyToRun = true;
    }

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    @Override
    public void timeRegulationEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject,
                                       ObjectClassHandle theObjectClass,
                                       String objectName)
            throws FederateInternalError {
        log("Discoverd Object: handle=" + theObject + ", classHandle=" +
                theObjectClass + ", name=" + objectName);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] tag,
                                       OrderType sentOrder,
                                       TransportationTypeHandle transport,
                                       SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        reflectAttributeValues(theObject,
                theAttributes,
                tag,
                sentOrder,
                transport,
                null,
                sentOrder,
                reflectInfo);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] tag,
                                       OrderType sentOrdering,
                                       TransportationTypeHandle theTransport,
                                       LogicalTime time,
                                       OrderType receivedOrdering,
                                       SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Reflection for object:");

        // print the handle
        builder.append(" handle=" + theObject);
        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above


        // print the attribute information
        builder.append(", attributeCount=" + theAttributes.size());
        builder.append("\n");
        for (AttributeHandle attributeHandle : theAttributes.keySet()) {
            // print the attibute handle
            builder.append("\tattributeHandle=");

            // if we're dealing with Flavor, decode into the appropriate enum value

            builder.append("\n");
        }

        log(builder.toString());
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        this.receiveInteraction(interactionClass,
                theParameters,
                tag,
                sentOrdering,
                theTransport,
                null,
                sentOrdering,
                receiveInfo);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   LogicalTime time,
                                   OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Interaction Received:");

        // print the handle
        builder.append(" handle=" + interactionClass);
        if (interactionClass.equals(federate.AddPasazerHandle)) {
            builder.append(" (AddPasazer)");
        } else if (interactionClass.equals(federate.getProductsHandle)) {
            builder.append(" (GetProducts)");
        } else if (interactionClass.equals(federate.SzukajMiejscaHandle)) {
            builder.append(" (SzukajMiejsca)");

        }
        if( interactionClass.equals(federate.stopSimulationHandle) )
        {
            builder.append( " (stopSimulationHandle)" );
            isRunning = false;
        }

        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=" + theParameters.size());
        builder.append("\n");
        if (interactionClass.equals(federate.SzukajMiejscaHandle)) {
//            builder.append(", pasazer szuka miejsca" );

        }
        if (interactionClass.equals(federate.checkBiletInteractionHandle)) {
            builder.append(" (checkBiletInteraction)");
            try {
                federate.checkInteraction(); // SPrawdzenie biletu przez konduktora
            } catch (RTIexception | InterruptedException rtIexception) {
                rtIexception.printStackTrace();
            }
        }
        for (
                ParameterHandle parameter : theParameters.keySet()) {

            if (parameter.equals(federate.countHandle)) {

            } else {
                // print the parameter handle
                builder.append("\tparamHandle=");
                builder.append(parameter);
                // print the parameter value
                builder.append(", paramValue=");
                builder.append(theParameters.get(parameter).length);
                builder.append(" bytes");
                builder.append("\n");
            }
            if (parameter.equals(federate.passengerObjectHandle)) {
                Pasazer passenger = new Pasazer(0);
                try {
                    passenger.decode(theParameters.get(parameter));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                federate.pociag.registerPassenger(passenger);
                federate.xVariable = 0;
                federate.yVariable = 0;

//              USTALENIE KOORDYNATOW DLA PASAZEROW O ZADANAYM WAGONIE
                int index = passenger.getPrzedzialNR();
                int nr_wagonNRPassenger = passenger.getNR_WagonNR();
                if ((nr_wagonNRPassenger-1) == 0) {
                    federate.xVariable = federate.xVariable + (nr_wagonNRPassenger-1) * 370 +
                            5 + nr_wagonNRPassenger;
                } else if ((nr_wagonNRPassenger-1) == 1) {
                    federate.xVariable = federate.xVariable + (nr_wagonNRPassenger-1) * 370 +
                            5 + (nr_wagonNRPassenger-1);
                } else if ((nr_wagonNRPassenger-1) == 2) {
                    federate.xVariable = federate.xVariable + (nr_wagonNRPassenger-1) * 370 +
                            5 + (nr_wagonNRPassenger-1);
                } else if ((nr_wagonNRPassenger-1) == 3) {
                    federate.xVariable = federate.xVariable + (nr_wagonNRPassenger-1) * 370 +
                            5 + (nr_wagonNRPassenger-1);
                }

//              USTALENIE KOORDYNATOW DLA PASAZEROW O ZADANAYM PRZEDZIALE
                int index2 = index;
                if (index2 == 0) {
                    federate.xVariable = federate.xVariable + index2 * 76 ;
                } else if (index2 == 1) {
                    federate.xVariable = federate.xVariable + index2 * 76 ;
                } else if (index2 == 2) {
                    federate.xVariable = federate.xVariable + index2 * 76 ;
                } else if (index2 == 3) {
                    federate.xVariable = federate.xVariable + index2 * 76 ;
                } else if (index2 == 4) {
                    federate.xVariable = federate.xVariable + index2 * 76 ;
                }
                federate.xVariable = federate.xVariable  + new Random().nextInt(65);
                numberOfxVariable = numberOfxVariable + 1;
//
//              USTALENIE KOORDYNATOW DLA PASAZEROW, KTOPRZY STOJA
                if (index == -1) {
                    federate.xVariable = federate.xVariable  + new Random().nextInt(333);
                    federate.yVariable = new Random().nextInt(85) + 279;
                } else {
                    federate.yVariable = new Random().nextInt(272);
                }
                federate.varx.add(federate.xVariable);
                federate.vary.add(federate.yVariable);

            }
        }

        log(builder.toString());
    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject,
                                     byte[] tag,
                                     OrderType sentOrdering,
                                     SupplementalRemoveInfo removeInfo)
            throws FederateInternalError {
        log("Object Removed: handle=" + theObject);
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
}
