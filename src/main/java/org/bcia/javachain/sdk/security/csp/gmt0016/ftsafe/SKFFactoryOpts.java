/**
 * Copyright Feitian. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe;

import org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.ec.ECCCipherBlob;
import org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.ec.ECCDer;
import org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.ec.ECCEnvelopedKeyBlob;
import org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.ec.ECCSignatureBlob;
import org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.util.BlockCipherParam;
import org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.util.DataUtil;
import org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.util.SKFCspKey;
import org.bcia.javachain.common.exception.JCSKFException;
import org.bcia.javachain.common.exception.SarException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bcia.javachain.sdk.security.csp.gmt0016.ftsafe.GMT0016CspConstant.*;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class SKFFactoryOpts implements ISKFFactory {

    private GMT0016Dll gmtDll;

    public void InitSKF(String lib) {
        gmtDll = null;//GMT0016Dll.getInstance(lib);
    }


    public List<String> SKF_EnumDevs(boolean bPresent) throws SarException, JCSKFException {

        byte[] devNamesByte = null;
        long[] lSize = new long[1];

        //SKF_CatchExcetpt(gmtDll.SKF_EnumDev_N(bPresent, null, lSize));
        if(lSize[0] < 2) {//default exist 0x00
            SKF_CatchJCExcetpt(JCSKFException.JC_SKF_NODEV);
        }

        devNamesByte = new byte[(int)lSize[0]];
        //SKF_CatchExcetpt(gmtDll.SKF_EnumDev_N(bPresent, devNamesByte, lSize));

        String[] devNames=new String(devNamesByte,0,devNamesByte.length).split("\0");
        return new ArrayList<String>(Arrays.asList(devNames));
    }


    public long SKF_ConnectDev(String devName) throws SarException{
        byte[] devNameBytes = devName.getBytes();
        byte[] tempdevNameBytes = new byte[devNameBytes.length + 1];
        System.arraycopy(devNameBytes, 0, tempdevNameBytes, 0, devNameBytes.length);

        long[] lDevHandle = {0};
        //SKF_CatchExcetpt(gmtDll.SKF_ConnectDev_N(tempdevNameBytes, lDevHandle));
        return lDevHandle[0];
    }


    public SKFDeviceInfo SKF_GetDevInfo(long lDevHandle) throws SarException{
        SKF_CheckHandler(lDevHandle);
        byte[] skf_ver = new byte[2];
        byte[] skf_hwver = new byte[2];
        byte[] skf_fwver = new byte[2];
        byte[] manufacturer = new byte[64];
        byte[] issuer = new byte[64];
        byte[] label = new byte[32];
        byte[] serialnumber = new byte[32];
        byte[] reserved = new byte[64];
        long[] algsyscap = new long[1];
        long[] algasymcap = new long[1];
        long[] alghashcap = new long[1];
        long[] devauthalgid = new long[1];;
        long[] totalspace = new long[1];
        long[] freespace = new long[1];
        long[] maxeccbuffersize = new long[1];
        long[] maxbuffersize = new long[1];

//                manufacturer, issuer, label, serialnumber,skf_hwver, skf_fwver, algsyscap,
//                algasymcap, alghashcap, devauthalgid, totalspace, freespace,
//                maxeccbuffersize, maxbuffersize, reserved));

        SKFDeviceInfo devinfo = new SKFDeviceInfo(skf_ver, manufacturer, issuer,
                label, serialnumber, skf_hwver, skf_fwver, algsyscap[0], algasymcap[0], alghashcap[0],
                devauthalgid[0], totalspace[0], freespace[0], maxeccbuffersize[0], maxbuffersize[0], reserved);

        return devinfo;
    }


    public List<String> SKF_EnumApplication(long lDevHandle) throws SarException, JCSKFException{
        SKF_CheckHandler(lDevHandle);


        long[] lSize = new long[1];

//        SKF_CatchExcetpt(gmtDll.SKF_EnumApplication_N(lDevHandle, null, lSize));
        if(lSize[0] < 2) {//default exist 0x00
            SKF_CatchJCExcetpt(JCSKFException.JC_SKF_NOAPP);
        }
        byte[] appNameByte = new byte[(int)lSize[0]];
//        SKF_CatchExcetpt(gmtDll.SKF_EnumApplication_N(lDevHandle, appNameByte, lSize));
        String[] appNames=new String(appNameByte,0,appNameByte.length).split("\0");
        return new ArrayList<String>(Arrays.asList(appNames));
    }


    public long SKF_OpenApplication(long lDevHandle, String appName) throws SarException {
        SKF_CheckHandler(lDevHandle);
        byte[] appNameBytes = appName.getBytes();
        byte[] tempappNameBytes = new byte[appNameBytes.length + 1];
        System.arraycopy(appNameBytes, 0, tempappNameBytes, 0, appNameBytes.length);

        long[] lAppHandle = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_OpenApplication_N(lDevHandle, tempappNameBytes, lAppHandle));
        return lAppHandle[0];
    }


    public void SKF_DisconnectDev(long lDevHandle) throws SarException{
        SKF_CheckHandler(lDevHandle);
//        SKF_CatchExcetpt(gmtDll.SKF_DisconnectDev_N(lDevHandle));
    }


    public long SKF_VerifyPIN(long lAppHandle, long lUserType, String sPin) throws SarException{
        SKF_CheckHandler(lAppHandle);
        byte[] pinBytes = sPin.getBytes();
        byte[] temppinBytes = new byte[pinBytes.length];
        System.arraycopy(pinBytes, 0, temppinBytes, 0, pinBytes.length);

        long[] lPinRetryCount = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_VerifyPIN_N(lAppHandle, lUserType, temppinBytes, lPinRetryCount));
        return lPinRetryCount[0];
    }


    public List<String> SKF_EnumContainer(long lAppHandle) throws SarException, JCSKFException{
        SKF_CheckHandler(lAppHandle);

        byte[] ContainerNameByte = {0};
        long[] lSize = new long[1];

//        SKF_CatchExcetpt(gmtDll.SKF_EnumContainer_N(lAppHandle, null, lSize));
        if(lSize[0] < 2) {//exist default 0x00
            //SKF_CatchJCExcetpt(JCSKFException.JC_SKF_NOCONTAINER);
            return null;
        }

        ContainerNameByte = new byte[(int)lSize[0]];
//        SKF_CatchExcetpt(gmtDll.SKF_EnumContainer_N(lAppHandle, ContainerNameByte, lSize));
        String[] appNames=new String(ContainerNameByte,0,(int)lSize[0]).split("\0");
        return new ArrayList<String>(Arrays.asList(appNames));
    }


    public long SKF_CreateContainer(long lAppHandle, String sContainerName) throws SarException{
        SKF_CheckHandler(lAppHandle);

        byte[] ContainerNameBytes = sContainerName.getBytes();
        //byte[] tempContainerNameBytes = new byte[ContainerNameBytes.length + 1];
        //System.arraycopy(ContainerNameBytes, 0, tempContainerNameBytes, 0, ContainerNameBytes.length);

        long[] lContainerHandle = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_CreateContainer_N(lAppHandle, ContainerNameBytes, lContainerHandle));
        return lContainerHandle[0];
    }


    public long SKF_OpenContainer(long lAppHandle, String sContainerName) throws SarException {
        SKF_CheckHandler(lAppHandle);

        byte[] ContainerNameBytes = sContainerName.getBytes();
        byte[] tempContainerNameBytes = new byte[ContainerNameBytes.length + 1];
        System.arraycopy(ContainerNameBytes, 0, tempContainerNameBytes, 0, ContainerNameBytes.length);

        long[] lContainerHandle = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_OpenContainer_N(lAppHandle, tempContainerNameBytes, lContainerHandle));
        return lContainerHandle[0];
    }


    public void SKF_CloseContainer(long lContainerHandle) throws SarException {
//        SKF_CatchExcetpt(gmtDll.SKF_CloseContainer_N(lContainerHandle));
    }


    public void SKF_CloseHandle(long lHandle) throws SarException {
//        SKF_CatchExcetpt(gmtDll.SKF_CloseHandle_N(lHandle));
    }


    public SKFCspKey.ECCPublicKeyBlob SKF_GenECCKeyPair(long lContainerHandle) throws SarException {
        SKF_CheckHandler(lContainerHandle);
        byte[] xCoordinate = new byte[(int)(ECC_MAX_XCOORDINATE_BITS_LEN)/8];
        byte[] yCoordinate = new byte[(int)(ECC_MAX_YCOORDINATE_BITS_LEN)/8];
//        SKF_CatchExcetpt(gmtDll.SKF_GenECCKeyPair_N(lContainerHandle, SGD_SM2_1, xCoordinate, yCoordinate));
        int xlen = DataUtil.getVirtualValueLength(xCoordinate);
        int ylen = DataUtil.getVirtualValueLength(yCoordinate);
        byte x[] = new byte[xlen];
        byte y[] = new byte[ylen];
        System.arraycopy(xCoordinate, 0, x, 0, xlen);
        System.arraycopy(yCoordinate, 0, y, 0, ylen);
        SKFCspKey.ECCPublicKeyBlob eccPubBlob = new SKFCspKey.ECCPublicKeyBlob(x, y, 256);
        return eccPubBlob;
    }


    public long SKF_GetContainerType(long lContainerHandle) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lType = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_GetContainerType_N(lContainerHandle, lType));
        return lType[0];
    }


    public Object SKF_ExportPublicKey(long lContainerHandle, boolean bSignFlag, boolean bECC) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lBlobLen = new long[1];
        long[] lAlgID = new long[1];
        long[] lBitLen = new long[1];
        int[] iType = new int[1];
        byte[] modulus = new byte[(int)MAX_RSA_MODULUS_LEN];
        byte[] publicExponent = new byte[(int)MAX_RSA_EXPONENT_LEN];
        byte[] xCoordinate = new byte[(int)(ECC_MAX_XCOORDINATE_BITS_LEN)/8];
        byte[] yCoordinate = new byte[(int)(ECC_MAX_YCOORDINATE_BITS_LEN)/8];
        if(bECC) {
//            SKF_CatchExcetpt(gmtDll.SKF_ExportPublicKey_N(lContainerHandle, bSignFlag, iType, lAlgID, lBitLen,
//                    modulus, publicExponent, xCoordinate, yCoordinate, lBlobLen));
            int xlen = DataUtil.getVirtualValueLength(xCoordinate);
            int ylen = DataUtil.getVirtualValueLength(yCoordinate);
            byte x[] = new byte[xlen];
            byte y[] = new byte[ylen];
            System.arraycopy(xCoordinate, 0, x, 0, xlen);
            System.arraycopy(yCoordinate, 0, y, 0, ylen);
            SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob = new SKFCspKey.ECCPublicKeyBlob(x, y, 256);
            return eccPublicKeyBlob;
        }else {
//            SKF_CatchExcetpt(gmtDll.SKF_ExportPublicKey_N(lContainerHandle, bSignFlag, iType, lAlgID, lBitLen,
//                    modulus, publicExponent, xCoordinate, yCoordinate, lBlobLen));
            if(iType[0] == 1)
            {
                byte[] pubmodulus = new byte[(int)lBitLen[0]/8];
                System.arraycopy(modulus, 0, pubmodulus, 0, (int)lBitLen[0]/8);
                SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob =
                        new  SKFCspKey.RSAPublicKeyBlob(lAlgID[0], lBitLen[0], pubmodulus, publicExponent);
                return rsaPublicKeyBlob;
            }else{
                return null;
            }
        }
    }

    public byte[] SKF_GenRandom(long lDevHandle, int length) throws SarException {
        SKF_CheckHandler(lDevHandle);
        byte[] randomData = new byte[length];
//        SKF_CatchExcetpt(gmtDll.SKF_GenRandom_N(lDevHandle, randomData, length));
        return randomData;
    }

    public long SKF_SetSymmKey(long lDevHandle, byte[] byteKey, long lAlgID) throws SarException{
        SKF_CheckHandler(lDevHandle);
        long[] lSessionHandle = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_SetSymmKey_N(lDevHandle, byteKey, lAlgID, lSessionHandle));
        return lSessionHandle[0];
    }

    public void SKF_EncryptInit(long lKeyHandle, BlockCipherParam blockCipherParam) throws SarException {
        SKF_CheckHandler(lKeyHandle);
//        SKF_CatchExcetpt(gmtDll.SKF_EncryptInit_N(lKeyHandle, blockCipherParam.getIV(),
//                blockCipherParam.getIVLen(), blockCipherParam.getPaddingType(), blockCipherParam.getFeedBitLen()));
    }


    public byte[] SKF_Encrypt(long lKeyHandle, byte[] data, long dataLen) throws SarException {
        SKF_CheckHandler(lKeyHandle);
        long[] lEncrytedDataLen = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_Encrypt_N(lKeyHandle, data, dataLen, null, lEncrytedDataLen));
        byte[] encrytedData = new byte[(int)lEncrytedDataLen[0]];
//        SKF_CatchExcetpt(gmtDll.SKF_Encrypt_N(lKeyHandle, data, dataLen, encrytedData, lEncrytedDataLen));
        return encrytedData;
    }

    public void SKF_DecryptInit(long lKeyHandle, BlockCipherParam blockCipherParam) throws SarException {
        SKF_CheckHandler(lKeyHandle);
//        SKF_CatchExcetpt(gmtDll.SKF_DecryptInit_N(lKeyHandle, blockCipherParam.getIV(),
//                blockCipherParam.getIVLen(), blockCipherParam.getPaddingType(), blockCipherParam.getFeedBitLen()));
    }

    public byte[] SKF_Decrypt(long lKeyHandle, byte[] cipherData, long cipherDataLen) throws SarException {
        SKF_CheckHandler(lKeyHandle);
        long[] lDecrytedDataLen = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_Decrypt_N(lKeyHandle, cipherData, cipherDataLen, null, lDecrytedDataLen));
        byte[] decrytedData = new byte[(int)lDecrytedDataLen[0]];
//        SKF_CatchExcetpt(gmtDll.SKF_Decrypt_N(lKeyHandle, cipherData, cipherDataLen, decrytedData, lDecrytedDataLen));
        return decrytedData;
    }


    public ECCCipherBlob SKF_ExtECCEncrypt(long lDevHandle, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob,
                                           byte[] plainText, long plainTextLen) throws SarException {

        byte[] hash = new byte[32];
        long[] cipherLen = new long[1];
        byte[] cipher = new byte[(int)plainTextLen];
        byte[] cipherXCoordinate = new byte[32];
        byte[] cipherYCoordinate = new byte[32];
//        SKF_CatchExcetpt(gmtDll.SKF_ExtECCEncrypt_N(lDevHandle, eccPublicKeyBlob.getBit(), eccPublicKeyBlob.getxCoordinate(),
//                eccPublicKeyBlob.getyCoordinate(),plainText, plainTextLen, cipherXCoordinate, cipherYCoordinate,
//                hash, cipherLen, cipher));
        ECCCipherBlob eccCipherBlob = new ECCCipherBlob(cipherXCoordinate, cipherYCoordinate, hash, cipherLen[0], cipher);
        return eccCipherBlob;
    }


    public void SKF_ImportECCKeyPair(long lContainer, ECCEnvelopedKeyBlob eccEnvelopedKeyBlob) throws SarException{
        SKF_CheckHandler(lContainer);
//        SKF_CatchExcetpt(gmtDll.SKF_ImportECCKeyPair_N(lContainer, eccEnvelopedKeyBlob.getVersion(), eccEnvelopedKeyBlob.getSymmAlgID(),
//                eccEnvelopedKeyBlob.getBits(), eccEnvelopedKeyBlob.getEncryptedPriKey(), eccEnvelopedKeyBlob.getEccPublicKeyBlob().getBit(),
//                eccEnvelopedKeyBlob.getEccPublicKeyBlob().getxCoordinate(), eccEnvelopedKeyBlob.getEccPublicKeyBlob().getyCoordinate(),
//                eccEnvelopedKeyBlob.getEccCipherBlob().getXCoordinate(), eccEnvelopedKeyBlob.getEccCipherBlob().getYCoordinate(),
//                eccEnvelopedKeyBlob.getEccCipherBlob().getHash(), eccEnvelopedKeyBlob.getEccCipherBlob().getCipherLen(),
//                eccEnvelopedKeyBlob.getEccCipherBlob().getCipher()));
        return;
    }


    public long SKF_DigestInit(long lDevHandle, long algId, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob, String sPucID) throws SarException{
        SKF_CheckHandler(lDevHandle);

        long[] lHashHandle = new long[1];
        if(eccPublicKeyBlob == null)
        {
//            SKF_CatchExcetpt(gmtDll.SKF_DigestInit_N(lDevHandle, algId, 0L, null,
//                    null, null, 0L, lHashHandle));
        }
        else {
            char[] charArray = sPucID.toCharArray();
//            SKF_CatchExcetpt(gmtDll.SKF_DigestInit_N(lDevHandle, algId, eccPublicKeyBlob.getBit(), eccPublicKeyBlob.getxCoordinate(),
//                    eccPublicKeyBlob.getyCoordinate(), charArray, charArray.length, lHashHandle));
        }
        return lHashHandle[0];
    }

    public byte[] SKF_Digest(long lHashHandle, byte[] msg, long dataLen) throws SarException {
        SKF_CheckHandler(lHashHandle);
        long[] lHashLen = new long[1];
        lHashLen[0] = 0L;
//        SKF_CatchExcetpt(gmtDll.SKF_Digest_N(lHashHandle, msg, dataLen, null, lHashLen));
        byte[] byteHash = new byte[(int)lHashLen[0]];
//        SKF_CatchExcetpt(gmtDll.SKF_Digest_N(lHashHandle, msg, dataLen, byteHash, lHashLen));
        return byteHash;

    }

    public byte[] SKF_ECCSignData(long lContainer, byte[] digest, long digestLen) throws SarException {
        byte[] r = new byte[(int)ECC_MAX_XCOORDINATE_BITS_LEN/8];
        byte[] s = new byte[(int)ECC_MAX_YCOORDINATE_BITS_LEN/8];
//        SKF_CatchExcetpt(gmtDll.SKF_ECCSignData_N(lContainer, digest, digestLen, r, s));
        int rlen = DataUtil.getVirtualValueLength(r);
        int slen = DataUtil.getVirtualValueLength(s);
        byte br[] = new byte[rlen];
        byte bs[] = new byte[slen];
        System.arraycopy(r, 0, br, 0, rlen);
        System.arraycopy(s, 0, bs, 0, slen);

        ECCSignatureBlob signBlob = new ECCSignatureBlob();
        signBlob.setR(br);
        signBlob.setS(bs);
        return ECCDer.encode(signBlob.getR(), signBlob.getS());
    }

    public boolean SKF_ECCVerify(long lDevHandle, SKFCspKey.ECCPublicKeyBlob eccPublicKeyBlob, byte[] digest,
                                 long digestLen, byte[] signature) throws SarException{
        SKF_CheckHandler(lDevHandle);
        byte[] r = ECCDer.decode(signature, ECCDer.R_X);
        byte[] s = ECCDer.decode(signature, ECCDer.S_Y);
        ECCSignatureBlob signBlob = new ECCSignatureBlob(r, s);
//        SKF_CatchExcetpt(gmtDll.SKF_ECCVerify_N(lDevHandle, eccPublicKeyBlob.getBit(),eccPublicKeyBlob.getxCoordinate(),
//                eccPublicKeyBlob.getyCoordinate(),digest, digestLen, r, s));
        return true;
    }


    public byte[] SKF_ExtECCDecrypt(long lDevHandle, SKFCspKey.ECCPrivateKeyBlob eccPrivateKeyBlob,
                                    ECCCipherBlob eccCipherBlob) throws SarException{
/*        SKF_CheckHandler(lDevHandle);
        ECCCipherBlob[] cipherBlob = new ECCCipherBlob[1];
        cipherBlob[0] = new ECCCipherBlob(eccCipherBlob.getXCoordinate(),eccCipherBlob.getYCoordinate(),
                eccCipherBlob.getHash(), eccCipherBlob.getCipherLen(), eccCipherBlob.getCipher());

        SKFCspKey.ECCPrivateKeyBlob[] privateKeyBlob = new SKFCspKey.ECCPrivateKeyBlob[1];
        privateKeyBlob[0] = new SKFCspKey.ECCPrivateKeyBlob(eccPrivateKeyBlob.getBit(), eccPrivateKeyBlob.getPrivateKey());
        long[] lPlaintextLen = new long[1];
        SKF_CatchExcetpt(SKF_ExtECCDecrypt_N(lDevHandle, privateKeyBlob, cipherBlob, null, lPlaintextLen));

        byte[] plainText = new byte[(int)lPlaintextLen[0]];
        SKF_CatchExcetpt(SKF_ExtECCDecrypt_N(lDevHandle, privateKeyBlob, cipherBlob, plainText, lPlaintextLen));
        return plainText;
*/
        return  null;
    }


    public SKFCspKey.RSAPublicKeyBlob SKF_GenRSAKeyPair(long lContainerHandle, long lBits) throws SarException{
        SKF_CheckHandler(lContainerHandle);

        long[] lAlgID = new long[1];
        long[] lBitLen = new long[1];
        byte[] modulus = new byte[(int)lBits/8];
        byte[] publicExponent = new byte[(int)MAX_RSA_EXPONENT_LEN];

//        SKF_CatchExcetpt(gmtDll.SKF_GenRSAKeyPair_N(lContainerHandle, lBits, lAlgID, lBitLen, modulus, publicExponent));

        SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob =
                new  SKFCspKey.RSAPublicKeyBlob(lAlgID[0], lBitLen[0], modulus, publicExponent);
        return rsaPublicKeyBlob;

    }

    public long SKF_RSAExportSessionKey(long lContainerHandle, long lAlgID, SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob,
                                        byte[] data, long[] lDataLen) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lSessionHandle = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_RSAExportSessionKey_N(lContainerHandle, lAlgID, rsaPublicKeyBlob.getAlgID(),
//                rsaPublicKeyBlob.getBitLen(), rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent(),
//                data, lDataLen, lSessionHandle));
        return lSessionHandle[0];
    }

    public byte[] SKF_ExtRSAPubKeyOperation(long lDevHandle, SKFCspKey.RSAPublicKeyBlob rsaPublicKeyBlob, byte[] input,
                                            long lInputLen) throws SarException{
        SKF_CheckHandler(lDevHandle);
        long[] datalen = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_ExtRSAPubKeyOperation_N(lDevHandle, rsaPublicKeyBlob.getAlgID(),
//                rsaPublicKeyBlob.getBitLen(), rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent(),
//                input, lInputLen, null, datalen));
        byte[] dataout = new byte[(int)datalen[0]];
//        SKF_CatchExcetpt(gmtDll.SKF_ExtRSAPubKeyOperation_N(lDevHandle, rsaPublicKeyBlob.getAlgID(),
//                rsaPublicKeyBlob.getBitLen(), rsaPublicKeyBlob.getModulus(), rsaPublicKeyBlob.getPublicExponent(),
//                input, lInputLen, dataout, datalen));
        return dataout;
    }
    public void SKF_ImportRSAKeyPair(long lContainerHandle, long lAlgID, byte[] wrappedKey, long wrappedKeyLen,
                                     byte[] encryptedData, long encryptedDataLen) throws SarException{
        SKF_CheckHandler(lContainerHandle);
//        SKF_CatchExcetpt(gmtDll.SKF_ImportRSAKeyPair_N(lContainerHandle, lAlgID,wrappedKey, wrappedKeyLen,
//                encryptedData, encryptedDataLen));
    }

    public byte[] SKF_RSASignData(long lContainerHandle, byte[] data, long dataLen) throws SarException{
        SKF_CheckHandler(lContainerHandle);
        long[] lSignatureLen = new long[1];
//        SKF_CatchExcetpt(gmtDll.SKF_RSASignData_N(lContainerHandle, data, dataLen,null,lSignatureLen));
        byte[] signature = new byte[(int)lSignatureLen[0]];
//        SKF_CatchExcetpt(gmtDll.SKF_RSASignData_N(lContainerHandle, data, dataLen,signature,lSignatureLen));
        return signature;
    }

    public boolean SKF_RSAVerify(long lDevHandle, SKFCspKey.RSAPublicKeyBlob rsaPublicKey, byte[] data,
                                 byte[] signature) throws SarException{
        SKF_CheckHandler(lDevHandle);
//        SKF_CatchExcetpt(gmtDll.SKF_RSAVerify_N(lDevHandle, rsaPublicKey.getAlgID(), rsaPublicKey.getBitLen(),
//                rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent(), data, data.length, signature, signature.length));
        return true;
    }

    private void SKF_CheckHandler(long lHandle) throws SarException {
        if (lHandle <= 0) {
            throw new SarException(SarException.SAR_INVALIDHANDLEERR);
        }
    }

    private void SKF_CatchExcetpt(long resultValue) throws SarException{
        if(resultValue != SarException.SAR_OK) {
            throw new SarException((int)resultValue);
        }
    }

    private void SKF_CatchJCExcetpt(long resultValue) throws JCSKFException{
        if(resultValue != JCSKFException.JC_SKF_OK) {
            throw new JCSKFException((int)resultValue);
        }
    }
}
