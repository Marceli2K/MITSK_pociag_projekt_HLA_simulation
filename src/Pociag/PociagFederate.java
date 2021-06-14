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

import Konduktor.Konduktor;
import Pasazer.Pasazer;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PociagFederate {
    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------
    /**
     * The number of times we will update our attributes and send an interaction
     */
    public static final int ITERATIONS = 20;

    /**
     * The sync point all federates will sync up on before starting
     */
    public static final String READY_TO_RUN = "ReadyToRun";
    public InteractionClassHandle SzukajMiejscaHandle;

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private RTIambassador rtiamb;
    private PociagFederateAmbassador fedamb;  // created when we connect
    private HLAfloat64TimeFactory timeFactory; // set when we join
    protected EncoderFactory encoderFactory;     // set when we join
    public Pociag pociag;
    // caches of handle types - set once we join a federation
    protected ObjectClassHandle storageHandle;
    protected AttributeHandle storageMaxHandle;
    protected AttributeHandle storageAvailableHandle;
    protected InteractionClassHandle AddPasazerHandle;
    protected InteractionClassHandle getProductsHandle;
    protected ParameterHandle countHandle;
    protected InteractionClassHandle newPasazerHandle;
    protected ParameterHandle newClientInteractionClassClientIdParameterHandle;
    protected InteractionClassHandle checkBiletInteractionHandle;
    protected InteractionClassHandle subscribePassengerObjectHandle;
    protected ParameterHandle passengerObjectHandle;

    private InteractionClassHandle sendInformationAboutPassengerForStatistics;
    private ParameterHandle countOfCheckedPassenger;
    private ParameterHandle countOfPassengerWithoutBilet;
    private ParameterHandle countOfPassengerWITHBilet;
    private ParameterHandle countOfPassengerWITHBiletfromALL;
    private ParameterHandle countOfPassengerWITHOUTTBiletfromALL;
    private ParameterHandle CountOfSeatedPassengerInTrain;
//    private InteractionClassHandle SzukajMiejscaHandle;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    /**
     * This is just a helper method to make sure all logging it output in the same form
     */
    private void log(String message) {
        System.out.println("PociagFederate   : " + message);
    }

    /**
     * This method will block until the user presses enter
     */
    private void waitForUser() {
        log(" >>>>>>>>>> Press Enter to Continue <<<<<<<<<<");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (Exception e) {
            log("Error while waiting for user input: " + e.getMessage());
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Main Simulation Method /////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the main simulation loop. It can be thought of as the main method of
     * the federate. For a description of the basic flow of this federate, see the
     * class level comments
     */
    public void runFederate(String federateName) throws Exception {
        /////////////////////////////////////////////////
        // 1 & 2. create the RTIambassador and Connect //
        /////////////////////////////////////////////////
        log("Creating RTIambassador");
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        // connect
        log("Connecting...");
        fedamb = new PociagFederateAmbassador(this);
        rtiamb.connect(fedamb, CallbackModel.HLA_EVOKED);

        //////////////////////////////
        // 3. create the federation //
        //////////////////////////////
        log("Creating Federation...");
        // We attempt to create a new federation with the first three of the
        // restaurant FOM modules covering processes, food and drink
        try {
            URL[] modules = new URL[]{
                    (new File("foms/ProducerConsumer.xml")).toURI().toURL(),
            };

            rtiamb.createFederationExecution("PociagFederation", modules);
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception loading one of the FOM modules from disk: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        ////////////////////////////
        // 4. join the federation //
        ////////////////////////////
        rtiamb.joinFederationExecution(federateName,            // name for the federate
                "PociagFederate",   // federate type
                "PociagFederation"     // name of federation
        );           // modules we want to add

        log("Joined Federation as " + federateName);

        // cache the time factory for easy access
        this.timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();

        ////////////////////////////////
        // 5. announce the sync point //
        ////////////////////////////////
        // announce a sync point to get everyone on the same page. if the point
        // has already been registered, we'll get a callback saying it failed,
        // but we don't care about that, as long as someone registered it
        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        // wait until the point is announced
        while (fedamb.isAnnounced == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        // WAIT FOR USER TO KICK US OFF
        // So that there is time to add other federates, we will wait until the
        // user hits enter before proceeding. That was, you have time to start
        // other federates.
        waitForUser();

        ///////////////////////////////////////////////////////
        // 6. achieve the point and wait for synchronization //
        ///////////////////////////////////////////////////////
        // tell the RTI we are ready to move past the sync point and then wait
        // until the federation has synchronized on
        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (fedamb.isReadyToRun == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // 7. enable time policies //
        /////////////////////////////
        // in this section we enable/disable all time policies
        // note that this step is optional!
        enableTimePolicy();
        log("Time Policy Enabled");

        //////////////////////////////
        // 8. publish and subscribe //
        //////////////////////////////
        // in this section we tell the RTI of all the data we are going to
        // produce, and all the data we want to know about
        publishAndSubscribe();
        log("Published and Subscribed");

        /////////////////////////////////////
        // 9. register an object to update //
        /////////////////////////////////////
        ObjectInstanceHandle objectHandle = this.rtiamb.registerObjectInstance(this.storageHandle);
        log("Registered Pociag, handle=" + objectHandle);

        /////////////////////////////////////
        // 10. do the main simulation loop //
        /////////////////////////////////////
        // here is where we do the meat of our work. in each iteration, we will
        // update the attribute values of the object we registered, and will
        // send an interaction.
        pociag = new Pociag(4);
        int x = 0;

        while (fedamb.isRunning) {
            // update ProductsStorage parameters max and available to current values
            AttributeHandleValueMap attributes = rtiamb.getAttributeHandleValueMapFactory().create(2);

//            HLAinteger32BE maxValue = encoderFactory.createHLAinteger32BE(Pociag.getInstance().getMax());
//            attributes.put(storageMaxHandle, maxValue.toByteArray());

            HLAinteger32BE availableValue = encoderFactory.createHLAinteger32BE(Pociag.getInstance().getAvailable());
            attributes.put(storageAvailableHandle, availableValue.toByteArray());

            rtiamb.updateAttributeValues(objectHandle, attributes, generateTag());
            int getCountOfCheckedPassengerALL = 0;
            int getCountOfPassengerWithoutBiletALL = 0;
            int getCountOfPassengerWITHBiletALL = 0;

            for (Konduktor konduktor : pociag.getKonduktorList()) {
                int getCountOfCheckedPassenger = konduktor.getCountOfCheckedPassenger();
                int getCountOfPassengerWithoutBilet = konduktor.getCountOfPassengerWithoutBilet();
                int getCountOfPassengerWITHBilet = konduktor.getCountOfPassengerWITHBilet();
                System.out.println(getCountOfCheckedPassenger);
                getCountOfCheckedPassengerALL = getCountOfCheckedPassengerALL + getCountOfCheckedPassenger;
                getCountOfPassengerWithoutBiletALL = getCountOfPassengerWithoutBiletALL + getCountOfPassengerWithoutBilet;
                getCountOfPassengerWITHBiletALL = getCountOfPassengerWITHBiletALL + getCountOfPassengerWITHBilet;
                if (getCountOfCheckedPassengerALL != x) {
                    changeNumberAboutPassenger(getCountOfCheckedPassengerALL, getCountOfPassengerWithoutBiletALL, getCountOfPassengerWITHBiletALL);
                    x = getCountOfCheckedPassengerALL;
                }
            }


            advanceTime(fedamb.federateLookahead);
            log("Time Advanced to " + fedamb.federateTime);
        }


        //////////////////////////////////////
        // 11. delete the object we created //
        //////////////////////////////////////
//		deleteObject( objectHandle );
//		log( "Deleted Object, handle=" + objectHandle );

        ////////////////////////////////////
        // 12. resign from the federation //
        ////////////////////////////////////
        rtiamb.resignFederationExecution(ResignAction.DELETE_OBJECTS);
        log("Resigned from Federation");

        ////////////////////////////////////////
        // 13. try and destroy the federation //
        ////////////////////////////////////////
        // NOTE: we won't die if we can't do this because other federates
        //       remain. in that case we'll leave it for them to clean up
        try {
            rtiamb.destroyFederationExecution("ExampleFederation");
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }
    }

    //    wysylanie zmian odosnie liczb pasazerow, tj obecnych i z biletem i bez biletu
    private void changeNumberAboutPassenger(int getCountOfCheckedPassengerALL, int getCountOfPassengerWithoutBiletALL, int getCountOfPassengerWITHBiletALL) throws FederateNotExecutionMember, NotConnected, InvalidInteractionClassHandle, NameNotFound, RTIinternalError, InteractionParameterNotDefined, RestoreInProgress, InteractionClassNotDefined, InteractionClassNotPublished, SaveInProgress, InvalidLogicalTime {
        

        ParameterHandleValueMap parameterHandleValueMap = rtiamb.getParameterHandleValueMapFactory().create(5);

        ParameterHandle countOfCheckedPassengerParameter = rtiamb.getParameterHandle(sendInformationAboutPassengerForStatistics, "countOfCheckedPassenger");
        HLAinteger32BE CountOfCheckedPassenger = encoderFactory.createHLAinteger32BE(getCountOfCheckedPassengerALL);

        ParameterHandle CountOfPassengerWithoutBiletParameter = rtiamb.getParameterHandle(sendInformationAboutPassengerForStatistics, "countOfPassengerWithoutBilet");
        HLAinteger32BE CountOfPassengerWithoutBiletPassenger = encoderFactory.createHLAinteger32BE(getCountOfPassengerWithoutBiletALL);

        ParameterHandle CountOfPassengerWITHBiletParameter = rtiamb.getParameterHandle(sendInformationAboutPassengerForStatistics, "countOfPassengerWITHBilet");
        HLAinteger32BE CountOfPassengerWITHBilet = encoderFactory.createHLAinteger32BE(getCountOfPassengerWITHBiletALL);

        ParameterHandle CountOfPassengerWITHBiletFromAllParameter = rtiamb.getParameterHandle(sendInformationAboutPassengerForStatistics, "countOfPassengerWITHBiletFromAllPassenger");
        HLAinteger32BE CountOfPassengerWITHBiletFromALL = encoderFactory.createHLAinteger32BE(pociag.passengerWithBilet);

        ParameterHandle CountOfPassengerWITHOUTBiletFromAllParameter = rtiamb.getParameterHandle(sendInformationAboutPassengerForStatistics, "countOfPassengerWITHOUTBiletFromAllPassenger");
        HLAinteger32BE CountOfPassengerWITHOUTBiletFromALL = encoderFactory.createHLAinteger32BE(pociag.passengerWithoutBilet);

        ParameterHandle CountOfSeatedPassengerParameter = rtiamb.getParameterHandle(sendInformationAboutPassengerForStatistics, "CountOfSeatedPassengerInTrain");
        HLAinteger32BE CountOfSeatedPassenger = encoderFactory.createHLAinteger32BE(pociag.getAllPassengerSeated());
        
        

        parameterHandleValueMap.put(countOfCheckedPassengerParameter, CountOfCheckedPassenger.toByteArray());
        parameterHandleValueMap.put(CountOfPassengerWithoutBiletParameter, CountOfPassengerWithoutBiletPassenger.toByteArray());
        parameterHandleValueMap.put(CountOfPassengerWITHBiletParameter, CountOfPassengerWITHBilet.toByteArray());
        parameterHandleValueMap.put(CountOfPassengerWITHBiletFromAllParameter, CountOfPassengerWITHBiletFromALL.toByteArray());
        parameterHandleValueMap.put(CountOfPassengerWITHOUTBiletFromAllParameter, CountOfPassengerWITHOUTBiletFromALL.toByteArray());
        parameterHandleValueMap.put(CountOfSeatedPassengerParameter, CountOfSeatedPassenger.toByteArray());
        
        rtiamb.sendInteraction(this.sendInformationAboutPassengerForStatistics, parameterHandleValueMap, generateTag());

    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helper Methods //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * This method will attempt to enable the various time related properties for
     * the federate
     */
    private void enableTimePolicy() throws Exception {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        HLAfloat64Interval lookahead = timeFactory.makeInterval(fedamb.federateLookahead);

        ////////////////////////////
        // enable time regulation //
        ////////////////////////////
        this.rtiamb.enableTimeRegulation(lookahead);

        // tick until we get the callback
        while (fedamb.isRegulating == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // enable time constrained //
        /////////////////////////////
        this.rtiamb.enableTimeConstrained();

        // tick until we get the callback
        while (fedamb.isConstrained == false) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    /**
     * This method will inform the RTI about the types of data that the federate will
     * be creating, and the types of data we are interested in hearing about as other
     * federates produce it.
     */
    private void publishAndSubscribe() throws RTIexception {
//		publish ProductsStrorage object
        this.storageHandle = rtiamb.getObjectClassHandle("HLAobjectRoot.Pociag");
        this.storageMaxHandle = rtiamb.getAttributeHandle(storageHandle, "max");
        this.storageAvailableHandle = rtiamb.getAttributeHandle(storageHandle, "available");
//		// package the information into a handle set
        AttributeHandleSet attributes = rtiamb.getAttributeHandleSetFactory().create();
        attributes.add(storageMaxHandle);
        attributes.add(storageAvailableHandle);
//
        rtiamb.publishObjectClassAttributes(storageHandle, attributes);

//        subscribe szukaj miejsca
        String iname = "HLAinteractionRoot.PasazerManagment.SzukajMiejsca";
        SzukajMiejscaHandle = rtiamb.getInteractionClassHandle(iname);
        //get count parameter for PasazerManagment Interaction
        countHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.SzukajMiejsca"), "count");
        rtiamb.subscribeInteractionClass(SzukajMiejscaHandle);

//        subscribe cehckbilet interaction
        String inames = "HLAinteractionRoot.PasazerManagment.CheckBilet";
        checkBiletInteractionHandle = rtiamb.getInteractionClassHandle(inames);
        this.rtiamb.subscribeInteractionClass(this.checkBiletInteractionHandle);


//      subscribe subscribePassengerObjectHandle interaction
        subscribePassengerObjectHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.SendPassengerObjectToTrainAndStatistics");
        //get object parameter for subscribePassengerObjectHandle Interaction
        passengerObjectHandle = rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.SendPassengerObjectToTrainAndStatistics"), "passengerObject");
        rtiamb.subscribeInteractionClass(subscribePassengerObjectHandle);

        
        ////	publish sendInformationAboutPassengerForStatistics interaction
        this.sendInformationAboutPassengerForStatistics = this.rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.InformationAboutPassengerForStatistics");
        this.countOfCheckedPassenger = this.rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.InformationAboutPassengerForStatistics"), "countOfCheckedPassenger");
        this.countOfPassengerWithoutBilet = this.rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.InformationAboutPassengerForStatistics"), "countOfPassengerWithoutBilet");
        this.countOfPassengerWITHBilet = this.rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.InformationAboutPassengerForStatistics"), "countOfPassengerWITHBilet");
        this.countOfPassengerWITHBiletfromALL = this.rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.InformationAboutPassengerForStatistics"), "countOfPassengerWITHBiletFromAllPassenger");
        this.countOfPassengerWITHOUTTBiletfromALL = this.rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.InformationAboutPassengerForStatistics"), "countOfPassengerWITHOUTBiletFromAllPassenger");
        this.CountOfSeatedPassengerInTrain = this.rtiamb.getParameterHandle(rtiamb.getInteractionClassHandle("HLAinteractionRoot.PasazerManagment.InformationAboutPassengerForStatistics"), "CountOfSeatedPassengerInTrain");

        // do the publication
        this.rtiamb.publishInteractionClass(this.sendInformationAboutPassengerForStatistics);


    }

    /**
     * This method will request a time advance to the current time, plus the given
     * timestep. It will then wait until a notification of the time advance grant
     * has been received.
     */
    protected void advanceTime(double timestep) throws RTIexception {
        // request the advance
        fedamb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime(fedamb.federateTime + timestep);
        rtiamb.timeAdvanceRequest(time);

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while (fedamb.isAdvancing) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);

        }
    }

    private short getTimeAsShort() {
        return (short) fedamb.federateTime;
    }

    private byte[] generateTag() {
        return ("(timestamp) " + System.currentTimeMillis()).getBytes();
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main(String[] args) {
        // get a federate name, use "exampleFederate" as default
        String federateName = "PociagFederate";
        if (args.length != 0) {
            federateName = args[0];
        }

        try {
            // run the example federate
            new PociagFederate().runFederate(federateName);
        } catch (Exception rtie) {
            // an exception occurred, just log the information and exit
            rtie.printStackTrace();
        }
    }

    protected void checkInteraction() throws RTIexception, InterruptedException {
        for (Wagon wagon : pociag.getWagonList()) {
            List<Pasazer> passengerList;
            passengerList = wagon.getListPassengerInWagon();
            int index = 0;
            index = pociag.getWagonList().indexOf(wagon);
            for (Pasazer pasazer : passengerList) {
                if (!pasazer.checked) {
                    pociag.konduktorList.get(index).checkBilet(pasazer);
                    break;
                }
            }
        }
    }
}