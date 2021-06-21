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
package Pasazer;

import GUI.GUIFederate;
import Pociag.Pociag;
import hla.rti1516e.*;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import org.portico.impl.hla1516e.types.encoding.HLA1516eInteger32BE;
import org.portico.impl.hla1516e.types.encoding.HLA1516eInteger64BE;

import Pociag.Pociag;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link GUIFederate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback information.
 */
public class PasazerFederateAmbassador extends NullFederateAmbassador {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private PasazerFederate federate;

    // these variables are accessible in the package
    protected double federateTime = 0.0;
    protected double federateLookahead = 1.0;

    protected boolean isRegulating = false;
    protected boolean isConstrained = false;
    protected boolean isAdvancing = false;

    protected boolean isAnnounced = false;
    protected boolean isReadyToRun = false;


    protected boolean isRunning = true;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public PasazerFederateAmbassador(PasazerFederate federate) {
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
        if (label.equals(GUIFederate.READY_TO_RUN))
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(GUIFederate.READY_TO_RUN))
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
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the attribute information
        builder.append(", attributeCount=" + theAttributes.size());
        builder.append("\n");
        for (AttributeHandle attributeHandle : theAttributes.keySet()) {
            // print the attibute handle
            builder.append("\tattributeHandle=");

            // if we're dealing with Flavor, decode into the appropriate enum value
            if (attributeHandle.equals(federate.storageAvailableHandle)) {
                builder.append(attributeHandle);
                builder.append(" (Available)    ");
                builder.append(", attributeValue=");
                HLAinteger32BE available = new HLA1516eInteger32BE();
                try {
                    available.decode(theAttributes.get(attributeHandle));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(available.getValue());
                federate.storageAvailable = available.getValue();
            } else if (attributeHandle.equals(federate.storageMaxHandle)) {
                builder.append(attributeHandle);
                builder.append(" (Max)");
                builder.append(", attributeValue=");
                HLAinteger32BE max = new HLA1516eInteger32BE();
                try {
                    max.decode(theAttributes.get(attributeHandle));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(max.getValue());
                federate.storageMax = max.getValue();
            } else if (attributeHandle.equals(federate.storageMaxHandle)) {
                builder.append(attributeHandle);
                builder.append(" (Max)");
                builder.append(", attributeValue=");
                HLAinteger32BE max = new HLA1516eInteger32BE();
                try {
                    max.decode(theAttributes.get(attributeHandle));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(max.getValue());
                federate.storageMax = max.getValue();
            } else {
                builder.append(attributeHandle);
                builder.append(" (Unknown)   ");
            }

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
        if (interactionClass.equals(this.federate.SzukajMiejscaHandle)) {
            builder.append(" (Pasazer szuka miejsca)");
        }

        if (interactionClass.equals(this.federate.addNewPasazerHandle)) {
            builder.append(" (Dodanie nowego pasazera)");
        }
        if( interactionClass.equals(federate.stopSimulationHandle) )
        {
            builder.append( " (stopSimulationHandle)" );
            isRunning = false;
        }

        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=").append(((HLAfloat64Time) time).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=").append(theParameters.size());
        builder.append("\n");
        for (ParameterHandle parameter : theParameters.keySet()) {
            if (parameter.equals(federate.countNewPasazerHandle)) {
                HLAinteger32BE deco = new HLA1516eInteger32BE();
                try {
                    deco.decode(theParameters.get(parameter));
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
                builder.append(deco.getValue());
                federate.storageMax = deco.getValue();
                int liczbaPasazerowDoStworzenia = deco.getValue();
                builder.append("Liczba pasazerow do stworzenia:  " + "\n").append(liczbaPasazerowDoStworzenia);

                try {
                    federate.sendPassenger(liczbaPasazerowDoStworzenia);
                } catch (FederateNotExecutionMember federateNotExecutionMember) {
                    federateNotExecutionMember.printStackTrace();
                } catch (NotConnected notConnected) {
                    notConnected.printStackTrace();
                } catch (NameNotFound nameNotFound) {
                    nameNotFound.printStackTrace();
                } catch (InvalidInteractionClassHandle invalidInteractionClassHandle) {
                    invalidInteractionClassHandle.printStackTrace();
                } catch (RTIinternalError rtIinternalError) {
                    rtIinternalError.printStackTrace();
                } catch (InteractionClassNotPublished interactionClassNotPublished) {
                    interactionClassNotPublished.printStackTrace();
                } catch (InteractionParameterNotDefined interactionParameterNotDefined) {
                    interactionParameterNotDefined.printStackTrace();
                } catch (InteractionClassNotDefined interactionClassNotDefined) {
                    interactionClassNotDefined.printStackTrace();
                } catch (SaveInProgress saveInProgress) {
                    saveInProgress.printStackTrace();
                } catch (RestoreInProgress restoreInProgress) {
                    restoreInProgress.printStackTrace();
                }


            }

            // print the parameter handle
            builder.append("\tparamHandle=");
            builder.append(parameter);
            // print the parameter value
            builder.append(", paramValue=");
            builder.append(theParameters.get(parameter).length);
            builder.append(" bytes");
            builder.append("\n");

        }

        if (interactionClass.equals(federate.SzukajMiejscaHandle)) {
            builder.append(" (Pasazer szuka miejsca)");
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
    public int getStandPassengerSize() {
        int standPassengerSize = 0;
        standPassengerSize = Pociag.getInstance().getPasazerowieWagonListSizeFromPociag(0) +
                Pociag.getInstance().getPasazerowieWagonListSizeFromPociag(1) +
                Pociag.getInstance().getPasazerowieWagonListSizeFromPociag(2) +
                Pociag.getInstance().getPasazerowieWagonListSizeFromPociag(3);

        return standPassengerSize;
    }

}
