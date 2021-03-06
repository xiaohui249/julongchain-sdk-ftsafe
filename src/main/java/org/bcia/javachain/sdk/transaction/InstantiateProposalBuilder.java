/*
 *  Copyright 2016 DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.bcia.javachain.sdk.transaction;

import static org.bcia.javachain.sdk.transaction.ProtoUtils.createDeploymentSpec;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.javachain.sdk.SmartContractEndorsementPolicy;
import org.bcia.javachain.sdk.TransactionRequest;
import org.bcia.javachain.sdk.exception.InvalidArgumentException;
import org.bcia.javachain.sdk.exception.ProposalException;
import org.bcia.julongchain.protos.node.SmartContractPackage;
import org.bcia.julongchain.protos.node.SmartContractPackage.SmartContractDeploymentSpec;
import org.bcia.julongchain.protos.node.ProposalPackage;

/**
 * modified for Node,SmartContract,Consenter,
 * Group,TransactionPackage,TransactionResponsePackage,
 * EventsPackage,ProposalPackage,ProposalResponsePackage
 * by wangzhe in ftsafe 2018-07-02
 */
public class InstantiateProposalBuilder extends LSSCProposalBuilder {

    private static final Log logger = LogFactory.getLog(InstantiateProposalBuilder.class);

    private String smartContractPath;

    private String smartContractName;
    private List<String> argList;
    private String smartContractVersion;
    private TransactionRequest.Type smartContractType = TransactionRequest.Type.JAVA;

    private byte[] smartContractPolicy = null;
    protected String action = "deploy";

    public void setTransientMap(Map<String, byte[]> transientMap) throws InvalidArgumentException {
        if (null == transientMap) {

            throw new InvalidArgumentException("Transient map may not be null");

        }
        this.transientMap = transientMap;
    }

    protected InstantiateProposalBuilder() {
        super();
    }

    public static InstantiateProposalBuilder newBuilder() {
        return new InstantiateProposalBuilder();

    }

    public InstantiateProposalBuilder smartContractPath(String smartContractPath) {

        this.smartContractPath = smartContractPath;

        return this;

    }

    public InstantiateProposalBuilder smartContractName(String smartContractName) {

        this.smartContractName = smartContractName;

        return this;

    }

    public InstantiateProposalBuilder smartContractType(TransactionRequest.Type smartContractType) {

        this.smartContractType = smartContractType;

        return this;

    }

    public void smartContractEndorsementPolicy(SmartContractEndorsementPolicy policy) {
        if (policy != null) {
            this.smartContractPolicy = policy.getSmartContractEndorsementPolicyAsBytes();
        }
    }

    public InstantiateProposalBuilder argss(List<String> argList) {
        this.argList = argList;
        return this;
    }

    @Override
    public ProposalPackage.Proposal build() throws ProposalException, InvalidArgumentException {

        constructInstantiateProposal();
        return super.build();
    }

    private void constructInstantiateProposal() throws ProposalException, InvalidArgumentException {

        try {

            createNetModeTransaction();

        } catch (InvalidArgumentException exp) {
            logger.error(exp);
            throw exp;
        } catch (Exception exp) {
            logger.error(exp);
            throw new ProposalException("IO Error while creating install transaction", exp);
        }
    }

    private void createNetModeTransaction() throws InvalidArgumentException {
        logger.debug("NetModeTransaction");

        if (smartContractType == null) {
            throw new InvalidArgumentException("SmartContract type is required");
        }

        List<String> modlist = new LinkedList<>();
        modlist.add("init");
        modlist.addAll(argList);

        switch (smartContractType) {
            case JAVA:
                ccType(SmartContractPackage.SmartContractSpec.Type.JAVA);
                break;
            case NODE:
                ccType(SmartContractPackage.SmartContractSpec.Type.NODE);
                break;
            case GO_LANG:
                ccType(SmartContractPackage.SmartContractSpec.Type.GOLANG);
                break;
            default:
                throw new InvalidArgumentException("Requested smartContract type is not supported: " + smartContractType);
        }

        SmartContractDeploymentSpec depspec = createDeploymentSpec(ccType,
                smartContractName, smartContractPath, smartContractVersion, modlist, null);

        List<ByteString> argList = new ArrayList<>();
        argList.add(ByteString.copyFrom(action, StandardCharsets.UTF_8));
        argList.add(ByteString.copyFrom(context.getGroupID(), StandardCharsets.UTF_8));
        argList.add(depspec.toByteString());
        if (smartContractPolicy != null) {
            argList.add(ByteString.copyFrom(smartContractPolicy));
        }

        args(argList);

    }

    public void smartContractVersion(String smartContractVersion) {
        this.smartContractVersion = smartContractVersion;
    }
}