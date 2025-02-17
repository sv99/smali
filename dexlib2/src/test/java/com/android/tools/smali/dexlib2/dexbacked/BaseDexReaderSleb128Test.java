/*
 * Copyright 2012, Google LLC
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google LLC nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.tools.smali.dexlib2.dexbacked;

import com.android.tools.smali.dexlib2.dexbacked.DexBuffer;
import com.android.tools.smali.dexlib2.dexbacked.DexReader;
import junit.framework.Assert;
import com.android.tools.smali.util.ExceptionWithContext;
import org.junit.Test;

public class BaseDexReaderSleb128Test {
    @Test
    public void testSleb128() {
        performTest(0x0, new byte[]{0x0, 0x11}, 1);
        performTest(0x1, new byte[]{0x1, 0x11}, 1);
        performTest(0x3f, new byte[]{0x3f, 0x11}, 1);
        performTest(0xffffffc0, new byte[]{0x40, 0x11}, 1);
        performTest(0xfffffff0, new byte[]{0x70, 0x11}, 1);
        performTest(0xffffffff, new byte[]{0x7f, 0x11}, 1);

        performTest(0x80, new byte[]{(byte)0x80, 0x1, 0x11}, 2);
        performTest(0x100, new byte[]{(byte)0x80, 0x2, 0x11}, 2);
        performTest(0x800, new byte[]{(byte)0x80, 0x10, 0x11}, 2);
        performTest(0x1f80, new byte[]{(byte)0x80, 0x3f, 0x11}, 2);
        performTest(0xffffe000, new byte[]{(byte)0x80, 0x40, 0x11}, 2);
        performTest(0xffffe080, new byte[]{(byte)0x80, 0x41, 0x11}, 2);
        performTest(0xfffff800, new byte[]{(byte)0x80, 0x70, 0x11}, 2);
        performTest(0xffffff80, new byte[]{(byte)0x80, 0x7f, 0x11}, 2);

        performTest(0xff, new byte[]{(byte)0xff, 0x1, 0x11}, 2);
        performTest(0x17f, new byte[]{(byte)0xff, 0x2, 0x11}, 2);
        performTest(0x87f, new byte[]{(byte)0xff, 0x10, 0x11}, 2);
        performTest(0x1fff, new byte[]{(byte)0xff, 0x3f, 0x11}, 2);
        performTest(0xffffe07f, new byte[]{(byte)0xff, 0x40, 0x11}, 2);
        performTest(0xffffe0ff, new byte[]{(byte)0xff, 0x41, 0x11}, 2);
        performTest(0xfffff87f, new byte[]{(byte)0xff, 0x70, 0x11}, 2);
        performTest(0xffffffff, new byte[]{(byte)0xff, 0x7f, 0x11}, 2);

        performTest(0x4000, new byte[]{(byte)0x80, (byte)0x80, 0x1, 0x11}, 3);
        performTest(0x8000, new byte[]{(byte)0x80, (byte)0x80, 0x2, 0x11}, 3);
        performTest(0x40000, new byte[]{(byte)0x80, (byte)0x80, 0x10, 0x11}, 3);
        performTest(0xfc000, new byte[]{(byte)0x80, (byte)0x80, 0x3f, 0x11}, 3);
        performTest(0xfff00000, new byte[]{(byte)0x80, (byte)0x80, 0x40, 0x11}, 3);
        performTest(0xfff04000, new byte[]{(byte)0x80, (byte)0x80, 0x41, 0x11}, 3);
        performTest(0xfffc0000, new byte[]{(byte)0x80, (byte)0x80, 0x70, 0x11}, 3);
        performTest(0xffffc000, new byte[]{(byte)0x80, (byte)0x80, 0x7f, 0x11}, 3);

        performTest(0x7fff, new byte[]{(byte)0xff, (byte)0xff, 0x1, 0x11}, 3);
        performTest(0xbfff, new byte[]{(byte)0xff, (byte)0xff, 0x2, 0x11}, 3);
        performTest(0x43fff, new byte[]{(byte)0xff, (byte)0xff, 0x10, 0x11}, 3);
        performTest(0xfffff, new byte[]{(byte)0xff, (byte)0xff, 0x3f, 0x11}, 3);
        performTest(0xfff03fff, new byte[]{(byte)0xff, (byte)0xff, 0x40, 0x11}, 3);
        performTest(0xfff07fff, new byte[]{(byte)0xff, (byte)0xff, 0x41, 0x11}, 3);
        performTest(0xfffc3fff, new byte[]{(byte)0xff, (byte)0xff, 0x70, 0x11}, 3);
        performTest(0xffffffff, new byte[]{(byte)0xff, (byte)0xff, 0x7f, 0x11}, 3);

        performTest(0x200000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x1, 0x11}, 4);
        performTest(0x400000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x2, 0x11}, 4);
        performTest(0x2000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x10, 0x11}, 4);
        performTest(0x7e00000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x3f, 0x11}, 4);
        performTest(0xf8000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x40, 0x11}, 4);
        performTest(0xf8200000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x41, 0x11}, 4);
        performTest(0xfe000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x70, 0x11}, 4);
        performTest(0xffe00000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 0x7f, 0x11}, 4);

        performTest(0x3fffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x1, 0x11}, 4);
        performTest(0x5fffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x2, 0x11}, 4);
        performTest(0x21fffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x10, 0x11}, 4);
        performTest(0x7ffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x3f, 0x11}, 4);
        performTest(0xf81fffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x40, 0x11}, 4);
        performTest(0xf83fffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x41, 0x11}, 4);
        performTest(0xfe1fffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x70, 0x11}, 4);
        performTest(0xffffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, 0x7f, 0x11}, 4);

        performTest(0x10000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0x1, 0x11}, 5);
        performTest(0x20000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0x2, 0x11}, 5);
        performTest(0x70000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0x7, 0x11}, 5);
        performTest(0x70000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0x17, 0x11}, 5);
        performTest(0x70000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0x47, 0x11}, 5);
        performTest(0x70000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0x77, 0x11}, 5);
        performTest(0x80000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0x8, 0x11}, 5);
        performTest(0xe0000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0xe, 0x11}, 5);
        performTest(0xf0000000, new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, 0xf, 0x11}, 5);

        performTest(0x1fffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x1, 0x11}, 5);
        performTest(0x2fffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x2, 0x11}, 5);
        performTest(0x7fffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x7, 0x11}, 5);
        performTest(0x7fffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x17, 0x11}, 5);
        performTest(0x7fffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x47, 0x11}, 5);
        performTest(0x7fffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x77, 0x11}, 5);
        performTest(0x8fffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x8, 0x11}, 5);
        performTest(0xefffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0xe, 0x11}, 5);
        performTest(0xffffffff, new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0xf, 0x11}, 5);

        performTest(0x8197d2, new byte[]{(byte)0xd2, (byte)0xaf, (byte)0x86, 0x4});
        performTest(0x3cc8eb78, new byte[]{(byte)0xf8, (byte)0xd6, (byte)0xa3, (byte)0xe6, 0x3});
        performTest(0x51307f32, new byte[]{(byte)0xb2, (byte)0xfe, (byte)0xc1, (byte)0x89, 0x5});
        performTest(0x8893, new byte[]{(byte)0x93, (byte)0x91, 0x2});
        performTest(0x80fb, new byte[]{(byte)0xfb, (byte)0x81, 0x2});
        performTest(0x3d, new byte[]{0x3d});
        performTest(0x987c, new byte[]{(byte)0xfc, (byte)0xb0, 0x2});
        performTest(0x5b2478, new byte[]{(byte)0xf8, (byte)0xc8, (byte)0xec, 0x2});
        performTest(0x65350ed9, new byte[]{(byte)0xd9, (byte)0x9d, (byte)0xd4, (byte)0xa9, 0x6});
        performTest(0x3e, new byte[]{0x3e});
        performTest(0x7b1e, new byte[]{(byte)0x9e, (byte)0xf6, 0x1});
        performTest(0xb5, new byte[]{(byte)0xb5, 0x1});
        performTest(0x96, new byte[]{(byte)0x96, 0x1});
        performTest(0xa1, new byte[]{(byte)0xa1, 0x1});
        performTest(0x4d50a85d, new byte[]{(byte)0xdd, (byte)0xd0, (byte)0xc2, (byte)0xea, 0x4});
        performTest(0xc419, new byte[]{(byte)0x99, (byte)0x88, 0x3});
        performTest(0xcf34, new byte[]{(byte)0xb4, (byte)0x9e, 0x3});
        performTest(0x527d, new byte[]{(byte)0xfd, (byte)0xa4, 0x1});
        performTest(0x5a2894, new byte[]{(byte)0x94, (byte)0xd1, (byte)0xe8, 0x2});
        performTest(0xa6, new byte[]{(byte)0xa6, 0x1});
        performTest(0x3e05, new byte[]{(byte)0x85, (byte)0xfc, 0x0});
        performTest(0x5f, new byte[]{(byte)0xdf, 0x0});
        performTest(0xe2d9af, new byte[]{(byte)0xaf, (byte)0xb3, (byte)0x8b, 0x7});
        performTest(0xa853fe14, new byte[]{(byte)0x94, (byte)0xfc, (byte)0xcf, (byte)0xc2, 0xa});
        performTest(0xa853fe14, new byte[]{(byte)0x94, (byte)0xfc, (byte)0xcf, (byte)0xc2, 0x7a});
        performTest(0x117de731, new byte[]{(byte)0xb1, (byte)0xce, (byte)0xf7, (byte)0x8b, 0x1});
        performTest(0xb7c9, new byte[]{(byte)0xc9, (byte)0xef, 0x2});
        performTest(0xb1, new byte[]{(byte)0xb1, 0x1});
        performTest(0x4f194d, new byte[]{(byte)0xcd, (byte)0xb2, (byte)0xbc, 0x2});
        performTest(0x8d5733, new byte[]{(byte)0xb3, (byte)0xae, (byte)0xb5, 0x4});
        performTest(0x2824e9ae, new byte[]{(byte)0xae, (byte)0xd3, (byte)0x93, (byte)0xc1, 0x2});
        performTest(0x792e, new byte[]{(byte)0xae, (byte)0xf2, 0x1});
        performTest(0xadef, new byte[]{(byte)0xef, (byte)0xdb, 0x2});
        performTest(0x5c, new byte[]{(byte)0xdc, 0x0});
        performTest(0x14f9ccf8, new byte[]{(byte)0xf8, (byte)0x99, (byte)0xe7, (byte)0xa7, 0x1});
        performTest(0xd1, new byte[]{(byte)0xd1, 0x1});
        performTest(0xba787ecd, new byte[]{(byte)0xcd, (byte)0xfd, (byte)0xe1, (byte)0xd3, 0x7b});
        performTest(0x4f, new byte[]{(byte)0xcf, 0x0});
        performTest(0xfb03, new byte[]{(byte)0x83, (byte)0xf6, 0x3});
        performTest(0xee3f7cd8, new byte[]{(byte)0xd8, (byte)0xf9, (byte)0xfd, (byte)0xf1, 0x7e});
        performTest(0x9a6e, new byte[]{(byte)0xee, (byte)0xb4, 0x2});
        performTest(0x8f0983, new byte[]{(byte)0x83, (byte)0x93, (byte)0xbc, 0x4});
        performTest(0x3a00e01f, new byte[]{(byte)0x9f, (byte)0xc0, (byte)0x83, (byte)0xd0, 0x3});
        performTest(0x7f532d93, new byte[]{(byte)0x93, (byte)0xdb, (byte)0xcc, (byte)0xfa, 0x7});
        performTest(0x179d8d, new byte[]{(byte)0x8d, (byte)0xbb, (byte)0xde, 0x0});
        performTest(0xfc5, new byte[]{(byte)0xc5, 0x1f});
        performTest(0x11, new byte[]{0x11});
        performTest(0xc9b53e8, new byte[]{(byte)0xe8, (byte)0xa7, (byte)0xed, (byte)0xe4, 0x0});
        performTest(0x97, new byte[]{(byte)0x97, 0x1});
        performTest(0x52b3, new byte[]{(byte)0xb3, (byte)0xa5, 0x1});
        performTest(0x92, new byte[]{(byte)0x92, 0x1});
        performTest(0xd2, new byte[]{(byte)0xd2, 0x1});
        performTest(0x13d330, new byte[]{(byte)0xb0, (byte)0xa6, (byte)0xcf, 0x0});
        performTest(0x672f41, new byte[]{(byte)0xc1, (byte)0xde, (byte)0x9c, 0x3});
        performTest(0xcf, new byte[]{(byte)0xcf, 0x1});
        performTest(0x54ddb6dd, new byte[]{(byte)0xdd, (byte)0xed, (byte)0xf6, (byte)0xa6, 0x5});
        performTest(0x7ebcae, new byte[]{(byte)0xae, (byte)0xf9, (byte)0xfa, 0x3});
        performTest(0x38, new byte[]{0x38});
        performTest(0x8118f4e7, new byte[]{(byte)0xe7, (byte)0xe9, (byte)0xe3, (byte)0x88, 0x78});
        performTest(0xac, new byte[]{(byte)0xac, 0x1});
        performTest(0xab309c, new byte[]{(byte)0x9c, (byte)0xe1, (byte)0xac, 0x5});
        performTest(0x1bf9b2, new byte[]{(byte)0xb2, (byte)0xf3, (byte)0xef, 0x0});
        performTest(0x8b3c70, new byte[]{(byte)0xf0, (byte)0xf8, (byte)0xac, 0x4});
        performTest(0x7774, new byte[]{(byte)0xf4, (byte)0xee, 0x1});
        performTest(0x33e839, new byte[]{(byte)0xb9, (byte)0xd0, (byte)0xcf, 0x1});
        performTest(0x84d655a0, new byte[]{(byte)0xa0, (byte)0xab, (byte)0xd9, (byte)0xa6, 0x78});
        performTest(0xf3543ef3, new byte[]{(byte)0xf3, (byte)0xfd, (byte)0xd0, (byte)0x9a, 0x7f});
        performTest(0x1d777e, new byte[]{(byte)0xfe, (byte)0xee, (byte)0xf5, 0x0});
        performTest(0xf7, new byte[]{(byte)0xf7, 0x1});
        performTest(0x2444, new byte[]{(byte)0xc4, (byte)0xc8, 0x0});
        performTest(0x536b, new byte[]{(byte)0xeb, (byte)0xa6, 0x1});
        performTest(0xa8, new byte[]{(byte)0xa8, 0x1});
        performTest(0xdbfc, new byte[]{(byte)0xfc, (byte)0xb7, 0x3});
        performTest(0xe66db7, new byte[]{(byte)0xb7, (byte)0xdb, (byte)0x99, 0x7});
        performTest(0xb7ca, new byte[]{(byte)0xca, (byte)0xef, 0x2});
        performTest(0xe807d0e5, new byte[]{(byte)0xe5, (byte)0xa1, (byte)0x9f, (byte)0xc0, 0x7e});
        performTest(0x6a4, new byte[]{(byte)0xa4, 0xd});
        performTest(0x64, new byte[]{(byte)0xe4, 0x0});
        performTest(0xf3fb75, new byte[]{(byte)0xf5, (byte)0xf6, (byte)0xcf, 0x7});
        performTest(0xb72cb6b9, new byte[]{(byte)0xb9, (byte)0xed, (byte)0xb2, (byte)0xb9, 0x7b});
        performTest(0xfd, new byte[]{(byte)0xfd, 0x1});
        performTest(0xb48b, new byte[]{(byte)0x8b, (byte)0xe9, 0x2});
        performTest(0x39c3, new byte[]{(byte)0xc3, (byte)0xf3, 0x0});
        performTest(0x12b8afbd, new byte[]{(byte)0xbd, (byte)0xdf, (byte)0xe2, (byte)0x95, 0x1});
        performTest(0x56f149, new byte[]{(byte)0xc9, (byte)0xe2, (byte)0xdb, 0x2});
        performTest(0xbf, new byte[]{(byte)0xbf, 0x1});
        performTest(0x3ac72481, new byte[]{(byte)0x81, (byte)0xc9, (byte)0x9c, (byte)0xd6, 0x3});
        performTest(0xb69ca721, new byte[]{(byte)0xa1, (byte)0xce, (byte)0xf2, (byte)0xb4, 0x7b});
        performTest(0x2380, new byte[]{(byte)0x80, (byte)0xc7, 0x0});
        performTest(0x656268, new byte[]{(byte)0xe8, (byte)0xc4, (byte)0x95, 0x3});
        performTest(0x71, new byte[]{(byte)0xf1, 0x0});
        performTest(0xf06425, new byte[]{(byte)0xa5, (byte)0xc8, (byte)0xc1, 0x7});
        performTest(0xb587cb, new byte[]{(byte)0xcb, (byte)0x8f, (byte)0xd6, 0x5});
        performTest(0x8742, new byte[]{(byte)0xc2, (byte)0x8e, 0x2});
        performTest(0xc6, new byte[]{(byte)0xc6, 0x1});
        performTest(0xee62789f, new byte[]{(byte)0x9f, (byte)0xf1, (byte)0x89, (byte)0xf3, 0x7e});
        performTest(0x470a, new byte[]{(byte)0x8a, (byte)0x8e, 0x1});
        performTest(0x11ef5cdc, new byte[]{(byte)0xdc, (byte)0xb9, (byte)0xbd, (byte)0x8f, 0x1});
        performTest(0xc44ea9, new byte[]{(byte)0xa9, (byte)0x9d, (byte)0x91, 0x6});
        performTest(0x94477f78, new byte[]{(byte)0xf8, (byte)0xfe, (byte)0x9d, (byte)0xa2, 0x79});
        performTest(0xe47a0b4f, new byte[]{(byte)0xcf, (byte)0x96, (byte)0xe8, (byte)0xa3, 0x7e});
    }

    @Test
    public void testSleb128Failure() {
        // test the case when the MSB of the last byte is set

        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x81, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x82, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x87, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x97, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0xc7, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0xf7, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x88, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x8e, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0x8f, 0x11});
        performFailureTest(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0xff, 0x11});

        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x81, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x82, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x87, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x97, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xc7, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xf7, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x88, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x8e, 0x11});
        performFailureTest(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, 0x11});
    }

    private void performTest(int expectedValue, byte[] buf) {
        performTest(expectedValue, buf, buf.length);
    }

    private void performTest(int expectedValue, byte[] buf, int expectedLength) {
        DexBuffer dexBuf = new DexBuffer(buf);
        DexReader<? extends DexBuffer> reader = dexBuf.readerAt(0);
        Assert.assertEquals(expectedValue, reader.readSleb128());
        Assert.assertEquals(expectedLength, reader.getOffset());

        reader = dexBuf.readerAt(0);
        Assert.assertEquals(expectedLength, reader.peekSleb128Size());
    }

    private void performFailureTest(byte[] buf) {
        DexBuffer dexBuf = new DexBuffer(buf);
        DexReader<? extends DexBuffer> reader = dexBuf.readerAt(0);
        try {
            reader.peekSleb128Size();
            Assert.fail();
        } catch (ExceptionWithContext ex) {
            // expected exception
        }
        try {
            reader.readSleb128();
            Assert.fail();
        } catch (ExceptionWithContext ex) {
            // expected exception
        }
    }
}
