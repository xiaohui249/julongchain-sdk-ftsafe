/*
 *  Copyright 2016 DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.bcia.javachain.sdk.transaction;

import org.bcia.javachain.sdk.TransactionRequest;
import org.bcia.javachain.sdk.exception.ProposalException;
import org.bcia.javachain.sdk.transaction.InstallProposalBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class InstallProposalBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // Create a temp folder to hold temp files for various file I/O operations
    // These are automatically deleted when each test completes
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testBuildNoSmartContract() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing chaincodeSource or chaincodeInputStream");

        InstallProposalBuilder builder = createTestBuilder();

        builder.build();

    }

    // Tests that both chaincodeSource and chaincodeInputStream are not specified together
    @Test
    public void testBuildBothSmartContractSources() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Both chaincodeSource and chaincodeInputStream");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractSource(new File("some/dir"));
        builder.setSmartContractInputStream(new ByteArrayInputStream("test string".getBytes()));

        builder.build();
    }

    // Tests that a chaincode path has been specified for GO_LANG code using a File
    @Test
    public void testBuildSmartContractPathGolangFile() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing chaincodePath");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.GO_LANG);
        builder.setSmartContractSource(new File("some/dir"));
        builder.chaincodePath(null);

        builder.build();
    }

    // Tests that a chaincode path has been specified for GO_LANG code using an InputStream
    @Test
    public void testBuildSmartContractPathGolangStream() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Missing chaincodePath");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.GO_LANG);
        builder.setSmartContractInputStream(new ByteArrayInputStream("test string".getBytes()));
        builder.chaincodePath(null);

        builder.build();
    }

    // Tests that a chaincode path is null for JAVA code using a File
    @Test
    public void testBuildSmartContractPathJavaFile() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("chaincodePath must be null for Java chaincode");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.JAVA);
        builder.setSmartContractSource(new File("some/dir"));
        builder.chaincodePath("");

        builder.build();
    }

    // Tests that a chaincode path is null for JAVA code using a File
    @Test
    public void testBuildSmartContractPathJavaStream() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("chaincodePath must be null for Java chaincode");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.JAVA);
        builder.setSmartContractInputStream(new ByteArrayInputStream("test string".getBytes()));
        builder.chaincodePath("");

        builder.build();
    }

    // Tests for non existent chaincode source path
    @Test
    public void testBuildSourceNotExistGolang() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The project source directory does not exist");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.JAVA);
        builder.chaincodePath(null);
        builder.setSmartContractSource(new File("some/dir"));

        builder.build();
    }

    // Tests for a chaincode source path which is a file and not a directory
    @Test
    public void testBuildSourceNotDirectory() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The project source directory is not a directory");

        InstallProposalBuilder builder = createTestBuilder();

        // create an empty src directory
        File sourceDir = tempFolder.newFolder("src");

        // Create a dummy file in the chaincode directory
        String dummyFileName = "myapp";
        File dummyFile = new File(sourceDir, dummyFileName);
        dummyFile.createNewFile();

        builder.chaincodePath(dummyFileName);
        builder.setSmartContractSource(tempFolder.getRoot().getAbsoluteFile());

        builder.build();
    }

    // Tests for an IOException on the stream
    @Test
    public void testBuildInvalidSource() throws Exception {

        // A mock InputStream that throws an IOException
        class MockInputStream extends InputStream {
            @Override
            public int read() throws IOException {
                throw new IOException("Cannot read!");
            }
        }

        thrown.expect(ProposalException.class);
        thrown.expectMessage("IO Error");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.JAVA);
        builder.setSmartContractInputStream(new MockInputStream());

        builder.build();
    }

    // Tests that no chaincode path is specified for Node code using a File
    @Test
    public void testBuildSmartContractPathNodeFile() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("chaincodePath must be null for Node chaincode");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.NODE);
        builder.setSmartContractSource(new File("some/dir"));
        builder.chaincodePath("src");

        builder.build();
    }

    // Tests that no chaincode path is specified for Node code using input stream
    @Test
    public void testBuildSmartContractPathNodeStream() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("chaincodePath must be null for Node chaincode");

        InstallProposalBuilder builder = createTestBuilder();

        builder.setSmartContractLanguage(TransactionRequest.Type.NODE);
        builder.setSmartContractInputStream(new ByteArrayInputStream("test string".getBytes()));
        builder.chaincodePath("src");

        builder.build();
    }
    // ==========================================================================================
    // Helper methods
    // ==========================================================================================

    // Instantiates a basic InstallProposalBuilder with no chaincode source specified
    private InstallProposalBuilder createTestBuilder() {

        InstallProposalBuilder builder = InstallProposalBuilder.newBuilder();

        builder.chaincodeName("mycc");
        builder.chaincodeVersion("1.0");
        builder.setSmartContractLanguage(TransactionRequest.Type.GO_LANG);

        return builder;
    }

}
