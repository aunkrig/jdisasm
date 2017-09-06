
/*
 * JDISASM - A Java[TM] class file disassembler
 *
 * Copyright (c) 2017, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package subject;

import de.unkrig.jdisasm.Disassembler;

/**
 * This class is never loaded; it is just a test sibject for the {@link Disassembler}.
 */
public
class WideLocals {

    public int
    methodWithOnlyAFewLocals(int p) {
        int l1 = 2 * p;
        return l1;
    }

    @SuppressWarnings("unused") public int
    methodWithManyLocals(int p) {

        int l000, l001, l002, l003, l004, l005, l006, l007, l008, l009;
        int l010, l011, l012, l013, l014, l015, l016, l017, l018, l019;
        int l020, l021, l022, l023, l024, l025, l026, l027, l028, l029;
        int l030, l031, l032, l033, l034, l035, l036, l037, l038, l039;
        int l040, l041, l042, l043, l044, l045, l046, l047, l048, l049;
        int l050, l051, l052, l053, l054, l055, l056, l057, l058, l059;
        int l060, l061, l062, l063, l064, l065, l066, l067, l068, l069;
        int l070, l071, l072, l073, l074, l075, l076, l077, l078, l079;
        int l080, l081, l082, l083, l084, l085, l086, l087, l088, l089;
        int l090, l091, l092, l093, l094, l095, l096, l097, l098, l099;

        int l100, l101, l102, l103, l104, l105, l106, l107, l108, l109;
        int l110, l111, l112, l113, l114, l115, l116, l117, l118, l119;
        int l120, l121, l122, l123, l124, l125, l126, l127, l128, l129;
        int l130, l131, l132, l133, l134, l135, l136, l137, l138, l139;
        int l140, l141, l142, l143, l144, l145, l146, l147, l148, l149;
        int l150, l151, l152, l153, l154, l155, l156, l157, l158, l159;
        int l160, l161, l162, l163, l164, l165, l166, l167, l168, l169;
        int l170, l171, l172, l173, l174, l175, l176, l177, l178, l179;
        int l180, l181, l182, l183, l184, l185, l186, l187, l188, l189;
        int l190, l191, l192, l193, l194, l195, l196, l197, l198, l199;

        int l200, l201, l202, l203, l204, l205, l206, l207, l208, l209;
        int l210, l211, l212, l213, l214, l215, l216, l217, l218, l219;
        int l220, l221, l222, l223, l224, l225, l226, l227, l228, l229;
        int l230, l231, l232, l233, l234, l235, l236, l237, l238, l239;
        int l240, l241, l242, l243, l244, l245, l246, l247, l248, l249;
        int l250, l251, l252, l253, l254, l255, l256, l257, l258, l259;
        int l260, l261, l262, l263, l264, l265, l266, l267, l268, l269;
        int l270, l271, l272, l273, l274, l275, l276, l277, l278, l279;
        int l280, l281, l282, l283, l284, l285, l286, l287, l288, l289;
        int l290, l291, l292, l293, l294, l295, l296, l297, l298, l299;

        l200 = 3;
        l270 = 4;
        return p;
    }
}
