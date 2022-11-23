var TESSELLATION_LIMIT = 8128;

function j(a) {
    throw a;
}
var k = void 0,
    n = !0,
    q = null,
    r = !1;

function u() {
    return function() {}
}
try {
    this.Module = Module, Module.test
} catch (aa) {
    this.Module = Module = {}
}
var ba = "object" === typeof process && "function" === typeof require,
    ca = "object" === typeof window,
    v = "function" === typeof importScripts,
    da = !ca && !ba && !v;
"object" === typeof module && (module.T = Module);
if (ba) {
    Module.print = function(a) {
        process.stdout.write(a + "\n")
    };
    Module.printErr = function(a) {
        process.stderr.write(a + "\n")
    };
    var ea = require("fs"),
        fa = require("path");
    Module.read = function(a, b) {
        var a = fa.normalize(a),
            c = ea.readFileSync(a);
        !c && a != fa.resolve(a) && (a = path.join(__dirname, "..", "src", a), c = ea.readFileSync(a));
        c && !b && (c = c.toString());
        return c
    };
    Module.readBinary = function(a) {
        return Module.read(a, n)
    };
    Module.load = function(a) {
        ga(read(a))
    };
    Module.arguments || (Module.arguments = process.argv.slice(2))
}
da && (Module.print = print, "undefined" != typeof printErr && (Module.printErr = printErr), Module.read = read, Module.readBinary = function(a) {
    return read(a, "binary")
}, Module.arguments || ("undefined" != typeof scriptArgs ? Module.arguments = scriptArgs : "undefined" != typeof arguments && (Module.arguments = arguments)));
ca && !v && (Module.print || (Module.print = function(a) {
    console.log(a)
}), Module.printErr || (Module.printErr = function(a) {
    console.log(a)
}));
if (ca || v) Module.read = function(a) {
    var b = new XMLHttpRequest;
    b.open("GET", a, r);
    b.send(q);
    return b.responseText
}, Module.arguments || "undefined" != typeof arguments && (Module.arguments = arguments);
v && (Module.print || (Module.print = u()), Module.load = importScripts);
!v && (!ca && !ba && !da) && j("Unknown runtime environment. Where are we?");

function ga(a) {
    eval.call(q, a)
}
"undefined" == !Module.load && Module.read && (Module.load = function(a) {
    ga(Module.read(a))
});
Module.print || (Module.print = u());
Module.printErr || (Module.printErr = Module.print);
Module.arguments || (Module.arguments = []);
Module.print = Module.print;
Module.g = Module.printErr;
Module.preRun || (Module.preRun = []);
Module.postRun || (Module.postRun = []);

function ha() {
    return w
}

function ia(a) {
    w = a
}

function ja(a) {
    if (1 == y) return 1;
    var b = {
        "%i1": 1,
        "%i8": 1,
        "%i16": 2,
        "%i32": 4,
        "%i64": 8,
        "%float": 4,
        "%double": 8
    } ["%" + a];
    b || ("*" == a.charAt(a.length - 1) ? b = y : "i" == a[0] && (a = parseInt(a.substr(1)), A(0 == a % 8), b = a / 8));
    return b
}

function ka(a, b, c) {
    c && c.length ? (c.splice || (c = Array.prototype.slice.call(c)), c.splice(0, 0, b), Module["dynCall_" + a].apply(q, c)) : Module["dynCall_" + a].call(q, b)
}
var la;

function ma() {
    var a = [],
        b = 0;
    this.B = function(c) {
        c &= 255;
        b && (a.push(c), b--);
        if (0 == a.length) {
            if (128 > c) return String.fromCharCode(c);
            a.push(c);
            b = 191 < c && 224 > c ? 1 : 2;
            return ""
        }
        if (0 < b) return "";
        var c = a[0],
            d = a[1],
            e = a[2],
            c = 191 < c && 224 > c ? String.fromCharCode((c & 31) << 6 | d & 63) : String.fromCharCode((c & 15) << 12 | (d & 63) << 6 | e & 63);
        a.length = 0;
        return c
    };
    this.O = function(a) {
        for (var a = unescape(encodeURIComponent(a)), b = [], e = 0; e < a.length; e++) b.push(a.charCodeAt(e));
        return b
    }
}

function na(a) {
    var b = w;
    w = w + a | 0;
    w = w + 7 >> 3 << 3;
    return b
}

function oa(a) {
    var b = B;
    B = B + a | 0;
    B = B + 7 >> 3 << 3;
    return b
}

function pa(a) {
    var b = G;
    G = G + a | 0;
    G = G + 7 >> 3 << 3;
    G >= qa && H("Cannot enlarge memory arrays in asm.js. Either (1) compile with -s TOTAL_MEMORY=X with X higher than the current value, or (2) set Module.TOTAL_MEMORY before the program runs.");
    return b
}

function ra(a, b) {
    return Math.ceil(a / (b ? b : 8)) * (b ? b : 8)
}
var y = 4,
    sa = {},
    I = r,
    ta;

function H(a) {
    Module.print(a + ":\n" + Error().stack);
    I = n;
    j("Assertion: " + a)
}

function A(a, b) {
    a || H("Assertion failed: " + b)
}
var ua = this;
Module.ccall = function(a, b, c, d) {
    return va(wa(a), b, c, d)
};

function wa(a) {
    try {
        var b = ua.Module["_" + a];
        b || (b = eval("_" + a))
    } catch (c) {}
    A(b, "Cannot call unknown function " + a + " (perhaps LLVM optimizations or closure removed it?)");
    return b
}

function va(a, b, c, d) {
    function e(a, b) {
        if ("string" == b) {
            if (a === q || a === k || 0 === a) return 0;
            f || (f = ha());
            var c = na(a.length + 1);
            xa(a, c);
            return c
        }
        return "array" == b ? (f || (f = ha()), c = na(a.length), ya(a, c), c) : a
    }
    var f = 0,
        g = 0,
        d = d ? d.map(function(a) {
            return e(a, c[g++])
        }) : [];
    a = a.apply(q, d);
    "string" == b ? b = J(a) : (A("array" != b), b = a);
    f && ia(f);
    return b
}
Module.cwrap = function(a, b, c) {
    var d = wa(a);
    return function() {
        return va(d, b, c, Array.prototype.slice.call(arguments))
    }
};

function za(a, b, c) {
    c = c || "i8";
    "*" === c.charAt(c.length - 1) && (c = "i32");
    switch (c) {
        case "i1":
            K[a] = b;
            break;
        case "i8":
            K[a] = b;
            break;
        case "i16":
            Aa[a >> 1] = b;
            break;
        case "i32":
            M[a >> 2] = b;
            break;
        case "i64":
            ta = [b >>> 0, (Math.min(+Math.floor(b / 4294967296), 4294967295) | 0) >>> 0];
            M[a >> 2] = ta[0];
            M[a + 4 >> 2] = ta[1];
            break;
        case "float":
            Ba[a >> 2] = b;
            break;
        case "double":
            N[a >> 3] = b;
            break;
        default:
            H("invalid type for setValue: " + c)
    }
}
Module.setValue = za;
Module.getValue = function(a, b) {
    b = b || "i8";
    "*" === b.charAt(b.length - 1) && (b = "i32");
    switch (b) {
        case "i1":
            return K[a];
        case "i8":
            return K[a];
        case "i16":
            return Aa[a >> 1];
        case "i32":
            return M[a >> 2];
        case "i64":
            return M[a >> 2];
        case "float":
            return Ba[a >> 2];
        case "double":
            return N[a >> 3];
        default:
            H("invalid type for setValue: " + b)
    }
    return q
};
var O = 2,
    Ca = 4;
Module.ALLOC_NORMAL = 0;
Module.ALLOC_STACK = 1;
Module.ALLOC_STATIC = O;
Module.ALLOC_DYNAMIC = 3;
Module.ALLOC_NONE = Ca;

function P(a, b, c, d) {
    var e, f;
    "number" === typeof a ? (e = n, f = a) : (e = r, f = a.length);
    var g = "string" === typeof b ? b : q,
        c = c == Ca ? d : [Da, na, oa, pa][c === k ? O : c](Math.max(f, g ? 1 : b.length));
    if (e) {
        d = c;
        A(0 == (c & 3));
        for (a = c + (f & -4); d < a; d += 4) M[d >> 2] = 0;
        for (a = c + f; d < a;) K[d++ | 0] = 0;
        return c
    }
    if ("i8" === g) return a.subarray || a.slice ? Q.set(a, c) : Q.set(new Uint8Array(a), c), c;
    for (var d = 0, i, l; d < f;) {
        var z = a[d];
        "function" === typeof z && (z = sa.U(z));
        e = g || b[d];
        0 === e ? d++ : ("i64" == e && (e = "i32"), za(c + d, z, e), l !== e && (i = ja(e), l = e), d += i)
    }
    return c
}
Module.allocate = P;

function J(a, b) {
    for (var c = r, d, e = 0;;) {
        d = Q[a + e | 0];
        if (128 <= d) c = n;
        else if (0 == d && !b) break;
        e++;
        if (b && e == b) break
    }
    b || (b = e);
    var f = "";
    if (!c) {
        for (; 0 < b;) d = String.fromCharCode.apply(String, Q.subarray(a, a + Math.min(b, 1024))), f = f ? f + d : d, a += 1024, b -= 1024;
        return f
    }
    c = new ma;
    for (e = 0; e < b; e++) d = Q[a + e | 0], f += c.B(d);
    return f
}
Module.Pointer_stringify = J;
var K, Q, Aa, Ea, M, Fa, Ba, N, Ga = 0,
    B = 0,
    Ha = 0,
    w = 0,
    Ia = 0,
    Ja = 0,
    G = 0,
    qa = Module.TOTAL_MEMORY || 16777216;
A(!!Int32Array && !!Float64Array && !!(new Int32Array(1)).subarray && !!(new Int32Array(1)).set, "Cannot fallback to non-typed array case: Code is too specialized");
var R = new ArrayBuffer(qa);
K = new Int8Array(R);
Aa = new Int16Array(R);
M = new Int32Array(R);
Q = new Uint8Array(R);
Ea = new Uint16Array(R);
Fa = new Uint32Array(R);
Ba = new Float32Array(R);
N = new Float64Array(R);
M[0] = 255;
A(255 === Q[0] && 0 === Q[3], "Typed arrays 2 must be run on a little-endian system");
Module.HEAP = k;
Module.HEAP8 = K;
Module.HEAP16 = Aa;
Module.HEAP32 = M;
Module.HEAPU8 = Q;
Module.HEAPU16 = Ea;
Module.HEAPU32 = Fa;
Module.HEAPF32 = Ba;
Module.HEAPF64 = N;

function Ka(a) {
    for (; 0 < a.length;) {
        var b = a.shift();
        if ("function" == typeof b) b();
        else {
            var c = b.l;
            "number" === typeof c ? b.i === k ? ka("v", c) : ka("vi", c, [b.i]) : c(b.i === k ? q : b.i)
        }
    }
}
var La = [],
    Ma = [],
    Na = [],
    Oa = r;

function S(a, b, c) {
    a = (new ma).O(a);
    c && (a.length = c);
    b || a.push(0);
    return a
}
Module.intArrayFromString = S;
Module.intArrayToString = function(a) {
    for (var b = [], c = 0; c < a.length; c++) {
        var d = a[c];
        255 < d && (d &= 255);
        b.push(String.fromCharCode(d))
    }
    return b.join("")
};

function xa(a, b, c) {
    a = S(a, c);
    for (c = 0; c < a.length;) K[b + c | 0] = a[c], c += 1
}
Module.writeStringToMemory = xa;

function ya(a, b) {
    for (var c = 0; c < a.length; c++) K[b + c | 0] = a[c]
}
Module.writeArrayToMemory = ya;

function Pa(a, b) {
    return 0 <= a ? a : 32 >= b ? 2 * Math.abs(1 << b - 1) + a : Math.pow(2, b) + a
}

function Qa(a, b) {
    if (0 >= a) return a;
    var c = 32 >= b ? Math.abs(1 << b - 1) : Math.pow(2, b - 1);
    if (a >= c && (32 >= b || a > c)) a = -2 * c + a;
    return a
}
Math.imul || (Math.imul = function(a, b) {
    var c = a & 65535,
        d = b & 65535;
    return c * d + ((a >>> 16) * d + c * (b >>> 16) << 16) | 0
});
var T = 0,
    Ra = {},
    Sa = r,
    Ta = q;

function Ua(a) {
    T++;
    Module.monitorRunDependencies && Module.monitorRunDependencies(T);
    a ? (A(!Ra[a]), Ra[a] = 1) : Module.g("warning: run dependency added without ID")
}
Module.addRunDependency = Ua;

function Va(a) {
    T--;
    Module.monitorRunDependencies && Module.monitorRunDependencies(T);
    a ? (A(Ra[a]), delete Ra[a]) : Module.g("warning: run dependency removed without ID");
    0 == T && (Ta !== q && (clearInterval(Ta), Ta = q), !Sa && Wa && Xa())
}
Module.removeRunDependency = Va;
Module.preloadedImages = {};
Module.preloadedAudios = {};
var Ga = 8,
    B = Ga + 2744,
    U;
U = U = P([0, 0, 0, 0, 0, 0, 0, 0], "i8", O);
P([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 69, 100, 103, 101, 83, 105, 103, 110, 40, 32, 100, 115, 116, 85, 112, 44, 32, 116, 101, 115, 115, 45, 62, 101, 118, 101, 110, 116, 44, 32, 111, 114, 103, 85, 112, 32, 41, 32, 60, 61, 32, 48, 0, 0, 0, 0, 0, 0, 101, 45, 62, 79, 114, 103, 32, 61, 61, 32, 118, 0, 0, 0, 0, 0, 33, 32, 86, 101, 114, 116, 69, 113, 40, 32, 100, 115, 116, 76, 111, 44, 32, 100, 115, 116, 85, 112, 32, 41, 0, 0, 0, 0, 0, 0, 0, 0, 99, 104, 105, 108, 100, 32, 60, 61, 32, 112, 113, 45, 62, 109, 97, 120, 0, 0, 0, 0, 0, 0, 0, 0, 118, 45, 62, 112, 114, 101, 118, 32, 61, 61, 32, 118, 80, 114, 101, 118, 0, 0, 0, 0, 0, 0, 0,
    0, 69, 82, 82, 79, 82, 44, 32, 99, 97, 110, 39, 116, 32, 104, 97, 110, 100, 108, 101, 32, 37, 100, 10, 0, 114, 101, 103, 80, 114, 101, 118, 45, 62, 119, 105, 110, 100, 105, 110, 103, 78, 117, 109, 98, 101, 114, 32, 45, 32, 101, 45, 62, 119, 105, 110, 100, 105, 110, 103, 32, 61, 61, 32, 114, 101, 103, 45, 62, 119, 105, 110, 100, 105, 110, 103, 78, 117, 109, 98, 101, 114, 0, 0, 0, 0, 0, 0, 0, 99, 117, 114, 114, 32, 60, 32, 112, 113, 45, 62, 109, 97, 120, 32, 38, 38, 32, 112, 113, 45, 62, 107, 101, 121, 115, 91, 99, 117, 114, 114, 93, 32, 33, 61, 32, 78, 85, 76, 76, 0, 0, 0, 0, 0, 0, 0, 0, 116, 101, 115, 115, 109, 111, 110, 111, 46, 99, 0, 0, 0,
    0, 0, 0, 102, 45, 62, 112, 114, 101, 118, 32, 61, 61, 32, 102, 80, 114, 101, 118, 32, 38, 38, 32, 102, 45, 62, 97, 110, 69, 100, 103, 101, 32, 61, 61, 32, 78, 85, 76, 76, 32, 38, 38, 32, 102, 45, 62, 100, 97, 116, 97, 32, 61, 61, 32, 78, 85, 76, 76, 0, 0, 0, 0, 0, 0, 0, 0, 86, 101, 114, 116, 76, 101, 113, 40, 32, 101, 45, 62, 79, 114, 103, 44, 32, 101, 45, 62, 68, 115, 116, 32, 41, 0, 0, 0, 0, 0, 0, 0, 99, 117, 114, 114, 32, 33, 61, 32, 76, 79, 78, 71, 95, 77, 65, 88, 0, 0, 0, 0, 0, 0, 0, 0, 101, 45, 62, 76, 102, 97, 99, 101, 32, 61, 61, 32, 102, 0, 0, 0, 114, 101, 103, 45, 62, 101, 85, 112, 45, 62, 119, 105, 110, 100, 105, 110, 103, 32, 61, 61, 32, 48,
    0, 0, 76, 69, 81, 40, 32, 42, 42, 40, 105, 43, 49, 41, 44, 32, 42, 42, 105, 32, 41, 0, 0, 0, 0, 0, 115, 119, 101, 101, 112, 46, 99, 0, 101, 45, 62, 79, 110, 101, 120, 116, 45, 62, 83, 121, 109, 45, 62, 76, 110, 101, 120, 116, 32, 61, 61, 32, 101, 0, 0, 0, 0, 0, 0, 0, 114, 101, 103, 45, 62, 119, 105, 110, 100, 105, 110, 103, 78, 117, 109, 98, 101, 114, 32, 61, 61, 32, 48, 0, 112, 113, 32, 33, 61, 32, 78, 85, 76, 76, 0, 0, 0, 0, 0, 0, 46, 47, 112, 114, 105, 111, 114, 105, 116, 121, 113, 45, 104, 101, 97, 112, 46, 99, 0, 0, 0, 0, 0, 0, 101, 45, 62, 76, 110, 101, 120, 116, 45, 62, 79, 110, 101, 120, 116, 45, 62, 83, 121, 109, 32, 61, 61, 32, 101, 0, 0, 0,
    0, 0, 0, 0, 43, 43, 102, 105, 120, 101, 100, 69, 100, 103, 101, 115, 32, 61, 61, 32, 49, 0, 0, 0, 0, 0, 0, 0, 112, 114, 105, 111, 114, 105, 116, 121, 113, 46, 99, 0, 0, 0, 0, 0, 103, 101, 111, 109, 46, 99, 0, 0, 115, 105, 122, 101, 32, 61, 61, 32, 49, 0, 0, 0, 0, 0, 0, 0, 101, 45, 62, 83, 121, 109, 45, 62, 83, 121, 109, 32, 61, 61, 32, 101, 0, 0, 0, 0, 0, 0, 0, 0, 108, 111, 45, 62, 76, 110, 101, 120, 116, 32, 33, 61, 32, 117, 112, 0, 114, 101, 103, 45, 62, 102, 105, 120, 85, 112, 112, 101, 114, 69, 100, 103, 101, 0, 0, 0, 0, 0, 0, 0, 104, 67, 117, 114, 114, 32, 62, 61, 32, 49, 32, 38, 38, 32, 104, 67, 117, 114, 114, 32, 60, 61, 32, 112, 113, 45, 62, 109,
    97, 120, 32, 38, 38, 32, 104, 91, 104, 67, 117, 114, 114, 93, 46, 107, 101, 121, 32, 33, 61, 32, 78, 85, 76, 76, 0, 0, 84, 114, 97, 110, 115, 76, 101, 113, 40, 32, 117, 44, 32, 118, 32, 41, 32, 38, 38, 32, 84, 114, 97, 110, 115, 76, 101, 113, 40, 32, 118, 44, 32, 119, 32, 41, 0, 0, 0, 0, 115, 105, 122, 101, 32, 61, 61, 32, 48, 0, 0, 0, 0, 0, 0, 0, 101, 45, 62, 83, 121, 109, 32, 33, 61, 32, 101, 0, 0, 0, 0, 0, 84, 79, 76, 69, 82, 65, 78, 67, 69, 95, 78, 79, 78, 90, 69, 82, 79, 0, 0, 0, 0, 0, 0, 0, 70, 65, 76, 83, 69, 0, 0, 0, 33, 32, 86, 101, 114, 116, 69, 113, 40, 32, 101, 85, 112, 45, 62, 68, 115, 116, 44, 32, 101, 76, 111, 45, 62, 68, 115, 116, 32, 41,
    0, 0, 117, 112, 45, 62, 76, 110, 101, 120, 116, 32, 33, 61, 32, 117, 112, 32, 38, 38, 32, 117, 112, 45, 62, 76, 110, 101, 120, 116, 45, 62, 76, 110, 101, 120, 116, 32, 33, 61, 32, 117, 112, 0, 0, 0, 0, 0, 0, 0, 114, 101, 110, 100, 101, 114, 46, 99, 0, 0, 0, 0, 0, 0, 0, 0, 105, 115, 101, 99, 116, 46, 115, 32, 60, 61, 32, 77, 65, 88, 40, 32, 111, 114, 103, 76, 111, 45, 62, 115, 44, 32, 111, 114, 103, 85, 112, 45, 62, 115, 32, 41, 0, 0, 0, 0, 77, 73, 78, 40, 32, 100, 115, 116, 76, 111, 45, 62, 115, 44, 32, 100, 115, 116, 85, 112, 45, 62, 115, 32, 41, 32, 60, 61, 32, 105, 115, 101, 99, 116, 46, 115, 0, 0, 0, 0, 101, 45, 62, 76, 110, 101, 120, 116, 32,
    33, 61, 32, 101, 0, 0, 0, 105, 115, 101, 99, 116, 46, 116, 32, 60, 61, 32, 77, 65, 88, 40, 32, 111, 114, 103, 76, 111, 45, 62, 116, 44, 32, 100, 115, 116, 76, 111, 45, 62, 116, 32, 41, 0, 0, 0, 0, 102, 114, 101, 101, 95, 104, 97, 110, 100, 108, 101, 32, 33, 61, 32, 76, 79, 78, 71, 95, 77, 65, 88, 0, 101, 45, 62, 83, 121, 109, 45, 62, 110, 101, 120, 116, 32, 61, 61, 32, 101, 80, 114, 101, 118, 45, 62, 83, 121, 109, 32, 38, 38, 32, 101, 45, 62, 83, 121, 109, 32, 61, 61, 32, 38, 109, 101, 115, 104, 45, 62, 101, 72, 101, 97, 100, 83, 121, 109, 32, 38, 38, 32, 101, 45, 62, 83, 121, 109, 45, 62, 83, 121, 109, 32, 61, 61, 32, 101, 32, 38, 38, 32, 101,
    45, 62, 79, 114, 103, 32, 61, 61, 32, 78, 85, 76, 76, 32, 38, 38, 32, 101, 45, 62, 68, 115, 116, 32, 61, 61, 32, 78, 85, 76, 76, 32, 38, 38, 32, 101, 45, 62, 76, 102, 97, 99, 101, 32, 61, 61, 32, 78, 85, 76, 76, 32, 38, 38, 32, 101, 45, 62, 82, 102, 97, 99, 101, 32, 61, 61, 32, 78, 85, 76, 76, 0, 77, 73, 78, 40, 32, 111, 114, 103, 85, 112, 45, 62, 116, 44, 32, 100, 115, 116, 85, 112, 45, 62, 116, 32, 41, 32, 60, 61, 32, 105, 115, 101, 99, 116, 46, 116, 0, 0, 0, 0, 86, 101, 114, 116, 76, 101, 113, 40, 32, 117, 44, 32, 118, 32, 41, 32, 38, 38, 32, 86, 101, 114, 116, 76, 101, 113, 40, 32, 118, 44, 32, 119, 32, 41, 0, 0, 0, 0, 0, 0, 101, 45, 62, 68, 115,
    116, 32, 33, 61, 32, 78, 85, 76, 76, 0, 0, 33, 32, 114, 101, 103, 85, 112, 45, 62, 102, 105, 120, 85, 112, 112, 101, 114, 69, 100, 103, 101, 32, 38, 38, 32, 33, 32, 114, 101, 103, 76, 111, 45, 62, 102, 105, 120, 85, 112, 112, 101, 114, 69, 100, 103, 101, 0, 0, 101, 45, 62, 79, 114, 103, 32, 33, 61, 32, 78, 85, 76, 76, 0, 0, 102, 45, 62, 109, 97, 114, 107, 101, 100, 0, 0, 0, 0, 0, 0, 0, 111, 114, 103, 85, 112, 32, 33, 61, 32, 116, 101, 115, 115, 45, 62, 101, 118, 101, 110, 116, 32, 38, 38, 32, 111, 114, 103, 76, 111, 32, 33, 61, 32, 116, 101, 115, 115, 45, 62, 101, 118, 101, 110, 116, 0, 0, 0, 0, 101, 45, 62, 83, 121, 109, 45, 62, 110, 101, 120,
    116, 32, 61, 61, 32, 101, 80, 114, 101, 118, 45, 62, 83, 121, 109, 0, 0, 0, 0, 0, 0, 69, 100, 103, 101, 83, 105, 103, 110, 40, 32, 100, 115, 116, 76, 111, 44, 32, 116, 101, 115, 115, 45, 62, 101, 118, 101, 110, 116, 44, 32, 111, 114, 103, 76, 111, 32, 41, 32, 62, 61, 32, 48, 0, 0, 0, 0, 0, 0, 118, 45, 62, 112, 114, 101, 118, 32, 61, 61, 32, 118, 80, 114, 101, 118, 32, 38, 38, 32, 118, 45, 62, 97, 110, 69, 100, 103, 101, 32, 61, 61, 32, 78, 85, 76, 76, 32, 38, 38, 32, 118, 45, 62, 100, 97, 116, 97, 32, 61, 61, 32, 78, 85, 76, 76, 0, 0, 0, 0, 0, 0, 0, 0, 102, 45, 62, 112, 114, 101, 118, 32, 61, 61, 32, 102, 80, 114, 101, 118, 0, 0, 0, 0, 0, 0, 0, 0, 109,
    101, 115, 104, 46, 99, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 95, 95, 103, 108, 95, 116, 114, 97, 110, 115, 83,
    105, 103, 110, 0, 0, 95, 95, 103, 108, 95, 116, 114, 97, 110, 115, 69, 118, 97, 108, 0, 0, 95, 95, 103, 108, 95, 114, 101, 110, 100, 101, 114, 77, 101, 115, 104, 0, 95, 95, 103, 108, 95, 112, 113, 83, 111, 114, 116, 73, 110, 115, 101, 114, 116, 0, 0, 0, 0, 0, 0, 0, 95, 95, 103, 108, 95, 112, 113, 83, 111, 114, 116, 73, 110, 105, 116, 0, 95, 95, 103, 108, 95, 112, 113, 83, 111, 114, 116, 68, 101, 108, 101, 116, 101, 80, 114, 105, 111, 114, 105, 116, 121, 81, 0, 0, 0, 0, 0, 0, 95, 95, 103, 108, 95, 112, 113, 83, 111, 114, 116, 68, 101, 108, 101, 116, 101, 0, 0, 0, 0, 0, 0, 0, 95, 95, 103, 108, 95, 112, 113, 72, 101, 97, 112, 73, 110, 115, 101,
    114, 116, 0, 0, 0, 0, 0, 0, 0, 95, 95, 103, 108, 95, 112, 113, 72, 101, 97, 112, 68, 101, 108, 101, 116, 101, 0, 0, 0, 0, 0, 0, 0, 95, 95, 103, 108, 95, 109, 101, 115, 104, 84, 101, 115, 115, 101, 108, 108, 97, 116, 101, 77, 111, 110, 111, 82, 101, 103, 105, 111, 110, 0, 0, 0, 95, 95, 103, 108, 95, 109, 101, 115, 104, 67, 104, 101, 99, 107, 77, 101, 115, 104, 0, 0, 0, 0, 0, 0, 95, 95, 103, 108, 95, 101, 100, 103, 101, 83, 105, 103, 110, 0, 0, 0, 95, 95, 103, 108, 95, 101, 100, 103, 101, 69, 118, 97, 108, 0, 0, 0, 82, 101, 110, 100, 101, 114, 84, 114, 105, 97, 110, 103, 108, 101, 0, 0, 82, 101, 110, 100, 101, 114, 83, 116, 114, 105, 112, 0, 0,
    0, 0, 0, 82, 101, 110, 100, 101, 114, 70, 97, 110, 0, 0, 0, 0, 0, 0, 0, 82, 101, 109, 111, 118, 101, 68, 101, 103, 101, 110, 101, 114, 97, 116, 101, 70, 97, 99, 101, 115, 0, 0, 0, 73, 115, 87, 105, 110, 100, 105, 110, 103, 73, 110, 115, 105, 100, 101, 0, 70, 108, 111, 97, 116, 68, 111, 119, 110, 0, 0, 0, 0, 0, 0, 0, 70, 105, 120, 85, 112, 112, 101, 114, 69, 100, 103, 101, 0, 0, 0, 0, 68, 111, 110, 101, 69, 100, 103, 101, 68, 105, 99, 116, 0, 0, 0, 0, 68, 101, 108, 101, 116, 101, 82, 101, 103, 105, 111, 110, 0, 0, 0, 0, 67, 111, 110, 110, 101, 99, 116, 76, 101, 102, 116, 68, 101, 103, 101, 110, 101, 114, 97, 116, 101, 0, 0, 0, 67, 104, 101, 99,
    107, 70, 111, 114, 76, 101, 102, 116, 83, 112, 108, 105, 99, 101, 0, 0, 0, 0, 0, 0, 67, 104, 101, 99, 107, 70, 111, 114, 73, 110, 116, 101, 114, 115, 101, 99, 116, 0, 0, 0, 0, 0, 0, 0, 65, 100, 100, 82, 105, 103, 104, 116, 69, 100, 103, 101, 115, 0, 0, 0, 0, 0, 0, 63, 0, 0, 0, 63, 0, 0, 0, 0, 0, 0, 0, 0
], "i8", Ca, 8);
var Ya = ra(P(12, "i8", O), 8);
A(0 == Ya % 8);
Module._memcpy = Za;
var $a = 5,
    ab = 6,
    bb = 9,
    V = 13,
    cb = 21,
    db = 22,
    eb = 0;

function W(a) {
    return M[eb >> 2] = a
}
var fb = P(1, "i32*", O),
    X = P(1, "i32*", O);
U = P(1, "i32*", O);
var gb = P(1, "i32*", O),
    hb = 2,
    Y = [q],
    ib = n;

function jb(a, b) {
    if ("string" !== typeof a) return q;
    b === k && (b = "/");
    a && "/" == a[0] && (b = "");
    for (var c = (b + "/" + a).split("/").reverse(), d = [""]; c.length;) {
        var e = c.pop();
        "" == e || "." == e || (".." == e ? 1 < d.length && d.pop() : d.push(e))
    }
    return 1 == d.length ? "/" : d.join("/")
}

function kb(a, b, c) {
    var d = {
            N: r,
            k: r,
            error: 0,
            name: q,
            path: q,
            object: q,
            w: r,
            A: q,
            z: q
        },
        a = jb(a);
    if ("/" == a) d.N = n, d.k = d.w = n, d.name = "/", d.path = d.A = "/", d.object = d.z = lb;
    else if (a !== q)
        for (var c = c || 0, a = a.slice(1).split("/"), e = lb, f = [""]; a.length;) {
            1 == a.length && e.c && (d.w = n, d.A = 1 == f.length ? "/" : f.join("/"), d.z = e, d.name = a[0]);
            var g = a.shift();
            if (e.c)
                if (e.C) {
                    if (!e.a.hasOwnProperty(g)) {
                        d.error = 2;
                        break
                    }
                } else {
                    d.error = V;
                    break
                }
            else {
                d.error = 20;
                break
            }
            e = e.a[g];
            if (e.link && !(b && 0 == a.length)) {
                if (40 < c) {
                    d.error = 92;
                    break
                }
                d = jb(e.link,
                    f.join("/"));
                d = kb([d].concat(a).join("/"), b, c + 1);
                break
            }
            f.push(g);
            0 == a.length && (d.k = n, d.path = f.join("/"), d.object = e)
        }
    return d
}

function mb(a) {
    nb();
    a = kb(a, k);
    if (a.k) return a.object;
    W(a.error);
    return q
}

function ob(a, b, c, d, e) {
    a || (a = "/");
    "string" === typeof a && (a = mb(a));
    a || (W(V), j(Error("Parent path must exist.")));
    a.c || (W(20), j(Error("Parent must be a folder.")));
    !a.write && !ib && (W(V), j(Error("Parent folder must be writeable.")));
    if (!b || "." == b || ".." == b) W(2), j(Error("Name must not be empty."));
    a.a.hasOwnProperty(b) && (W(17), j(Error("Can't overwrite object.")));
    a.a[b] = {
        C: d === k ? n : d,
        write: e === k ? r : e,
        timestamp: Date.now(),
        M: hb++
    };
    for (var f in c) c.hasOwnProperty(f) && (a.a[b][f] = c[f]);
    return a.a[b]
}

function pb(a, b, c, d) {
    return ob(a, b, {
        c: n,
        b: r,
        a: {}
    }, c, d)
}

function qb(a, b, c, d) {
    a = mb(a);
    a === q && j(Error("Invalid parent."));
    for (b = b.split("/").reverse(); b.length;) {
        var e = b.pop();
        e && (a.a.hasOwnProperty(e) || pb(a, e, c, d), a = a.a[e])
    }
    return a
}

function rb(a, b, c, d, e) {
    c.c = r;
    return ob(a, b, c, d, e)
}

function sb(a, b, c, d, e) {
    if ("string" === typeof c) {
        for (var f = Array(c.length), g = 0, i = c.length; g < i; ++g) f[g] = c.charCodeAt(g);
        c = f
    }
    c = {
        b: r,
        a: c.subarray ? c.subarray(0) : c
    };
    return rb(a, b, c, d, e)
}

function Z(a, b, c, d) {
    !c && !d && j(Error("A device must have at least one callback defined."));
    return rb(a, b, {
        b: n,
        input: c,
        d: d
    }, Boolean(c), Boolean(d))
}

function nb() {
    lb || (lb = {
        C: n,
        write: n,
        c: n,
        b: r,
        timestamp: Date.now(),
        M: 1,
        a: {}
    })
}
var tb, lb;

function ub(a, b, c) {
    a = Y[a];
    if (!a) return -1;
    a.sender(Q.subarray(b, b + c));
    return c
}

function vb(a, b, c, d) {
    a = Y[a];
    if (!a || a.object.b) return W(bb), -1;
    if (a.f) {
        if (a.object.c) return W(cb), -1;
        if (0 > c || 0 > d) return W(db), -1;
        for (var e = a.object.a; e.length < d;) e.push(0);
        for (var f = 0; f < c; f++) e[d + f] = Q[b + f | 0];
        a.object.timestamp = Date.now();
        return f
    }
    W(V);
    return -1
}

function wb(a, b, c) {
    var d = Y[a];
    if (d && "socket" in d) return ub(a, b, c);
    if (d) {
        if (d.f) {
            if (0 > c) return W(db), -1;
            if (d.object.b) {
                if (d.object.d) {
                    for (a = 0; a < c; a++) try {
                        d.object.d(K[b + a | 0])
                    } catch (e) {
                        return W($a), -1
                    }
                    d.object.timestamp = Date.now();
                    return a
                }
                W(ab);
                return -1
            }
            b = vb(a, b, c, d.position); - 1 != b && (d.position += b);
            return b
        }
        W(V);
        return -1
    }
    W(bb);
    return -1
}

function xb(a, b) {
    var c = Pa(a & 255);
    K[xb.D | 0] = c;
    return -1 == wb(b, xb.D, 1) ? (Y[b] && (Y[b].error = n), -1) : c
}
Module._saveSetjmp = yb;
Module._testSetjmp = zb;
Module._memset = Ab;

function Bb(a, b, c, d) {
    c *= b;
    if (0 == c) return 0;
    a = wb(d, a, c);
    return -1 == a ? (Y[d] && (Y[d].error = n), 0) : Math.floor(a / b)
}
Module._strlen = Cb;

function Db(a) {
    return 0 > a || 0 === a && -Infinity === 1 / a
}

function Eb(a, b) {
    function c(a) {
        var c;
        "double" === a ? c = N[b + e >> 3] : "i64" == a ? (c = [M[b + e >> 2], M[b + (e + 8) >> 2]], e += 8) : (a = "i32", c = M[b + e >> 2]);
        e += Math.max(Math.max(ja(a), y), 8);
        return c
    }
    for (var d = a, e = 0, f = [], g, i;;) {
        var l = d;
        g = K[d];
        if (0 === g) break;
        i = K[d + 1 | 0];
        if (37 == g) {
            var z = r,
                E = r,
                x = r,
                t = r;
            a: for (;;) {
                switch (i) {
                    case 43:
                        z = n;
                        break;
                    case 45:
                        E = n;
                        break;
                    case 35:
                        x = n;
                        break;
                    case 48:
                        if (t) break a;
                        else {
                            t = n;
                            break
                        }
                        default:
                            break a
                }
                d++;
                i = K[d + 1 | 0]
            }
            var C = 0;
            if (42 == i) C = c("i32"), d++, i = K[d + 1 | 0];
            else
                for (; 48 <= i && 57 >= i;) C = 10 * C + (i - 48), d++, i = K[d +
                    1 | 0];
            var L = r;
            if (46 == i) {
                var m = 0,
                    L = n;
                d++;
                i = K[d + 1 | 0];
                if (42 == i) m = c("i32"), d++;
                else
                    for (;;) {
                        i = K[d + 1 | 0];
                        if (48 > i || 57 < i) break;
                        m = 10 * m + (i - 48);
                        d++
                    }
                i = K[d + 1 | 0]
            } else m = 6;
            var p;
            switch (String.fromCharCode(i)) {
                case "h":
                    i = K[d + 2 | 0];
                    104 == i ? (d++, p = 1) : p = 2;
                    break;
                case "l":
                    i = K[d + 2 | 0];
                    108 == i ? (d++, p = 8) : p = 4;
                    break;
                case "L":
                case "q":
                case "j":
                    p = 8;
                    break;
                case "z":
                case "t":
                case "I":
                    p = 4;
                    break;
                default:
                    p = q
            }
            p && d++;
            i = K[d + 1 | 0];
            switch (String.fromCharCode(i)) {
                case "d":
                case "i":
                case "u":
                case "o":
                case "x":
                case "X":
                case "p":
                    l = 100 == i || 105 == i;
                    p = p || 4;
                    var F = g = c("i" + 8 * p),
                        h;
                    8 == p && (g = 117 == i ? +(g[0] >>> 0) + 4294967296 * +(g[1] >>> 0) : +(g[0] >>> 0) + 4294967296 * +(g[1] | 0));
                    4 >= p && (g = (l ? Qa : Pa)(g & Math.pow(256, p) - 1, 8 * p));
                    var D = Math.abs(g),
                        l = "";
                    if (100 == i || 105 == i) h = 8 == p && Fb ? Fb.stringify(F[0], F[1], q) : Qa(g, 8 * p).toString(10);
                    else if (117 == i) h = 8 == p && Fb ? Fb.stringify(F[0], F[1], n) : Pa(g, 8 * p).toString(10), g = Math.abs(g);
                    else if (111 == i) h = (x ? "0" : "") + D.toString(8);
                    else if (120 == i || 88 == i) {
                        l = x && 0 != g ? "0x" : "";
                        if (8 == p && Fb)
                            if (F[1]) {
                                h = (F[1] >>> 0).toString(16);
                                for (x = (F[0] >>> 0).toString(16); 8 >
                                    x.length;) x = "0" + x;
                                h += x
                            } else h = (F[0] >>> 0).toString(16);
                        else if (0 > g) {
                            g = -g;
                            h = (D - 1).toString(16);
                            F = [];
                            for (x = 0; x < h.length; x++) F.push((15 - parseInt(h[x], 16)).toString(16));
                            for (h = F.join(""); h.length < 2 * p;) h = "f" + h
                        } else h = D.toString(16);
                        88 == i && (l = l.toUpperCase(), h = h.toUpperCase())
                    } else 112 == i && (0 === D ? h = "(nil)" : (l = "0x", h = D.toString(16)));
                    if (L)
                        for (; h.length < m;) h = "0" + h;
                    for (z && (l = 0 > g ? "-" + l : "+" + l); l.length + h.length < C;) E ? h += " " : t ? h = "0" + h : l = " " + l;
                    h = l + h;
                    h.split("").forEach(function(a) {
                        f.push(a.charCodeAt(0))
                    });
                    break;
                case "f":
                case "F":
                case "e":
                case "E":
                case "g":
                case "G":
                    g = c("double");
                    if (isNaN(g)) h = "nan", t = r;
                    else if (isFinite(g)) {
                        L = r;
                        p = Math.min(m, 20);
                        if (103 == i || 71 == i) L = n, m = m || 1, p = parseInt(g.toExponential(p).split("e")[1], 10), m > p && -4 <= p ? (i = (103 == i ? "f" : "F").charCodeAt(0), m -= p + 1) : (i = (103 == i ? "e" : "E").charCodeAt(0), m--), p = Math.min(m, 20);
                        if (101 == i || 69 == i) h = g.toExponential(p), /[eE][-+]\d$/.test(h) && (h = h.slice(0, -1) + "0" + h.slice(-1));
                        else if (102 == i || 70 == i) h = g.toFixed(p), 0 === g && Db(g) && (h = "-" + h);
                        l = h.split("e");
                        if (L && !x)
                            for (; 1 <
                                l[0].length && -1 != l[0].indexOf(".") && ("0" == l[0].slice(-1) || "." == l[0].slice(-1));) l[0] = l[0].slice(0, -1);
                        else
                            for (x && -1 == h.indexOf(".") && (l[0] += "."); m > p++;) l[0] += "0";
                        h = l[0] + (1 < l.length ? "e" + l[1] : "");
                        69 == i && (h = h.toUpperCase());
                        z && 0 <= g && (h = "+" + h)
                    } else h = (0 > g ? "-" : "") + "inf", t = r;
                    for (; h.length < C;) h = E ? h + " " : t && ("-" == h[0] || "+" == h[0]) ? h[0] + "0" + h.slice(1) : (t ? "0" : " ") + h;
                    97 > i && (h = h.toUpperCase());
                    h.split("").forEach(function(a) {
                        f.push(a.charCodeAt(0))
                    });
                    break;
                case "s":
                    t = (z = c("i8*")) ? Cb(z) : 6;
                    L && (t = Math.min(t, m));
                    if (!E)
                        for (; t < C--;) f.push(32);
                    if (z)
                        for (x = 0; x < t; x++) f.push(Q[z++ | 0]);
                    else f = f.concat(S("(null)".substr(0, t), n));
                    if (E)
                        for (; t < C--;) f.push(32);
                    break;
                case "c":
                    for (E && f.push(c("i8")); 0 < --C;) f.push(32);
                    E || f.push(c("i8"));
                    break;
                case "n":
                    E = c("i32*");
                    M[E >> 2] = f.length;
                    break;
                case "%":
                    f.push(g);
                    break;
                default:
                    for (x = l; x < d + 2; x++) f.push(K[x])
            }
            d += 2
        } else f.push(g), d += 1
    }
    return f
}

function Gb(a) {
    Gb.J || (G = G + 4095 >> 12 << 12, Gb.J = n, A(pa), Gb.I = pa, pa = function() {
        H("cannot dynamically allocate, sbrk now has control")
    });
    var b = G;
    0 != a && Gb.I(a);
    return b
}
var Hb = r,
    Ib = r,
    Jb = r,
    Kb = r,
    Lb = k,
    Mb = k,
    Nb = [];

function Ob() {
    var a = Module.canvas;
    Nb.forEach(function(b) {
        b(a.width, a.height)
    })
}

function Pb() {
    var a = Module.canvas;
    this.S = a.width;
    this.R = a.height;
    a.width = screen.width;
    a.height = screen.height;
    "undefined" != typeof SDL && (a = Fa[SDL.screen + 0 * y >> 2], M[SDL.screen + 0 * y >> 2] = a | 8388608);
    Ob()
}

function Qb() {
    var a = Module.canvas;
    a.width = this.S;
    a.height = this.R;
    "undefined" != typeof SDL && (a = Fa[SDL.screen + 0 * y >> 2], M[SDL.screen + 0 * y >> 2] = a & -8388609);
    Ob()
}
var Rb, Sb, Tb, Ub;
xb.D = P([0], "i8", O);
La.unshift({
    l: function() {
        if (!Module.noFSInit && !tb) {
            var a, b, c, d = function(a) {
                a === q || 10 === a ? (b.h(b.buffer.join("")), b.buffer = []) : b.buffer.push(i.B(a))
            };
            A(!tb, "FS.init was previously called. If you want to initialize later with custom parameters, remove any earlier calls (note that one is automatically added to the generated code)");
            tb = n;
            nb();
            a = a || Module.stdin;
            b = b || Module.stdout;
            c = c || Module.stderr;
            var e = n,
                f = n,
                g = n;
            a || (e = r, a = function() {
                if (!a.j || !a.j.length) {
                    var b;
                    "undefined" != typeof window && "function" ==
                        typeof window.prompt ? (b = window.prompt("Input: "), b === q && (b = String.fromCharCode(0))) : "function" == typeof readline && (b = readline());
                    b || (b = "");
                    a.j = S(b + "\n", n)
                }
                return a.j.shift()
            });
            var i = new ma;
            b || (f = r, b = d);
            b.h || (b.h = Module.print);
            b.buffer || (b.buffer = []);
            c || (g = r, c = d);
            c.h || (c.h = Module.print);
            c.buffer || (c.buffer = []);
            try {
                pb("/", "tmp", n, n)
            } catch (l) {}
            var d = pb("/", "dev", n, n),
                z = Z(d, "stdin", a),
                E = Z(d, "stdout", q, b);
            c = Z(d, "stderr", q, c);
            Z(d, "tty", a, b);
            Z(d, "null", u(), u());
            Y[1] = {
                path: "/dev/stdin",
                object: z,
                position: 0,
                u: n,
                f: r,
                t: r,
                v: !e,
                error: r,
                q: r,
                F: []
            };
            Y[2] = {
                path: "/dev/stdout",
                object: E,
                position: 0,
                u: r,
                f: n,
                t: r,
                v: !f,
                error: r,
                q: r,
                F: []
            };
            Y[3] = {
                path: "/dev/stderr",
                object: c,
                position: 0,
                u: r,
                f: n,
                t: r,
                v: !g,
                error: r,
                q: r,
                F: []
            };
            M[fb >> 2] = 1;
            M[X >> 2] = 2;
            M[U >> 2] = 3;
            qb("/", "dev/shm/tmp", n, n);
            for (e = Y.length; e < Math.max(fb, X, U) + 4; e++) Y[e] = q;
            Y[fb] = Y[1];
            Y[X] = Y[2];
            Y[U] = Y[3];
            P([P([0, 0, 0, 0, fb, 0, 0, 0, X, 0, 0, 0, U, 0, 0, 0], "void*", 0)], "void*", Ca, gb)
        }
    }
});
Ma.push({
    l: function() {
        ib = r
    }
});
Na.push({
    l: function() {
        tb && (Y[2] && 0 < Y[2].object.d.buffer.length && Y[2].object.d(10), Y[3] && 0 < Y[3].object.d.buffer.length && Y[3].object.d(10))
    }
});
Module.FS_createFolder = pb;
Module.FS_createPath = qb;
Module.FS_createDataFile = sb;
Module.FS_createPreloadedFile = function(a, b, c, d, e, f, g, i) {
    function l() {
        Jb = document.pointerLockElement === t || document.mozPointerLockElement === t || document.webkitPointerLockElement === t
    }

    function z(a) {
        return {
            jpg: "image/jpeg",
            jpeg: "image/jpeg",
            png: "image/png",
            bmp: "image/bmp",
            ogg: "audio/ogg",
            wav: "audio/wav",
            mp3: "audio/mpeg"
        } [a.substr(a.lastIndexOf(".") + 1)]
    }

    function E(c) {
        function h(c) {
            i || sb(a, b, c, d, e);
            f && f();
            Va("cp " + C)
        }
        var l = r;
        Module.preloadPlugins.forEach(function(a) {
            !l && a.canHandle(C) && (a.handle(c, C, h, function() {
                g &&
                    g();
                Va("cp " + C)
            }), l = n)
        });
        l || h(c)
    }
    Module.preloadPlugins || (Module.preloadPlugins = []);
    if (!Rb && !v) {
        Rb = n;
        try {
            new Blob, Sb = n
        } catch (x) {
            Sb = r, console.log("warning: no blob constructor, cannot create blobs with mimetypes")
        }
        Tb = "undefined" != typeof MozBlobBuilder ? MozBlobBuilder : "undefined" != typeof WebKitBlobBuilder ? WebKitBlobBuilder : !Sb ? console.log("warning: no BlobBuilder") : q;
        Ub = "undefined" != typeof window ? window.URL ? window.URL : window.webkitURL : console.log("warning: cannot create object URLs");
        Module.preloadPlugins.push({
            canHandle: function(a) {
                return !Module.W &&
                    /\.(jpg|jpeg|png|bmp)$/.exec(a)
            },
            handle: function(a, b, c, d) {
                var e = q;
                if (Sb) try {
                    e = new Blob([a], {
                        type: z(b)
                    })
                } catch (f) {
                    var g = "Blob constructor present but fails: " + f + "; falling back to blob builder";
                    la || (la = {});
                    la[g] || (la[g] = 1, Module.g(g))
                }
                e || (e = new Tb, e.append((new Uint8Array(a)).buffer), e = e.getBlob());
                var i = Ub.createObjectURL(e),
                    h = new Image;
                h.onload = function() {
                    A(h.complete, "Image " + b + " could not be decoded");
                    var d = document.createElement("canvas");
                    d.width = h.width;
                    d.height = h.height;
                    d.getContext("2d").drawImage(h,
                        0, 0);
                    Module.preloadedImages[b] = d;
                    Ub.revokeObjectURL(i);
                    c && c(a)
                };
                h.onerror = function() {
                    console.log("Image " + i + " could not be decoded");
                    d && d()
                };
                h.src = i
            }
        });
        Module.preloadPlugins.push({
            canHandle: function(a) {
                return !Module.V && a.substr(-4) in {
                    ".ogg": 1,
                    ".wav": 1,
                    ".mp3": 1
                }
            },
            handle: function(a, b, c, d) {
                function e(d) {
                    g || (g = n, Module.preloadedAudios[b] = d, c && c(a))
                }

                function f() {
                    g || (g = n, Module.preloadedAudios[b] = new Audio, d && d())
                }
                var g = r;
                if (Sb) {
                    try {
                        var i = new Blob([a], {
                            type: z(b)
                        })
                    } catch (h) {
                        return f()
                    }
                    var i = Ub.createObjectURL(i),
                        l = new Audio;
                    l.addEventListener("canplaythrough", function() {
                        e(l)
                    }, r);
                    l.onerror = function() {
                        if (!g) {
                            console.log("warning: browser could not fully decode audio " + b + ", trying slower base64 approach");
                            for (var c = "", d = 0, f = 0, i = 0; i < a.length; i++) {
                                d = d << 8 | a[i];
                                for (f += 8; 6 <= f;) var h = d >> f - 6 & 63,
                                    f = f - 6,
                                    c = c + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/" [h]
                            }
                            2 == f ? (c += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/" [(d & 3) << 4], c += "==") : 4 == f && (c += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/" [(d &
                                15) << 2], c += "=");
                            l.src = "data:audio/x-" + b.substr(-3) + ";base64," + c;
                            e(l)
                        }
                    };
                    l.src = i;
                    setTimeout(function() {
                        I || e(l)
                    }, 1E4)
                } else return f()
            }
        });
        var t = Module.canvas;
        t.n = t.requestPointerLock || t.mozRequestPointerLock || t.webkitRequestPointerLock;
        t.r = document.exitPointerLock || document.mozExitPointerLock || document.webkitExitPointerLock || u();
        t.r = t.r.bind(document);
        document.addEventListener("pointerlockchange", l, r);
        document.addEventListener("mozpointerlockchange", l, r);
        document.addEventListener("webkitpointerlockchange",
            l, r);
        Module.elementPointerLock && t.addEventListener("click", function(a) {
            !Jb && t.n && (t.n(), a.preventDefault())
        }, r)
    }
    for (var C, L = [a, b], m = L[0], p = 1; p < L.length; p++) "/" != m[m.length - 1] && (m += "/"), m += L[p];
    "/" == m[0] && (m = m.substr(1));
    C = m;
    Ua("cp " + C);
    if ("string" == typeof c) {
        var F = g,
            h = function() {
                F ? F() : j('Loading data file "' + c + '" failed.')
            },
            D = new XMLHttpRequest;
        D.open("GET", c, n);
        D.responseType = "arraybuffer";
        D.onload = function() {
            if (200 == D.status || 0 == D.status && D.response) {
                var a = D.response;
                A(a, 'Loading data file "' + c +
                    '" failed (no arrayBuffer).');
                a = new Uint8Array(a);
                E(a);
                Va("al " + c)
            } else h()
        };
        D.onerror = h;
        D.send(q);
        Ua("al " + c)
    } else E(c)
};
Module.FS_createLazyFile = function(a, b, c, d, e) {
    if ("undefined" !== typeof XMLHttpRequest) {
        v || j("Cannot do synchronous binary XHRs outside webworkers in modern browsers. Use --embed-file or --preload-file in emcc");
        var f = function() {
            this.m = r;
            this.e = []
        };
        f.prototype.get = function(a) {
            if (!(a > this.length - 1 || 0 > a)) {
                var c = a % this.K;
                return this.L(Math.floor(a / this.K))[c]
            }
        };
        f.prototype.Q = function(a) {
            this.L = a
        };
        f.prototype.o = function() {
            var a = new XMLHttpRequest;
            a.open("HEAD", c, r);
            a.send(q);
            200 <= a.status && 300 > a.status ||
                304 === a.status || j(Error("Couldn't load " + c + ". Status: " + a.status));
            var b = Number(a.getResponseHeader("Content-length")),
                d, e = 1048576;
            if (!((d = a.getResponseHeader("Accept-Ranges")) && "bytes" === d)) e = b;
            var f = this;
            f.Q(function(a) {
                var d = a * e,
                    g = (a + 1) * e - 1,
                    g = Math.min(g, b - 1);
                if ("undefined" === typeof f.e[a]) {
                    var l = f.e;
                    d > g && j(Error("invalid range (" + d + ", " + g + ") or no bytes requested!"));
                    g > b - 1 && j(Error("only " + b + " bytes available! programmer error!"));
                    var m = new XMLHttpRequest;
                    m.open("GET", c, r);
                    b !== e && m.setRequestHeader("Range",
                        "bytes=" + d + "-" + g);
                    "undefined" != typeof Uint8Array && (m.responseType = "arraybuffer");
                    m.overrideMimeType && m.overrideMimeType("text/plain; charset=x-user-defined");
                    m.send(q);
                    200 <= m.status && 300 > m.status || 304 === m.status || j(Error("Couldn't load " + c + ". Status: " + m.status));
                    d = m.response !== k ? new Uint8Array(m.response || []) : S(m.responseText || "", n);
                    l[a] = d
                }
                "undefined" === typeof f.e[a] && j(Error("doXHR failed!"));
                return f.e[a]
            });
            this.H = b;
            this.G = e;
            this.m = n
        };
        f = new f;
        Object.defineProperty(f, "length", {
            get: function() {
                this.m ||
                    this.o();
                return this.H
            }
        });
        Object.defineProperty(f, "chunkSize", {
            get: function() {
                this.m || this.o();
                return this.G
            }
        });
        f = {
            b: r,
            a: f
        }
    } else f = {
        b: r,
        url: c
    };
    return rb(a, b, f, d, e)
};
Module.FS_createLink = function(a, b, c, d, e) {
    return rb(a, b, {
        b: r,
        link: c
    }, d, e)
};
Module.FS_createDevice = Z;
eb = oa(4);
M[eb >> 2] = 0;
Module.requestFullScreen = function(a, b) {
    function c() {
        Ib = r;
        (document.webkitFullScreenElement || document.webkitFullscreenElement || document.mozFullScreenElement || document.mozFullscreenElement || document.fullScreenElement || document.fullscreenElement) === d ? (d.p = document.cancelFullScreen || document.mozCancelFullScreen || document.webkitCancelFullScreen, d.p = d.p.bind(document), Lb && d.n(), Ib = n, Mb && Pb()) : Mb && Qb();
        if (Module.onFullScreen) Module.onFullScreen(Ib)
    }
    Lb = a;
    Mb = b;
    "undefined" === typeof Lb && (Lb = n);
    "undefined" ===
    typeof Mb && (Mb = r);
    var d = Module.canvas;
    Kb || (Kb = n, document.addEventListener("fullscreenchange", c, r), document.addEventListener("mozfullscreenchange", c, r), document.addEventListener("webkitfullscreenchange", c, r));
    d.P = d.requestFullScreen || d.mozRequestFullScreen || (d.webkitRequestFullScreen ? function() {
        d.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT)
    } : q);
    d.P()
};
Module.requestAnimationFrame = function(a) {
    window.requestAnimationFrame || (window.requestAnimationFrame = window.requestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame || window.msRequestAnimationFrame || window.oRequestAnimationFrame || window.setTimeout);
    window.requestAnimationFrame(a)
};
Module.pauseMainLoop = u();
Module.resumeMainLoop = function() {
    Hb && (Hb = r, q())
};
Module.getUserMedia = function() {
    window.s || (window.s = navigator.getUserMedia || navigator.mozGetUserMedia);
    window.s(k)
};
Ha = w = ra(B);
Ia = Ha + 5242880;
Ja = G = ra(Ia);
A(Ja < qa);
var Vb = Math.min;
var $ = (function(global, env, buffer) {
        // EMSCRIPTEN_START_ASM
        "use asm";
        var a = new global.Int8Array(buffer);
        var b = new global.Int16Array(buffer);
        var c = new global.Int32Array(buffer);
        var d = new global.Uint8Array(buffer);
        var e = new global.Uint16Array(buffer);
        var f = new global.Uint32Array(buffer);
        var g = new global.Float32Array(buffer);
        var h = new global.Float64Array(buffer);
        var i = env.STACKTOP | 0;
        var j = env.STACK_MAX | 0;
        var k = env.tempDoublePtr | 0;
        var l = env.ABORT | 0;
        var m = env._stderr | 0;
        var n = +env.NaN;
        var o = +env.Infinity;
        var p = 0;
        var q = 0;
        var r = 0;
        var s = 0;
        var t = 0,
            u = 0,
            v = 0,
            w = 0,
            x = 0.0,
            y = 0,
            z = 0,
            A = 0,
            B = 0.0;
        var C = 0;
        var D = 0;
        var E = 0;
        var F = 0;
        var G = 0;
        var H = 0;
        var I = 0;
        var J = 0;
        var K = 0;
        var L = 0;
        var M = global.Math.floor;
        var N = global.Math.abs;
        var O = global.Math.sqrt;
        var P = global.Math.pow;
        var Q = global.Math.cos;
        var R = global.Math.sin;
        var S = global.Math.tan;
        var T = global.Math.acos;
        var U = global.Math.asin;
        var V = global.Math.atan;
        var W = global.Math.atan2;
        var X = global.Math.exp;
        var Y = global.Math.log;
        var Z = global.Math.ceil;
        var _ = global.Math.imul;
        var $ = env.abort;
        var aa = env.assert;
        var ab = env.asmPrintInt;
        var ac = env.asmPrintFloat;
        var ad = env.min;
        var ae = env.invoke_viiiii;
        var af = env.invoke_vi;
        var ag = env.invoke_vii;
        var ah = env.invoke_ii;
        var ai = env.invoke_iiii;
        var aj = env.invoke_viii;
        var ak = env.invoke_v;
        var al = env.invoke_iii;
        var am = env.invoke_viiii;
        var an = env._llvm_lifetime_end;
        var ao = env._sysconf;
        var ap = env._abort;
        var aq = env._fprintf;
        var ar = env.__reallyNegative;
        var as = env._fputc;
        var at = env.___setErrNo;
        var au = env._fwrite;
        var av = env._send;
        var aw = env._longjmp;
        var ax = env.__formatString;
        var ay = env.___assert_func;
        var az = env._pwrite;
        var aA = env._putchar;
        var aB = env._sbrk;
        var aC = env.___errno_location;
        var aD = env._llvm_lifetime_start;
        var aE = env._write;
        var aF = env._time;
        // EMSCRIPTEN_START_FUNCS
        function aP(a) {
            a = a | 0;
            var b = 0;
            b = i;
            i = i + a | 0;
            i = i + 7 >> 3 << 3;
            return b | 0
        }

        function aQ() {
            return i | 0
        }

        function aR(a) {
            a = a | 0;
            i = a
        }

        function aS(a, b) {
            a = a | 0;
            b = b | 0;
            if ((p | 0) == 0) {
                p = a;
                q = b
            }
        }

        function aT(b) {
            b = b | 0;
            a[k] = a[b];
            a[k + 1 | 0] = a[b + 1 | 0];
            a[k + 2 | 0] = a[b + 2 | 0];
            a[k + 3 | 0] = a[b + 3 | 0]
        }

        function aU(b) {
            b = b | 0;
            a[k] = a[b];
            a[k + 1 | 0] = a[b + 1 | 0];
            a[k + 2 | 0] = a[b + 2 | 0];
            a[k + 3 | 0] = a[b + 3 | 0];
            a[k + 4 | 0] = a[b + 4 | 0];
            a[k + 5 | 0] = a[b + 5 | 0];
            a[k + 6 | 0] = a[b + 6 | 0];
            a[k + 7 | 0] = a[b + 7 | 0]
        }

        function aV(a) {
            a = a | 0;
            C = a
        }

        function aW(a) {
            a = a | 0;
            D = a
        }

        function aX(a) {
            a = a | 0;
            E = a
        }

        function aY(a) {
            a = a | 0;
            F = a
        }

        function aZ(a) {
            a = a | 0;
            G = a
        }

        function a_(a) {
            a = a | 0;
            H = a
        }

        function a$(a) {
            a = a | 0;
            I = a
        }

        function a0(a) {
            a = a | 0;
            J = a
        }

        function a1(a) {
            a = a | 0;
            K = a
        }

        function a2(a) {
            a = a | 0;
            L = a
        }

        function a3(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0;
            b = b0(64) | 0;
            d = b;
            e = b0(64) | 0;
            f = e;
            g = b0(28) | 0;
            h = g;
            i = (b | 0) == 0;
            j = (e | 0) == 0;
            k = (g | 0) == 0;
            if (i | j | k) {
                if (!i) {
                    b1(b)
                }
                if (!j) {
                    b1(e)
                }
                if (k) {
                    l = 0;
                    return l | 0
                }
                b1(g);
                l = 0;
                return l | 0
            }
            k = a + 92 | 0;
            j = b0(64) | 0;
            if ((j | 0) == 0) {
                b1(b);
                b1(e);
                b1(g);
                l = 0;
                return l | 0
            }
            i = j;
            m = j + 32 | 0;
            n = m;
            o = c[a + 96 >> 2] | 0;
            p = o >>> 0 < k >>> 0 ? o : k;
            k = p + 4 | 0;
            o = c[c[k >> 2] >> 2] | 0;
            c[m >> 2] = o;
            c[c[o + 4 >> 2] >> 2] = i;
            c[j >> 2] = p;
            c[c[k >> 2] >> 2] = n;
            k = j + 4 | 0;
            c[k >> 2] = n;
            c[j + 8 >> 2] = i;
            c[j + 12 >> 2] = n;
            b9(j + 16 | 0, 0, 16);
            c[j + 36 >> 2] = i;
            c[j + 40 >> 2] = n;
            c[j + 44 >> 2] = i;
            b9(j + 48 | 0, 0, 16);
            j = a | 0;
            n = a + 4 | 0;
            p = c[n >> 2] | 0;
            c[b + 4 >> 2] = p;
            c[p >> 2] = d;
            c[b >> 2] = j;
            c[n >> 2] = d;
            c[b + 8 >> 2] = i;
            c[b + 12 >> 2] = 0;
            b = i;
            do {
                c[b + 16 >> 2] = d;
                b = c[b + 8 >> 2] | 0;
            } while ((b | 0) != (i | 0));
            b = c[k >> 2] | 0;
            k = c[n >> 2] | 0;
            c[e + 4 >> 2] = k;
            c[k >> 2] = f;
            c[e >> 2] = j;
            c[n >> 2] = f;
            c[e + 8 >> 2] = b;
            c[e + 12 >> 2] = 0;
            e = b;
            do {
                c[e + 16 >> 2] = f;
                e = c[e + 8 >> 2] | 0;
            } while ((e | 0) != (b | 0));
            b = a + 68 | 0;
            e = c[b >> 2] | 0;
            c[g + 4 >> 2] = e;
            c[e >> 2] = h;
            c[g >> 2] = a + 64;
            c[b >> 2] = h;
            c[g + 8 >> 2] = i;
            c[g + 12 >> 2] = 0;
            c[g + 16 >> 2] = 0;
            c[g + 20 >> 2] = 0;
            c[g + 24 >> 2] = c[a + 88 >> 2];
            a = i;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                c[a + 20 >> 2] = h;
                g = c[a + 12 >> 2] | 0;
                if ((g | 0) == (i | 0)) {
                    l = i;
                    break
                } else {
                    a = g
                }
            }
            return l | 0
        }

        function a4(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0;
            if ((a | 0) == (b | 0)) {
                d = 1;
                return d | 0
            }
            e = c[b + 16 >> 2] | 0;
            f = a + 16 | 0;
            g = c[f >> 2] | 0;
            if ((e | 0) == (g | 0)) {
                h = 0
            } else {
                i = c[e + 8 >> 2] | 0;
                j = i;
                do {
                    c[j + 16 >> 2] = g;
                    j = c[j + 8 >> 2] | 0;
                } while ((j | 0) != (i | 0));
                i = c[e + 4 >> 2] | 0;
                j = c[e >> 2] | 0;
                c[j + 4 >> 2] = i;
                c[i >> 2] = j;
                b1(e);
                h = 1
            }
            e = c[b + 20 >> 2] | 0;
            j = a + 20 | 0;
            i = c[j >> 2] | 0;
            if ((e | 0) == (i | 0)) {
                k = 0
            } else {
                g = c[e + 8 >> 2] | 0;
                l = g;
                do {
                    c[l + 20 >> 2] = i;
                    l = c[l + 12 >> 2] | 0;
                } while ((l | 0) != (g | 0));
                g = c[e + 4 >> 2] | 0;
                l = c[e >> 2] | 0;
                c[l + 4 >> 2] = g;
                c[g >> 2] = l;
                b1(e);
                k = 1
            }
            e = b + 8 | 0;
            l = c[e >> 2] | 0;
            g = a + 8 | 0;
            i = c[g >> 2] | 0;
            c[(c[l + 4 >> 2] | 0) + 12 >> 2] = a;
            c[(c[i + 4 >> 2] | 0) + 12 >> 2] = b;
            c[e >> 2] = i;
            c[g >> 2] = l;
            if ((h | 0) == 0) {
                h = b0(64) | 0;
                l = h;
                if ((h | 0) == 0) {
                    d = 0;
                    return d | 0
                }
                g = c[f >> 2] | 0;
                i = g + 4 | 0;
                e = c[i >> 2] | 0;
                c[h + 4 >> 2] = e;
                c[e >> 2] = l;
                c[h >> 2] = g;
                c[i >> 2] = l;
                c[h + 8 >> 2] = b;
                c[h + 12 >> 2] = 0;
                h = b;
                do {
                    c[h + 16 >> 2] = l;
                    h = c[h + 8 >> 2] | 0;
                } while ((h | 0) != (b | 0));
                c[(c[f >> 2] | 0) + 8 >> 2] = a
            }
            if ((k | 0) != 0) {
                d = 1;
                return d | 0
            }
            k = b0(28) | 0;
            f = k;
            if ((k | 0) == 0) {
                d = 0;
                return d | 0
            }
            h = c[j >> 2] | 0;
            l = h + 4 | 0;
            i = c[l >> 2] | 0;
            c[k + 4 >> 2] = i;
            c[i >> 2] = f;
            c[k >> 2] = h;
            c[l >> 2] = f;
            c[k + 8 >> 2] = b;
            c[k + 12 >> 2] = 0;
            c[k + 16 >> 2] = 0;
            c[k + 20 >> 2] = 0;
            c[k + 24 >> 2] = c[h + 24 >> 2];
            h = b;
            do {
                c[h + 20 >> 2] = f;
                h = c[h + 12 >> 2] | 0;
            } while ((h | 0) != (b | 0));
            c[(c[j >> 2] | 0) + 8 >> 2] = a;
            d = 1;
            return d | 0
        }

        function a5(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0;
            b = a + 4 | 0;
            d = c[b >> 2] | 0;
            e = a + 20 | 0;
            f = c[e >> 2] | 0;
            g = d + 20 | 0;
            h = c[g >> 2] | 0;
            if ((f | 0) == (h | 0)) {
                i = 0
            } else {
                j = c[f + 8 >> 2] | 0;
                k = j;
                do {
                    c[k + 20 >> 2] = h;
                    k = c[k + 12 >> 2] | 0;
                } while ((k | 0) != (j | 0));
                j = c[f + 4 >> 2] | 0;
                k = c[f >> 2] | 0;
                c[k + 4 >> 2] = j;
                c[j >> 2] = k;
                b1(f);
                i = 1
            }
            f = a + 8 | 0;
            do {
                if ((c[f >> 2] | 0) == (a | 0)) {
                    k = c[a + 16 >> 2] | 0;
                    j = c[k + 8 >> 2] | 0;
                    h = j;
                    do {
                        c[h + 16 >> 2] = 0;
                        h = c[h + 8 >> 2] | 0;
                    } while ((h | 0) != (j | 0));
                    j = c[k + 4 >> 2] | 0;
                    h = c[k >> 2] | 0;
                    c[h + 4 >> 2] = j;
                    c[j >> 2] = h;
                    b1(k)
                } else {
                    h = c[b >> 2] | 0;
                    c[(c[h + 20 >> 2] | 0) + 8 >> 2] = c[h + 12 >> 2];
                    c[(c[a + 16 >> 2] | 0) + 8 >> 2] = c[f >> 2];
                    h = c[(c[b >> 2] | 0) + 12 >> 2] | 0;
                    j = c[f >> 2] | 0;
                    l = h + 8 | 0;
                    m = c[l >> 2] | 0;
                    c[(c[j + 4 >> 2] | 0) + 12 >> 2] = h;
                    c[(c[m + 4 >> 2] | 0) + 12 >> 2] = a;
                    c[f >> 2] = m;
                    c[l >> 2] = j;
                    if ((i | 0) != 0) {
                        break
                    }
                    j = b0(28) | 0;
                    l = j;
                    if ((j | 0) == 0) {
                        n = 0;
                        return n | 0
                    }
                    m = c[e >> 2] | 0;
                    h = m + 4 | 0;
                    o = c[h >> 2] | 0;
                    c[j + 4 >> 2] = o;
                    c[o >> 2] = l;
                    c[j >> 2] = m;
                    c[h >> 2] = l;
                    c[j + 8 >> 2] = a;
                    c[j + 12 >> 2] = 0;
                    c[j + 16 >> 2] = 0;
                    c[j + 20 >> 2] = 0;
                    c[j + 24 >> 2] = c[m + 24 >> 2];
                    m = a;
                    do {
                        c[m + 20 >> 2] = l;
                        m = c[m + 12 >> 2] | 0;
                    } while ((m | 0) != (a | 0))
                }
            } while (0);
            i = d + 8 | 0;
            if ((c[i >> 2] | 0) == (d | 0)) {
                f = c[d + 16 >> 2] | 0;
                m = c[f + 8 >> 2] | 0;
                l = m;
                do {
                    c[l + 16 >> 2] = 0;
                    l = c[l + 8 >> 2] | 0;
                } while ((l | 0) != (m | 0));
                m = c[f + 4 >> 2] | 0;
                l = c[f >> 2] | 0;
                c[l + 4 >> 2] = m;
                c[m >> 2] = l;
                b1(f);
                f = c[g >> 2] | 0;
                g = c[f + 8 >> 2] | 0;
                l = g;
                do {
                    c[l + 20 >> 2] = 0;
                    l = c[l + 12 >> 2] | 0;
                } while ((l | 0) != (g | 0));
                g = c[f + 4 >> 2] | 0;
                l = c[f >> 2] | 0;
                c[l + 4 >> 2] = g;
                c[g >> 2] = l;
                b1(f)
            } else {
                f = d + 4 | 0;
                c[(c[e >> 2] | 0) + 8 >> 2] = c[(c[f >> 2] | 0) + 12 >> 2];
                c[(c[d + 16 >> 2] | 0) + 8 >> 2] = c[i >> 2];
                e = c[(c[f >> 2] | 0) + 12 >> 2] | 0;
                f = c[i >> 2] | 0;
                l = e + 8 | 0;
                g = c[l >> 2] | 0;
                c[(c[f + 4 >> 2] | 0) + 12 >> 2] = e;
                c[(c[g + 4 >> 2] | 0) + 12 >> 2] = d;
                c[i >> 2] = g;
                c[l >> 2] = f
            }
            f = c[b >> 2] | 0;
            b = f >>> 0 < a >>> 0 ? f : a;
            a = c[b >> 2] | 0;
            f = c[c[b + 4 >> 2] >> 2] | 0;
            c[c[a + 4 >> 2] >> 2] = f;
            c[c[f + 4 >> 2] >> 2] = a;
            b1(b);
            n = 1;
            return n | 0
        }

        function a6(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0;
            b = b0(64) | 0;
            if ((b | 0) == 0) {
                d = 0;
                return d | 0
            }
            e = b;
            f = b + 32 | 0;
            g = f;
            h = a + 4 | 0;
            i = c[h >> 2] | 0;
            j = i >>> 0 < a >>> 0 ? i : a;
            i = j + 4 | 0;
            k = c[c[i >> 2] >> 2] | 0;
            c[f >> 2] = k;
            c[c[k + 4 >> 2] >> 2] = e;
            c[b >> 2] = j;
            c[c[i >> 2] >> 2] = g;
            c[b + 4 >> 2] = g;
            i = b + 8 | 0;
            c[i >> 2] = e;
            c[b + 12 >> 2] = g;
            j = b + 16 | 0;
            b9(j | 0, 0, 16);
            c[b + 36 >> 2] = e;
            c[b + 40 >> 2] = g;
            c[b + 44 >> 2] = e;
            b9(b + 48 | 0, 0, 16);
            k = c[a + 12 >> 2] | 0;
            f = k + 8 | 0;
            l = c[f >> 2] | 0;
            c[b + 44 >> 2] = k;
            c[(c[l + 4 >> 2] | 0) + 12 >> 2] = e;
            c[i >> 2] = l;
            c[f >> 2] = e;
            f = j;
            c[f >> 2] = c[(c[h >> 2] | 0) + 16 >> 2];
            h = b0(64) | 0;
            j = h;
            if ((h | 0) == 0) {
                d = 0;
                return d | 0
            }
            l = c[f >> 2] | 0;
            f = l + 4 | 0;
            i = c[f >> 2] | 0;
            c[h + 4 >> 2] = i;
            c[i >> 2] = j;
            c[h >> 2] = l;
            c[f >> 2] = j;
            c[h + 8 >> 2] = g;
            c[h + 12 >> 2] = 0;
            h = g;
            do {
                c[h + 16 >> 2] = j;
                h = c[h + 8 >> 2] | 0;
            } while ((h | 0) != (g | 0));
            g = c[a + 20 >> 2] | 0;
            c[b + 52 >> 2] = g;
            c[b + 20 >> 2] = g;
            d = e;
            return d | 0
        }

        function a7(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0;
            d = b0(64) | 0;
            if ((d | 0) == 0) {
                e = 0;
                return e | 0
            }
            f = d;
            g = d + 32 | 0;
            h = g;
            i = a + 4 | 0;
            j = c[i >> 2] | 0;
            k = j >>> 0 < a >>> 0 ? j : a;
            j = k + 4 | 0;
            l = c[c[j >> 2] >> 2] | 0;
            c[g >> 2] = l;
            c[c[l + 4 >> 2] >> 2] = f;
            c[d >> 2] = k;
            c[c[j >> 2] >> 2] = h;
            c[d + 4 >> 2] = h;
            j = d + 8 | 0;
            c[j >> 2] = f;
            c[d + 12 >> 2] = h;
            k = d + 16 | 0;
            b9(k | 0, 0, 16);
            c[d + 36 >> 2] = f;
            c[d + 40 >> 2] = h;
            c[d + 44 >> 2] = f;
            b9(d + 48 | 0, 0, 16);
            l = c[b + 20 >> 2] | 0;
            g = a + 20 | 0;
            m = c[g >> 2] | 0;
            if ((l | 0) == (m | 0)) {
                n = 0;
                o = f
            } else {
                p = c[l + 8 >> 2] | 0;
                q = p;
                do {
                    c[q + 20 >> 2] = m;
                    q = c[q + 12 >> 2] | 0;
                } while ((q | 0) != (p | 0));
                p = c[l + 4 >> 2] | 0;
                q = c[l >> 2] | 0;
                c[q + 4 >> 2] = p;
                c[p >> 2] = q;
                b1(l);
                n = 1;
                o = c[j >> 2] | 0
            }
            l = c[a + 12 >> 2] | 0;
            a = l + 8 | 0;
            q = c[a >> 2] | 0;
            c[(c[o + 4 >> 2] | 0) + 12 >> 2] = l;
            c[(c[q + 4 >> 2] | 0) + 12 >> 2] = f;
            c[j >> 2] = q;
            c[a >> 2] = o;
            o = d + 40 | 0;
            a = c[o >> 2] | 0;
            q = b + 8 | 0;
            j = c[q >> 2] | 0;
            c[(c[a + 4 >> 2] | 0) + 12 >> 2] = b;
            c[(c[j + 4 >> 2] | 0) + 12 >> 2] = h;
            c[o >> 2] = j;
            c[q >> 2] = a;
            c[k >> 2] = c[(c[i >> 2] | 0) + 16 >> 2];
            c[d + 48 >> 2] = c[b + 16 >> 2];
            b = c[g >> 2] | 0;
            c[d + 52 >> 2] = b;
            c[d + 20 >> 2] = b;
            c[(c[g >> 2] | 0) + 8 >> 2] = h;
            if (n) {
                e = f;
                return e | 0
            }
            n = b0(28) | 0;
            h = n;
            if ((n | 0) == 0) {
                e = 0;
                return e | 0
            }
            b = c[g >> 2] | 0;
            g = b + 4 | 0;
            d = c[g >> 2] | 0;
            c[n + 4 >> 2] = d;
            c[d >> 2] = h;
            c[n >> 2] = b;
            c[g >> 2] = h;
            c[n + 8 >> 2] = f;
            c[n + 12 >> 2] = 0;
            c[n + 16 >> 2] = 0;
            c[n + 20 >> 2] = 0;
            c[n + 24 >> 2] = c[b + 24 >> 2];
            b = f;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                c[b + 20 >> 2] = h;
                n = c[b + 12 >> 2] | 0;
                if ((n | 0) == (f | 0)) {
                    e = f;
                    break
                } else {
                    b = n
                }
            }
            return e | 0
        }

        function a8(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0;
            b = c[a + 8 >> 2] | 0;
            d = c[b + 12 >> 2] | 0;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                e = c[d + 12 >> 2] | 0;
                c[d + 20 >> 2] = 0;
                f = d + 4 | 0;
                if ((c[(c[f >> 2] | 0) + 20 >> 2] | 0) == 0) {
                    g = d + 8 | 0;
                    h = c[g >> 2] | 0;
                    i = c[d + 16 >> 2] | 0;
                    j = i + 8 | 0;
                    if ((h | 0) == (d | 0)) {
                        k = c[j >> 2] | 0;
                        l = k;
                        do {
                            c[l + 16 >> 2] = 0;
                            l = c[l + 8 >> 2] | 0;
                        } while ((l | 0) != (k | 0));
                        k = c[i + 4 >> 2] | 0;
                        l = c[i >> 2] | 0;
                        c[l + 4 >> 2] = k;
                        c[k >> 2] = l;
                        b1(i)
                    } else {
                        c[j >> 2] = h;
                        l = c[(c[f >> 2] | 0) + 12 >> 2] | 0;
                        k = c[g >> 2] | 0;
                        m = l + 8 | 0;
                        n = c[m >> 2] | 0;
                        c[(c[k + 4 >> 2] | 0) + 12 >> 2] = l;
                        c[(c[n + 4 >> 2] | 0) + 12 >> 2] = d;
                        c[g >> 2] = n;
                        c[m >> 2] = k
                    }
                    k = c[f >> 2] | 0;
                    m = k + 8 | 0;
                    n = c[m >> 2] | 0;
                    l = c[k + 16 >> 2] | 0;
                    o = l + 8 | 0;
                    if ((n | 0) == (k | 0)) {
                        p = c[o >> 2] | 0;
                        q = p;
                        do {
                            c[q + 16 >> 2] = 0;
                            q = c[q + 8 >> 2] | 0;
                        } while ((q | 0) != (p | 0));
                        p = c[l + 4 >> 2] | 0;
                        q = c[l >> 2] | 0;
                        c[q + 4 >> 2] = p;
                        c[p >> 2] = q;
                        b1(l)
                    } else {
                        c[o >> 2] = n;
                        q = c[(c[k + 4 >> 2] | 0) + 12 >> 2] | 0;
                        p = c[m >> 2] | 0;
                        g = q + 8 | 0;
                        h = c[g >> 2] | 0;
                        c[(c[p + 4 >> 2] | 0) + 12 >> 2] = q;
                        c[(c[h + 4 >> 2] | 0) + 12 >> 2] = k;
                        c[m >> 2] = h;
                        c[g >> 2] = p
                    }
                    p = c[f >> 2] | 0;
                    g = p >>> 0 < d >>> 0 ? p : d;
                    p = c[g >> 2] | 0;
                    h = c[c[g + 4 >> 2] >> 2] | 0;
                    c[c[p + 4 >> 2] >> 2] = h;
                    c[c[h + 4 >> 2] >> 2] = p;
                    b1(g)
                }
                if ((d | 0) == (b | 0)) {
                    break
                } else {
                    d = e
                }
            }
            d = c[a + 4 >> 2] | 0;
            b = c[a >> 2] | 0;
            c[b + 4 >> 2] = d;
            c[d >> 2] = b;
            b1(a);
            return
        }

        function a9(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0;
            b = a + 64 | 0;
            d = a | 0;
            e = a + 92 | 0;
            f = c[b >> 2] | 0;
            g = (c[f + 4 >> 2] | 0) == (b | 0);
            L145: do {
                if ((f | 0) == (b | 0)) {
                    h = g
                } else {
                    i = f;
                    j = g;
                    L146: {
                        var tessellationLimit3 = 0;
                        while (1) {
                            if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            if (!j) {
                                k = 110;
                                break
                            }
                            l = c[i + 8 >> 2] | 0;
                            m = l;
                            var tessellationLimit4 = 0;
                            while (1) {
                                if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                n = c[m + 4 >> 2] | 0;
                                if ((n | 0) == (m | 0)) {
                                    k = 113;
                                    break L146
                                }
                                if ((c[n + 4 >> 2] | 0) != (m | 0)) {
                                    k = 115;
                                    break L146
                                }
                                n = c[m + 12 >> 2] | 0;
                                if ((c[(c[n + 8 >> 2] | 0) + 4 >> 2] | 0) != (m | 0)) {
                                    k = 117;
                                    break L146
                                }
                                if ((c[(c[(c[m + 8 >> 2] | 0) + 4 >> 2] | 0) + 12 >> 2] | 0) != (m | 0)) {
                                    k = 119;
                                    break L146
                                }
                                if ((c[m + 20 >> 2] | 0) != (i | 0)) {
                                    k = 121;
                                    break L146
                                }
                                if ((n | 0) == (l | 0)) {
                                    break
                                } else {
                                    m = n
                                }
                            }
                            m = c[i >> 2] | 0;
                            l = (c[m + 4 >> 2] | 0) == (i | 0);
                            if ((m | 0) == (b | 0)) {
                                h = l;
                                break L145
                            } else {
                                i = m;
                                j = l
                            }
                        }
                    }
                    if ((k | 0) == 121) {
                        ay(1736, 760, 2440, 448)
                    } else if ((k | 0) == 115) {
                        ay(1736, 757, 2440, 712)
                    } else if ((k | 0) == 117) {
                        ay(1736, 758, 2440, 616)
                    } else if ((k | 0) == 119) {
                        ay(1736, 759, 2440, 520)
                    } else if ((k | 0) == 110) {
                        ay(1736, 753, 2440, 1712)
                    } else if ((k | 0) == 113) {
                        ay(1736, 756, 2440, 888)
                    }
                }
            } while (0);
            if (!h) {
                ay(1736, 764, 2440, 328)
            }
            if ((c[a + 72 >> 2] | 0) != 0) {
                ay(1736, 764, 2440, 328)
            }
            if ((c[a + 76 >> 2] | 0) != 0) {
                ay(1736, 764, 2440, 328)
            }
            h = c[a >> 2] | 0;
            b = (c[h + 4 >> 2] | 0) == (d | 0);
            L174: do {
                if ((h | 0) == (d | 0)) {
                    o = b
                } else {
                    g = h;
                    f = b;
                    L175: {
                        var tessellationLimit3 = 0;
                        while (1) {
                            if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            if (!f) {
                                k = 130;
                                break
                            }
                            j = c[g + 8 >> 2] | 0;
                            i = j;
                            l = c[j + 4 >> 2] | 0;
                            var tessellationLimit4 = 0;
                            while (1) {
                                if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                if ((l | 0) == (i | 0)) {
                                    k = 133;
                                    break L175
                                }
                                if ((c[l + 4 >> 2] | 0) != (i | 0)) {
                                    k = 135;
                                    break L175
                                }
                                if ((c[(c[(c[i + 12 >> 2] | 0) + 8 >> 2] | 0) + 4 >> 2] | 0) != (i | 0)) {
                                    k = 137;
                                    break L175
                                }
                                m = c[i + 8 >> 2] | 0;
                                n = c[m + 4 >> 2] | 0;
                                if ((c[n + 12 >> 2] | 0) != (i | 0)) {
                                    k = 139;
                                    break L175
                                }
                                if ((c[i + 16 >> 2] | 0) != (g | 0)) {
                                    k = 141;
                                    break L175
                                }
                                if ((m | 0) == (j | 0)) {
                                    break
                                } else {
                                    i = m;
                                    l = n
                                }
                            }
                            l = c[g >> 2] | 0;
                            i = (c[l + 4 >> 2] | 0) == (g | 0);
                            if ((l | 0) == (d | 0)) {
                                o = i;
                                break L174
                            } else {
                                g = l;
                                f = i
                            }
                        }
                    }
                    if ((k | 0) == 130) {
                        ay(1736, 768, 2440, 152)
                    } else if ((k | 0) == 133) {
                        ay(1736, 771, 2440, 888)
                    } else if ((k | 0) == 135) {
                        ay(1736, 772, 2440, 712)
                    } else if ((k | 0) == 137) {
                        ay(1736, 773, 2440, 616)
                    } else if ((k | 0) == 139) {
                        ay(1736, 774, 2440, 520)
                    } else if ((k | 0) == 141) {
                        ay(1736, 775, 2440, 80)
                    }
                }
            } while (0);
            if (!o) {
                ay(1736, 779, 2440, 1648)
            }
            if ((c[a + 8 >> 2] | 0) != 0) {
                ay(1736, 779, 2440, 1648)
            }
            if ((c[a + 12 >> 2] | 0) != 0) {
                ay(1736, 779, 2440, 1648)
            }
            o = e;
            d = c[a + 96 >> 2] | 0;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                b = c[o >> 2] | 0;
                p = c[b + 4 >> 2] | 0;
                q = (c[p >> 2] | 0) == (d | 0);
                if ((b | 0) == (e | 0)) {
                    k = 163;
                    break
                }
                if (!q) {
                    k = 150;
                    break
                }
                if ((p | 0) == (b | 0)) {
                    k = 152;
                    break
                }
                if ((c[p + 4 >> 2] | 0) != (b | 0)) {
                    k = 154;
                    break
                }
                if ((c[b + 16 >> 2] | 0) == 0) {
                    k = 156;
                    break
                }
                if ((c[p + 16 >> 2] | 0) == 0) {
                    k = 158;
                    break
                }
                if ((c[(c[(c[b + 12 >> 2] | 0) + 8 >> 2] | 0) + 4 >> 2] | 0) != (b | 0)) {
                    k = 160;
                    break
                }
                if ((c[(c[(c[b + 8 >> 2] | 0) + 4 >> 2] | 0) + 12 >> 2] | 0) == (b | 0)) {
                    o = b;
                    d = p
                } else {
                    k = 162;
                    break
                }
            }
            if ((k | 0) == 150) {
                ay(1736, 783, 2440, 1568)
            } else if ((k | 0) == 152) {
                ay(1736, 784, 2440, 888)
            } else if ((k | 0) == 154) {
                ay(1736, 785, 2440, 712)
            } else if ((k | 0) == 156) {
                ay(1736, 786, 2440, 1488)
            } else if ((k | 0) == 158) {
                ay(1736, 787, 2440, 1424)
            } else if ((k | 0) == 160) {
                ay(1736, 788, 2440, 616)
            } else if ((k | 0) == 162) {
                ay(1736, 789, 2440, 520)
            } else if ((k | 0) == 163) {
                if (!q) {
                    ay(1736, 795, 2440, 1192)
                }
                if ((p | 0) != (a + 124 | 0)) {
                    ay(1736, 795, 2440, 1192)
                }
                if ((c[p + 4 >> 2] | 0) != (e | 0)) {
                    ay(1736, 795, 2440, 1192)
                }
                if ((c[a + 108 >> 2] | 0) != 0) {
                    ay(1736, 795, 2440, 1192)
                }
                if ((c[p + 16 >> 2] | 0) != 0) {
                    ay(1736, 795, 2440, 1192)
                }
                if ((c[a + 112 >> 2] | 0) != 0) {
                    ay(1736, 795, 2440, 1192)
                }
                if ((c[p + 20 >> 2] | 0) == 0) {
                    return
                } else {
                    ay(1736, 795, 2440, 1192)
                }
            }
        }

        function ba(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0.0,
                i = 0.0,
                j = 0.0,
                k = 0,
                l = 0.0,
                m = 0.0,
                n = 0.0,
                o = 0.0,
                p = 0.0,
                q = 0.0,
                r = 0,
                s = 0.0,
                t = 0.0,
                u = 0.0,
                v = 0.0,
                w = 0.0,
                x = 0.0,
                y = 0.0,
                z = 0.0,
                A = 0.0,
                B = 0.0,
                C = 0.0,
                D = 0.0,
                E = 0,
                F = 0.0,
                G = 0.0,
                H = 0.0,
                I = 0.0,
                J = 0.0,
                K = 0.0,
                L = 0,
                M = 0,
                N = 0,
                O = 0,
                P = 0.0,
                Q = 0.0,
                R = 0.0,
                S = 0.0,
                T = 0,
                U = 0,
                V = 0,
                W = 0,
                X = 0;
            b = a + 160 | 0;
            d = c[a + 156 >> 2] | 0;
            e = a + 160 + (d << 5) | 0;
            if ((d | 0) < 3) {
                f = 1;
                return f | 0
            }
            g = +h[a + 16 >> 3];
            i = +h[a + 24 >> 3];
            j = +h[a + 32 >> 3];
            do {
                if (g == 0.0) {
                    if (i == 0.0 & j == 0.0) {
                        k = a + 192 | 0;
                        if ((d | 0) <= 2) {
                            break
                        }
                        l = +h[a + 176 >> 3];
                        m = +h[a + 168 >> 3];
                        n = +h[a + 160 >> 3];
                        o = +h[a + 208 >> 3] - l;
                        p = +h[a + 200 >> 3] - m;
                        q = +h[k >> 3] - n;
                        r = k;
                        k = a + 224 | 0;
                        s = 0.0;
                        t = 0.0;
                        u = 0.0;
                        var tessellationLimit4 = 0;
                        while (1) {
                            if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            v = +h[k >> 3] - n;
                            w = +h[r + 40 >> 3] - m;
                            x = +h[r + 48 >> 3] - l;
                            y = p * x - o * w;
                            z = o * v - q * x;
                            A = q * w - p * v;
                            if (u * A + (s * y + t * z) < 0.0) {
                                B = s - y;
                                C = u - A;
                                D = t - z
                            } else {
                                B = s + y;
                                C = u + A;
                                D = t + z
                            }
                            E = k + 32 | 0;
                            if (E >>> 0 < e >>> 0) {
                                o = x;
                                p = w;
                                q = v;
                                r = k;
                                k = E;
                                s = B;
                                t = D;
                                u = C
                            } else {
                                F = B;
                                G = D;
                                H = C;
                                break
                            }
                        }
                    } else {
                        F = g;
                        G = i;
                        H = j
                    }
                    if ((d | 0) > 2) {
                        I = H;
                        J = G;
                        K = F;
                        L = 196
                    }
                } else {
                    I = j;
                    J = i;
                    K = g;
                    L = 196
                }
            } while (0);
            do {
                if ((L | 0) == 196) {
                    k = a + 192 | 0;
                    g = +h[a + 176 >> 3];
                    i = +h[a + 168 >> 3];
                    j = +h[a + 160 >> 3];
                    F = +h[a + 208 >> 3] - g;
                    G = +h[a + 200 >> 3] - i;
                    H = +h[k >> 3] - j;
                    r = k;
                    E = 0;
                    M = a + 224 | 0;
                    L261: {
                        var tessellationLimit3 = 0;
                        while (1) {
                            if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            C = F;
                            D = G;
                            B = H;
                            N = r;
                            O = M;
                            var tessellationLimit6 = 0;
                            while (1) {
                                if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                P = +h[O >> 3] - j;
                                Q = +h[N + 40 >> 3] - i;
                                R = +h[N + 48 >> 3] - g;
                                S = I * (B * Q - D * P) + (K * (D * R - C * Q) + J * (C * P - B * R));
                                if (S != 0.0) {
                                    break
                                }
                                T = O + 32 | 0;
                                if (T >>> 0 < e >>> 0) {
                                    C = R;
                                    D = Q;
                                    B = P;
                                    N = O;
                                    O = T
                                } else {
                                    U = E;
                                    L = 204;
                                    break L261
                                }
                            }
                            if (S > 0.0) {
                                if ((E | 0) < 0) {
                                    f = 0;
                                    L = 242;
                                    break
                                } else {
                                    V = 1
                                }
                            } else {
                                if ((E | 0) > 0) {
                                    f = 0;
                                    L = 239;
                                    break
                                } else {
                                    V = -1
                                }
                            }
                            N = O + 32 | 0;
                            if (N >>> 0 < e >>> 0) {
                                F = R;
                                G = Q;
                                H = P;
                                r = O;
                                E = V;
                                M = N
                            } else {
                                U = V;
                                L = 204;
                                break
                            }
                        }
                    }
                    if ((L | 0) == 204) {
                        if ((U | 0) == 0) {
                            break
                        } else if ((U | 0) == 2) {
                            f = 0;
                            return f | 0
                        }
                        M = c[a + 96 >> 2] | 0;
                        do {
                            if ((M | 0) == 100132) {
                                if ((U | 0) < 0) {
                                    f = 1
                                } else {
                                    break
                                }
                                return f | 0
                            } else if ((M | 0) == 100133) {
                                if ((U | 0) > 0) {
                                    f = 1
                                } else {
                                    break
                                }
                                return f | 0
                            } else if ((M | 0) == 100134) {
                                f = 1;
                                return f | 0
                            }
                        } while (0);
                        M = c[a + 3360 >> 2] | 0;
                        if ((M | 0) == 26) {
                            if ((c[a + 124 >> 2] | 0) == 0) {
                                W = (d | 0) > 3 ? 6 : 4
                            } else {
                                W = 2
                            }
                            aH[c[a + 132 >> 2] & 31](W)
                        } else {
                            if ((c[a + 124 >> 2] | 0) == 0) {
                                X = (d | 0) > 3 ? 6 : 4
                            } else {
                                X = 2
                            }
                            aI[M & 31](X, c[a + 3424 >> 2] | 0)
                        }
                        M = a + 3368 | 0;
                        E = c[M >> 2] | 0;
                        if ((E | 0) == 6) {
                            aH[c[a + 140 >> 2] & 31](c[a + 184 >> 2] | 0)
                        } else {
                            aI[E & 31](c[a + 184 >> 2] | 0, c[a + 3424 >> 2] | 0)
                        }
                        do {
                            if ((U | 0) > 0) {
                                if ((d | 0) <= 1) {
                                    break
                                }
                                E = a + 140 | 0;
                                r = a + 3424 | 0;
                                N = k;
                                var tessellationLimit8 = 0;
                                do {
                                    if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    T = c[M >> 2] | 0;
                                    if ((T | 0) == 6) {
                                        aH[c[E >> 2] & 31](c[N + 24 >> 2] | 0)
                                    } else {
                                        aI[T & 31](c[N + 24 >> 2] | 0, c[r >> 2] | 0)
                                    }
                                    N = N + 32 | 0;
                                } while (N >>> 0 < e >>> 0)
                            } else {
                                N = d - 1 | 0;
                                if ((N | 0) <= 0) {
                                    break
                                }
                                r = a + 140 | 0;
                                E = a + 3424 | 0;
                                O = a + 160 + (N << 5) | 0;
                                var tessellationLimit8 = 0;
                                do {
                                    if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    N = c[M >> 2] | 0;
                                    if ((N | 0) == 6) {
                                        aH[c[r >> 2] & 31](c[O + 24 >> 2] | 0)
                                    } else {
                                        aI[N & 31](c[O + 24 >> 2] | 0, c[E >> 2] | 0)
                                    }
                                    O = O - 32 | 0;
                                } while (O >>> 0 > b >>> 0)
                            }
                        } while (0);
                        M = c[a + 3372 >> 2] | 0;
                        if ((M | 0) == 4) {
                            aM[c[a + 144 >> 2] & 3]();
                            f = 1;
                            return f | 0
                        } else {
                            aH[M & 31](c[a + 3424 >> 2] | 0);
                            f = 1;
                            return f | 0
                        }
                    } else if ((L | 0) == 239) {
                        return f | 0
                    } else if ((L | 0) == 242) {
                        return f | 0
                    }
                }
            } while (0);
            f = 1;
            return f | 0
        }

        function bb(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0,
                P = 0,
                Q = 0,
                R = 0,
                S = 0,
                T = 0,
                U = 0,
                V = 0,
                W = 0,
                X = 0,
                Y = 0,
                Z = 0,
                _ = 0,
                $ = 0,
                aa = 0,
                ab = 0,
                ac = 0,
                ad = 0,
                ae = 0,
                af = 0,
                ag = 0,
                ah = 0,
                ai = 0,
                aj = 0,
                ak = 0,
                al = 0,
                am = 0,
                an = 0,
                ao = 0,
                ap = 0;
            d = i;
            i = i + 48 | 0;
            e = d | 0;
            f = d + 16 | 0;
            g = d + 32 | 0;
            h = a + 128 | 0;
            c[h >> 2] = 0;
            j = b + 64 | 0;
            b = j | 0;
            k = c[b >> 2] | 0;
            if ((k | 0) == (j | 0)) {
                i = d;
                return
            } else {
                l = k
            }
            var tessellationLimit3 = 0;
            do {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                c[l + 20 >> 2] = 0;
                l = c[l >> 2] | 0;
            } while ((l | 0) != (j | 0));
            l = c[b >> 2] | 0;
            L330: do {
                if ((l | 0) != (j | 0)) {
                    b = a + 120 | 0;
                    k = e | 0;
                    m = e + 8 | 0;
                    n = e + 4 | 0;
                    o = f | 0;
                    p = f + 8 | 0;
                    q = f + 4 | 0;
                    r = g | 0;
                    s = g + 8 | 0;
                    t = g + 4 | 0;
                    u = l;
                    L332: {
                        var tessellationLimit3 = 0;
                        while (1) {
                            if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            do {
                                if ((c[u + 24 >> 2] | 0) != 0) {
                                    v = u + 20 | 0;
                                    if ((c[v >> 2] | 0) != 0) {
                                        break
                                    }
                                    w = c[u + 8 >> 2] | 0;
                                    do {
                                        if ((c[b >> 2] | 0) == 0) {
                                            x = w + 20 | 0;
                                            y = c[x >> 2] | 0;
                                            L339: do {
                                                if ((c[y + 24 >> 2] | 0) == 0) {
                                                    z = 0;
                                                    A = 0
                                                } else {
                                                    B = 0;
                                                    C = 0;
                                                    D = w;
                                                    E = x;
                                                    F = y;
                                                    var tessellationLimit12 = 0;
                                                    while (1) {
                                                        if (++tessellationLimit12 > TESSELLATION_LIMIT) {
                                                            throw new Error("Tessellate limit exceeded");
                                                        }
                                                        if ((c[F + 20 >> 2] | 0) != 0) {
                                                            z = B;
                                                            A = C;
                                                            break L339
                                                        }
                                                        c[F + 16 >> 2] = C;
                                                        G = c[E >> 2] | 0;
                                                        c[G + 20 >> 2] = 1;
                                                        H = B + 1 | 0;
                                                        I = c[D + 8 >> 2] | 0;
                                                        J = I + 20 | 0;
                                                        K = c[J >> 2] | 0;
                                                        if ((c[K + 24 >> 2] | 0) == 0) {
                                                            z = H;
                                                            A = G;
                                                            break
                                                        } else {
                                                            B = H;
                                                            C = G;
                                                            D = I;
                                                            E = J;
                                                            F = K
                                                        }
                                                    }
                                                }
                                            } while (0);
                                            y = w + 4 | 0;
                                            x = c[(c[y >> 2] | 0) + 20 >> 2] | 0;
                                            L344: do {
                                                if ((c[x + 24 >> 2] | 0) == 0) {
                                                    L = z;
                                                    M = A;
                                                    N = w
                                                } else {
                                                    F = z;
                                                    E = A;
                                                    D = w;
                                                    C = y;
                                                    B = x;
                                                    var tessellationLimit12 = 0;
                                                    while (1) {
                                                        if (++tessellationLimit12 > TESSELLATION_LIMIT) {
                                                            throw new Error("Tessellate limit exceeded");
                                                        }
                                                        if ((c[B + 20 >> 2] | 0) != 0) {
                                                            L = F;
                                                            M = E;
                                                            N = D;
                                                            break L344
                                                        }
                                                        c[B + 16 >> 2] = E;
                                                        K = c[(c[C >> 2] | 0) + 20 >> 2] | 0;
                                                        c[K + 20 >> 2] = 1;
                                                        J = F + 1 | 0;
                                                        I = c[(c[C >> 2] | 0) + 12 >> 2] | 0;
                                                        G = I + 4 | 0;
                                                        H = c[(c[G >> 2] | 0) + 20 >> 2] | 0;
                                                        if ((c[H + 24 >> 2] | 0) == 0) {
                                                            L = J;
                                                            M = K;
                                                            N = I;
                                                            break
                                                        } else {
                                                            F = J;
                                                            E = K;
                                                            D = I;
                                                            C = G;
                                                            B = H
                                                        }
                                                    }
                                                }
                                            } while (0);
                                            if ((M | 0) != 0) {
                                                x = M;
                                                var tessellationLimit15 = 0;
                                                do {
                                                    if (++tessellationLimit15 > TESSELLATION_LIMIT) {
                                                        throw new Error("Tessellate limit exceeded");
                                                    }
                                                    c[x + 20 >> 2] = 0;
                                                    x = c[x + 16 >> 2] | 0;
                                                } while ((x | 0) != 0)
                                            }
                                            x = (L | 0) > 1;
                                            y = x ? 4 : 2;
                                            B = x ? N : w;
                                            C = x ? L : 1;
                                            x = w + 12 | 0;
                                            D = c[x >> 2] | 0;
                                            E = D + 20 | 0;
                                            F = c[E >> 2] | 0;
                                            L353: do {
                                                if ((c[F + 24 >> 2] | 0) == 0) {
                                                    O = 0;
                                                    P = 0
                                                } else {
                                                    H = 0;
                                                    G = 0;
                                                    I = D;
                                                    K = E;
                                                    J = F;
                                                    var tessellationLimit12 = 0;
                                                    while (1) {
                                                        if (++tessellationLimit12 > TESSELLATION_LIMIT) {
                                                            throw new Error("Tessellate limit exceeded");
                                                        }
                                                        if ((c[J + 20 >> 2] | 0) != 0) {
                                                            O = H;
                                                            P = G;
                                                            break L353
                                                        }
                                                        c[J + 16 >> 2] = G;
                                                        Q = c[K >> 2] | 0;
                                                        c[Q + 20 >> 2] = 1;
                                                        R = H + 1 | 0;
                                                        S = c[I + 8 >> 2] | 0;
                                                        T = S + 20 | 0;
                                                        U = c[T >> 2] | 0;
                                                        if ((c[U + 24 >> 2] | 0) == 0) {
                                                            O = R;
                                                            P = Q;
                                                            break
                                                        } else {
                                                            H = R;
                                                            G = Q;
                                                            I = S;
                                                            K = T;
                                                            J = U
                                                        }
                                                    }
                                                }
                                            } while (0);
                                            F = D + 4 | 0;
                                            E = c[(c[F >> 2] | 0) + 20 >> 2] | 0;
                                            L358: do {
                                                if ((c[E + 24 >> 2] | 0) == 0) {
                                                    V = O;
                                                    W = P;
                                                    X = D
                                                } else {
                                                    J = O;
                                                    K = P;
                                                    I = D;
                                                    G = F;
                                                    H = E;
                                                    var tessellationLimit12 = 0;
                                                    while (1) {
                                                        if (++tessellationLimit12 > TESSELLATION_LIMIT) {
                                                            throw new Error("Tessellate limit exceeded");
                                                        }
                                                        if ((c[H + 20 >> 2] | 0) != 0) {
                                                            V = J;
                                                            W = K;
                                                            X = I;
                                                            break L358
                                                        }
                                                        c[H + 16 >> 2] = K;
                                                        U = c[(c[G >> 2] | 0) + 20 >> 2] | 0;
                                                        c[U + 20 >> 2] = 1;
                                                        T = J + 1 | 0;
                                                        S = c[(c[G >> 2] | 0) + 12 >> 2] | 0;
                                                        Q = S + 4 | 0;
                                                        R = c[(c[Q >> 2] | 0) + 20 >> 2] | 0;
                                                        if ((c[R + 24 >> 2] | 0) == 0) {
                                                            V = T;
                                                            W = U;
                                                            X = S;
                                                            break
                                                        } else {
                                                            J = T;
                                                            K = U;
                                                            I = S;
                                                            G = Q;
                                                            H = R
                                                        }
                                                    }
                                                }
                                            } while (0);
                                            if ((W | 0) != 0) {
                                                E = W;
                                                var tessellationLimit15 = 0;
                                                do {
                                                    if (++tessellationLimit15 > TESSELLATION_LIMIT) {
                                                        throw new Error("Tessellate limit exceeded");
                                                    }
                                                    c[E + 20 >> 2] = 0;
                                                    E = c[E + 16 >> 2] | 0;
                                                } while ((E | 0) != 0)
                                            }
                                            E = (V | 0) > (C | 0);
                                            F = E ? 4 : y;
                                            D = E ? X : B;
                                            H = E ? V : C;
                                            E = w + 8 | 0;
                                            G = c[(c[E >> 2] | 0) + 4 >> 2] | 0;
                                            I = G + 20 | 0;
                                            K = c[I >> 2] | 0;
                                            L367: do {
                                                if ((c[K + 24 >> 2] | 0) == 0) {
                                                    Y = 0;
                                                    Z = 0
                                                } else {
                                                    J = 0;
                                                    R = 0;
                                                    Q = G;
                                                    S = I;
                                                    U = K;
                                                    var tessellationLimit12 = 0;
                                                    while (1) {
                                                        if (++tessellationLimit12 > TESSELLATION_LIMIT) {
                                                            throw new Error("Tessellate limit exceeded");
                                                        }
                                                        if ((c[U + 20 >> 2] | 0) != 0) {
                                                            Y = J;
                                                            Z = R;
                                                            break L367
                                                        }
                                                        c[U + 16 >> 2] = R;
                                                        T = c[S >> 2] | 0;
                                                        c[T + 20 >> 2] = 1;
                                                        _ = J + 1 | 0;
                                                        $ = c[Q + 8 >> 2] | 0;
                                                        aa = $ + 20 | 0;
                                                        ab = c[aa >> 2] | 0;
                                                        if ((c[ab + 24 >> 2] | 0) == 0) {
                                                            Y = _;
                                                            Z = T;
                                                            break
                                                        } else {
                                                            J = _;
                                                            R = T;
                                                            Q = $;
                                                            S = aa;
                                                            U = ab
                                                        }
                                                    }
                                                }
                                            } while (0);
                                            K = G + 4 | 0;
                                            I = c[(c[K >> 2] | 0) + 20 >> 2] | 0;
                                            L372: do {
                                                if ((c[I + 24 >> 2] | 0) == 0) {
                                                    ac = Y;
                                                    ad = Z;
                                                    ae = G
                                                } else {
                                                    C = Y;
                                                    B = Z;
                                                    y = G;
                                                    U = K;
                                                    S = I;
                                                    var tessellationLimit12 = 0;
                                                    while (1) {
                                                        if (++tessellationLimit12 > TESSELLATION_LIMIT) {
                                                            throw new Error("Tessellate limit exceeded");
                                                        }
                                                        if ((c[S + 20 >> 2] | 0) != 0) {
                                                            ac = C;
                                                            ad = B;
                                                            ae = y;
                                                            break L372
                                                        }
                                                        c[S + 16 >> 2] = B;
                                                        Q = c[(c[U >> 2] | 0) + 20 >> 2] | 0;
                                                        c[Q + 20 >> 2] = 1;
                                                        R = C + 1 | 0;
                                                        J = c[(c[U >> 2] | 0) + 12 >> 2] | 0;
                                                        ab = J + 4 | 0;
                                                        aa = c[(c[ab >> 2] | 0) + 20 >> 2] | 0;
                                                        if ((c[aa + 24 >> 2] | 0) == 0) {
                                                            ac = R;
                                                            ad = Q;
                                                            ae = J;
                                                            break
                                                        } else {
                                                            C = R;
                                                            B = Q;
                                                            y = J;
                                                            U = ab;
                                                            S = aa
                                                        }
                                                    }
                                                }
                                            } while (0);
                                            if ((ad | 0) != 0) {
                                                I = ad;
                                                var tessellationLimit15 = 0;
                                                do {
                                                    if (++tessellationLimit15 > TESSELLATION_LIMIT) {
                                                        throw new Error("Tessellate limit exceeded");
                                                    }
                                                    c[I + 20 >> 2] = 0;
                                                    I = c[I + 16 >> 2] | 0;
                                                } while ((I | 0) != 0)
                                            }
                                            I = (ac | 0) > (H | 0);
                                            K = I ? ac : H;
                                            bq(e, w);
                                            G = c[k >> 2] | 0;
                                            if ((G | 0) > (K | 0)) {
                                                af = G;
                                                ag = c[n >> 2] | 0;
                                                ah = c[m >> 2] | 0
                                            } else {
                                                af = K;
                                                ag = I ? ae : D;
                                                ah = I ? 4 : F
                                            }
                                            bq(f, c[x >> 2] | 0);
                                            I = c[o >> 2] | 0;
                                            if ((I | 0) > (af | 0)) {
                                                ai = I;
                                                aj = c[q >> 2] | 0;
                                                ak = c[p >> 2] | 0
                                            } else {
                                                ai = af;
                                                aj = ag;
                                                ak = ah
                                            }
                                            bq(g, c[(c[E >> 2] | 0) + 4 >> 2] | 0);
                                            I = c[r >> 2] | 0;
                                            if ((I | 0) <= (ai | 0)) {
                                                al = ai;
                                                am = aj;
                                                an = ak;
                                                break
                                            }
                                            al = I;
                                            am = c[t >> 2] | 0;
                                            an = c[s >> 2] | 0
                                        } else {
                                            al = 1;
                                            am = w;
                                            an = 2
                                        }
                                    } while (0);
                                    aL[an & 7](a, am, al);
                                    if ((c[v >> 2] | 0) == 0) {
                                        break L332
                                    }
                                }
                            } while (0);
                            u = c[u >> 2] | 0;
                            if ((u | 0) == (j | 0)) {
                                break L330
                            }
                        }
                    }
                    ay(1016, 100, 2248, 1504)
                }
            } while (0);
            j = c[h >> 2] | 0;
            if ((j | 0) == 0) {
                i = d;
                return
            }
            al = c[a + 3360 >> 2] | 0;
            if ((al | 0) == 26) {
                aH[c[a + 132 >> 2] & 31](4);
                ao = a + 3424 | 0
            } else {
                am = a + 3424 | 0;
                aI[al & 31](4, c[am >> 2] | 0);
                ao = am
            }
            am = a + 120 | 0;
            al = a + 3368 | 0;
            an = a + 140 | 0;
            ak = a + 3364 | 0;
            aj = a + 136 | 0;
            ai = j;
            j = -1;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                g = ai + 8 | 0;
                ah = j;
                ag = c[g >> 2] | 0;
                var tessellationLimit4 = 0;
                while (1) {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    do {
                        if ((c[am >> 2] | 0) == 0) {
                            ap = ah
                        } else {
                            af = (c[(c[(c[ag + 4 >> 2] | 0) + 20 >> 2] | 0) + 24 >> 2] | 0) == 0 & 1;
                            if ((ah | 0) == (af | 0)) {
                                ap = ah;
                                break
                            }
                            f = c[ak >> 2] | 0;
                            if ((f | 0) == 20) {
                                aH[c[aj >> 2] & 31](af);
                                ap = af;
                                break
                            } else {
                                aI[f & 31](af, c[ao >> 2] | 0);
                                ap = af;
                                break
                            }
                        }
                    } while (0);
                    af = c[al >> 2] | 0;
                    if ((af | 0) == 6) {
                        aH[c[an >> 2] & 31](c[(c[ag + 16 >> 2] | 0) + 12 >> 2] | 0)
                    } else {
                        aI[af & 31](c[(c[ag + 16 >> 2] | 0) + 12 >> 2] | 0, c[ao >> 2] | 0)
                    }
                    af = c[ag + 12 >> 2] | 0;
                    if ((af | 0) == (c[g >> 2] | 0)) {
                        break
                    } else {
                        ah = ap;
                        ag = af
                    }
                }
                ag = c[ai + 16 >> 2] | 0;
                if ((ag | 0) == 0) {
                    break
                } else {
                    ai = ag;
                    j = ap
                }
            }
            ap = c[a + 3372 >> 2] | 0;
            if ((ap | 0) == 4) {
                aM[c[a + 144 >> 2] & 3]()
            } else {
                aH[ap & 31](c[ao >> 2] | 0)
            }
            c[h >> 2] = 0;
            i = d;
            return
        }

        function bc(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            if ((d | 0) == 1) {
                d = a + 128 | 0;
                a = b + 20 | 0;
                c[(c[a >> 2] | 0) + 16 >> 2] = c[d >> 2];
                c[d >> 2] = c[a >> 2];
                c[(c[a >> 2] | 0) + 20 >> 2] = 1;
                return
            } else {
                ay(1016, 243, 2496, 696)
            }
        }

        function bd(a, b) {
            a = a | 0;
            b = b | 0;
            return
        }

        function be(a, b) {
            a = a | 0;
            b = b | 0;
            return
        }

        function bf(a, b) {
            a = a | 0;
            b = b | 0;
            return
        }

        function bg(a) {
            a = a | 0;
            return
        }

        function bh(a, b) {
            a = a | 0;
            b = b | 0;
            return
        }

        function bi(a, b, c, d, e) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            e = e | 0;
            return
        }

        function bj(a) {
            a = a | 0;
            return
        }

        function bk(a) {
            a = a | 0;
            return
        }

        function bl(a) {
            a = a | 0;
            return
        }

        function bm() {
            return
        }

        function bn(a) {
            a = a | 0;
            return
        }

        function bo(a, b, c, d) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            return
        }

        function bp(a) {
            a = a | 0;
            return
        }

        function bq(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0;
            d = b + 20 | 0;
            e = c[d >> 2] | 0;
            L439: do {
                if ((c[e + 24 >> 2] | 0) == 0) {
                    f = b;
                    g = 0;
                    h = 0
                } else {
                    i = 0;
                    j = 0;
                    k = b;
                    l = d;
                    m = e;
                    var tessellationLimit4 = 0;
                    while (1) {
                        if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        if ((c[m + 20 >> 2] | 0) != 0) {
                            f = k;
                            g = j;
                            h = i;
                            break L439
                        }
                        c[m + 16 >> 2] = j;
                        n = c[l >> 2] | 0;
                        c[n + 20 >> 2] = 1;
                        o = i | 1;
                        p = c[(c[k + 12 >> 2] | 0) + 4 >> 2] | 0;
                        q = p + 20 | 0;
                        r = c[q >> 2] | 0;
                        if ((c[r + 24 >> 2] | 0) == 0) {
                            f = p;
                            g = n;
                            h = o;
                            break L439
                        }
                        if ((c[r + 20 >> 2] | 0) != 0) {
                            f = p;
                            g = n;
                            h = o;
                            break L439
                        }
                        c[r + 16 >> 2] = n;
                        n = c[q >> 2] | 0;
                        c[n + 20 >> 2] = 1;
                        q = i + 2 | 0;
                        r = c[p + 8 >> 2] | 0;
                        p = r + 20 | 0;
                        o = c[p >> 2] | 0;
                        if ((c[o + 24 >> 2] | 0) == 0) {
                            f = r;
                            g = n;
                            h = q;
                            break
                        } else {
                            i = q;
                            j = n;
                            k = r;
                            l = p;
                            m = o
                        }
                    }
                }
            } while (0);
            e = b + 4 | 0;
            d = c[(c[e >> 2] | 0) + 20 >> 2] | 0;
            L446: do {
                if ((c[d + 24 >> 2] | 0) == 0) {
                    s = b;
                    t = g;
                    u = 0
                } else {
                    m = 0;
                    l = g;
                    k = b;
                    j = e;
                    i = d;
                    var tessellationLimit4 = 0;
                    while (1) {
                        if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        if ((c[i + 20 >> 2] | 0) != 0) {
                            s = k;
                            t = l;
                            u = m;
                            break L446
                        }
                        c[i + 16 >> 2] = l;
                        o = c[(c[j >> 2] | 0) + 20 >> 2] | 0;
                        c[o + 20 >> 2] = 1;
                        p = m | 1;
                        r = c[(c[j >> 2] | 0) + 12 >> 2] | 0;
                        n = r + 4 | 0;
                        q = c[(c[n >> 2] | 0) + 20 >> 2] | 0;
                        if ((c[q + 24 >> 2] | 0) == 0) {
                            s = r;
                            t = o;
                            u = p;
                            break L446
                        }
                        if ((c[q + 20 >> 2] | 0) != 0) {
                            s = r;
                            t = o;
                            u = p;
                            break L446
                        }
                        c[q + 16 >> 2] = o;
                        o = c[(c[n >> 2] | 0) + 20 >> 2] | 0;
                        c[o + 20 >> 2] = 1;
                        q = m + 2 | 0;
                        p = c[(c[(c[n >> 2] | 0) + 8 >> 2] | 0) + 4 >> 2] | 0;
                        n = p + 4 | 0;
                        r = c[(c[n >> 2] | 0) + 20 >> 2] | 0;
                        if ((c[r + 24 >> 2] | 0) == 0) {
                            s = p;
                            t = o;
                            u = q;
                            break
                        } else {
                            m = q;
                            l = o;
                            k = p;
                            j = n;
                            i = r
                        }
                    }
                }
            } while (0);
            d = u + h | 0;
            do {
                if ((h & 1 | 0) == 0) {
                    v = c[f + 4 >> 2] | 0;
                    w = d
                } else {
                    if ((u & 1 | 0) == 0) {
                        v = s;
                        w = d;
                        break
                    }
                    v = c[s + 8 >> 2] | 0;
                    w = d - 1 | 0
                }
            } while (0);
            if ((t | 0) == 0) {
                x = a | 0;
                c[x >> 2] = w;
                y = a + 4 | 0;
                c[y >> 2] = v;
                z = a + 8 | 0;
                c[z >> 2] = 6;
                return
            } else {
                A = t
            }
            var tessellationLimit3 = 0;
            do {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                c[A + 20 >> 2] = 0;
                A = c[A + 16 >> 2] | 0;
            } while ((A | 0) != 0);
            x = a | 0;
            c[x >> 2] = w;
            y = a + 4 | 0;
            c[y >> 2] = v;
            z = a + 8 | 0;
            c[z >> 2] = 6;
            return
        }

        function br(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0;
            d = a | 0;
            e = c[d >> 2] | 0;
            if ((e | 0) == (b | 0)) {
                return
            }
            f = a + 3376 | 0;
            g = a + 12 | 0;
            h = a + 156 | 0;
            i = a + 152 | 0;
            j = a + 8 | 0;
            k = a + 3424 | 0;
            l = a + 4 | 0;
            m = e;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                do {
                    if (m >>> 0 < b >>> 0) {
                        if ((m | 0) == 0) {
                            e = c[f >> 2] | 0;
                            if ((e | 0) == 16) {
                                aH[c[g >> 2] & 31](100151)
                            } else {
                                aI[e & 31](100151, c[k >> 2] | 0)
                            }
                            if ((c[d >> 2] | 0) != 0) {
                                br(a, 0)
                            }
                            c[d >> 2] = 1;
                            c[h >> 2] = 0;
                            c[i >> 2] = 0;
                            c[j >> 2] = 0;
                            c[k >> 2] = 0;
                            n = 1;
                            break
                        } else if ((m | 0) != 1) {
                            n = m;
                            break
                        }
                        e = c[f >> 2] | 0;
                        if ((e | 0) == 16) {
                            aH[c[g >> 2] & 31](100152)
                        } else {
                            aI[e & 31](100152, c[k >> 2] | 0)
                        }
                        if ((c[d >> 2] | 0) != 1) {
                            br(a, 1)
                        }
                        c[d >> 2] = 2;
                        c[l >> 2] = 0;
                        if ((c[h >> 2] | 0) <= 0) {
                            n = 2;
                            break
                        }
                        c[i >> 2] = 1;
                        n = 2
                    } else {
                        if ((m | 0) == 2) {
                            e = c[f >> 2] | 0;
                            if ((e | 0) == 16) {
                                aH[c[g >> 2] & 31](100154)
                            } else {
                                aI[e & 31](100154, c[k >> 2] | 0)
                            }
                            if ((c[d >> 2] | 0) != 2) {
                                br(a, 2)
                            }
                            c[d >> 2] = 1;
                            n = 1;
                            break
                        } else if ((m | 0) != 1) {
                            n = m;
                            break
                        }
                        e = c[f >> 2] | 0;
                        if ((e | 0) == 16) {
                            aH[c[g >> 2] & 31](100153)
                        } else {
                            aI[e & 31](100153, c[k >> 2] | 0)
                        }
                        e = c[j >> 2] | 0;
                        if ((e | 0) != 0) {
                            o = e + 64 | 0;
                            p = c[o >> 2] | 0;
                            if ((p | 0) != (o | 0)) {
                                q = p;
                                var tessellationLimit9 = 0;
                                while (1) {
                                    if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    p = c[q >> 2] | 0;
                                    b1(q);
                                    if ((p | 0) == (o | 0)) {
                                        break
                                    } else {
                                        q = p
                                    }
                                }
                            }
                            q = e | 0;
                            o = c[e >> 2] | 0;
                            if ((o | 0) != (q | 0)) {
                                p = o;
                                var tessellationLimit9 = 0;
                                while (1) {
                                    if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    o = c[p >> 2] | 0;
                                    b1(p);
                                    if ((o | 0) == (q | 0)) {
                                        break
                                    } else {
                                        p = o
                                    }
                                }
                            }
                            p = e + 92 | 0;
                            q = c[p >> 2] | 0;
                            if ((q | 0) != (p | 0)) {
                                o = q;
                                var tessellationLimit9 = 0;
                                while (1) {
                                    if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    q = c[o >> 2] | 0;
                                    b1(o);
                                    if ((q | 0) == (p | 0)) {
                                        break
                                    } else {
                                        o = q
                                    }
                                }
                            }
                            b1(e)
                        }
                        c[d >> 2] = 0;
                        c[l >> 2] = 0;
                        c[j >> 2] = 0;
                        n = 0
                    }
                } while (0);
                if ((n | 0) == (b | 0)) {
                    break
                } else {
                    m = n
                }
            }
            return
        }

        function bs(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            var e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0;
            e = c[a + 3360 >> 2] | 0;
            if ((e | 0) == 26) {
                aH[c[a + 132 >> 2] & 31](5)
            } else {
                aI[e & 31](5, c[a + 3424 >> 2] | 0)
            }
            e = a + 3368 | 0;
            f = c[e >> 2] | 0;
            if ((f | 0) == 6) {
                aH[c[a + 140 >> 2] & 31](c[(c[b + 16 >> 2] | 0) + 12 >> 2] | 0)
            } else {
                aI[f & 31](c[(c[b + 16 >> 2] | 0) + 12 >> 2] | 0, c[a + 3424 >> 2] | 0)
            }
            f = c[e >> 2] | 0;
            if ((f | 0) == 6) {
                aH[c[a + 140 >> 2] & 31](c[(c[(c[b + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0)
            } else {
                aI[f & 31](c[(c[(c[b + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0, c[a + 3424 >> 2] | 0)
            }
            f = c[b + 20 >> 2] | 0;
            L534: do {
                if ((c[f + 24 >> 2] | 0) == 0) {
                    g = d
                } else {
                    h = a + 140 | 0;
                    i = a + 3424 | 0;
                    j = b;
                    k = d;
                    l = f;
                    var tessellationLimit5 = 0;
                    while (1) {
                        if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        m = l + 20 | 0;
                        if ((c[m >> 2] | 0) != 0) {
                            g = k;
                            break L534
                        }
                        c[m >> 2] = 1;
                        m = k - 1 | 0;
                        n = c[(c[j + 12 >> 2] | 0) + 4 >> 2] | 0;
                        o = c[e >> 2] | 0;
                        if ((o | 0) == 6) {
                            aH[c[h >> 2] & 31](c[(c[n + 16 >> 2] | 0) + 12 >> 2] | 0)
                        } else {
                            aI[o & 31](c[(c[n + 16 >> 2] | 0) + 12 >> 2] | 0, c[i >> 2] | 0)
                        }
                        o = c[n + 20 >> 2] | 0;
                        if ((c[o + 24 >> 2] | 0) == 0) {
                            g = m;
                            break L534
                        }
                        p = o + 20 | 0;
                        if ((c[p >> 2] | 0) != 0) {
                            g = m;
                            break L534
                        }
                        c[p >> 2] = 1;
                        p = k - 2 | 0;
                        m = c[n + 8 >> 2] | 0;
                        n = c[e >> 2] | 0;
                        if ((n | 0) == 6) {
                            aH[c[h >> 2] & 31](c[(c[(c[m + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0)
                        } else {
                            aI[n & 31](c[(c[(c[m + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0, c[i >> 2] | 0)
                        }
                        n = c[m + 20 >> 2] | 0;
                        if ((c[n + 24 >> 2] | 0) == 0) {
                            g = p;
                            break
                        } else {
                            j = m;
                            k = p;
                            l = n
                        }
                    }
                }
            } while (0);
            if ((g | 0) != 0) {
                ay(1016, 328, 2512, 872)
            }
            g = c[a + 3372 >> 2] | 0;
            if ((g | 0) == 4) {
                aM[c[a + 144 >> 2] & 3]();
                return
            } else {
                aH[g & 31](c[a + 3424 >> 2] | 0);
                return
            }
        }

        function bt(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            var e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0;
            e = c[a + 3360 >> 2] | 0;
            if ((e | 0) == 26) {
                aH[c[a + 132 >> 2] & 31](6)
            } else {
                aI[e & 31](6, c[a + 3424 >> 2] | 0)
            }
            e = a + 3368 | 0;
            f = c[e >> 2] | 0;
            if ((f | 0) == 6) {
                aH[c[a + 140 >> 2] & 31](c[(c[b + 16 >> 2] | 0) + 12 >> 2] | 0)
            } else {
                aI[f & 31](c[(c[b + 16 >> 2] | 0) + 12 >> 2] | 0, c[a + 3424 >> 2] | 0)
            }
            f = c[e >> 2] | 0;
            if ((f | 0) == 6) {
                aH[c[a + 140 >> 2] & 31](c[(c[(c[b + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0)
            } else {
                aI[f & 31](c[(c[(c[b + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0, c[a + 3424 >> 2] | 0)
            }
            f = c[b + 20 >> 2] | 0;
            L571: do {
                if ((c[f + 24 >> 2] | 0) == 0) {
                    g = d
                } else {
                    h = a + 140 | 0;
                    i = a + 3424 | 0;
                    j = b;
                    k = d;
                    l = f;
                    var tessellationLimit5 = 0;
                    while (1) {
                        if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        m = l + 20 | 0;
                        if ((c[m >> 2] | 0) != 0) {
                            g = k;
                            break L571
                        }
                        c[m >> 2] = 1;
                        m = k - 1 | 0;
                        n = c[j + 8 >> 2] | 0;
                        o = c[e >> 2] | 0;
                        if ((o | 0) == 6) {
                            aH[c[h >> 2] & 31](c[(c[(c[n + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0)
                        } else {
                            aI[o & 31](c[(c[(c[n + 4 >> 2] | 0) + 16 >> 2] | 0) + 12 >> 2] | 0, c[i >> 2] | 0)
                        }
                        o = c[n + 20 >> 2] | 0;
                        if ((c[o + 24 >> 2] | 0) == 0) {
                            g = m;
                            break
                        } else {
                            j = n;
                            k = m;
                            l = o
                        }
                    }
                }
            } while (0);
            if ((g | 0) != 0) {
                ay(1016, 300, 2528, 872)
            }
            g = c[a + 3372 >> 2] | 0;
            if ((g | 0) == 4) {
                aM[c[a + 144 >> 2] & 3]();
                return
            } else {
                aH[g & 31](c[a + 3424 >> 2] | 0);
                return
            }
        }

        function bu(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            var e = 0,
                f = 0,
                g = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0.0,
                A = 0.0,
                B = 0.0,
                C = 0.0,
                D = 0,
                E = 0,
                F = 0;
            if ((c[a >> 2] | 0) != 2) {
                br(a, 2)
            }
            e = a + 152 | 0;
            L593: do {
                if ((c[e >> 2] | 0) != 0) {
                    f = b0(160) | 0;
                    L595: do {
                        if ((f | 0) == 0) {
                            c[a + 8 >> 2] = 0
                        } else {
                            g = f;
                            i = f + 64 | 0;
                            j = i;
                            k = f + 92 | 0;
                            l = k;
                            m = f + 124 | 0;
                            n = m;
                            c[f + 4 >> 2] = g;
                            c[f >> 2] = g;
                            c[f + 8 >> 2] = 0;
                            c[f + 12 >> 2] = 0;
                            c[f + 68 >> 2] = j;
                            c[i >> 2] = j;
                            b9(f + 72 | 0, 0, 20);
                            c[k >> 2] = l;
                            c[f + 96 >> 2] = n;
                            b9(f + 100 | 0, 0, 24);
                            c[m >> 2] = n;
                            c[f + 128 >> 2] = l;
                            b9(f + 132 | 0, 0, 24);
                            l = a + 8 | 0;
                            c[l >> 2] = f;
                            n = a + 156 | 0;
                            m = c[n >> 2] | 0;
                            k = a + 160 + (m << 5) | 0;
                            if ((m | 0) > 0) {
                                m = a + 4 | 0;
                                j = a + 160 | 0;
                                i = c[m >> 2] | 0;
                                var tessellationLimit8 = 0;
                                while (1) {
                                    if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    g = j | 0;
                                    o = c[j + 24 >> 2] | 0;
                                    if ((i | 0) == 0) {
                                        p = a3(c[l >> 2] | 0) | 0;
                                        if ((p | 0) == 0) {
                                            break L595
                                        }
                                        if ((a4(p, c[p + 4 >> 2] | 0) | 0) == 0) {
                                            break L595
                                        } else {
                                            q = p
                                        }
                                    } else {
                                        p = a6(i) | 0;
                                        if ((p | 0) == 0) {
                                            break L595
                                        }
                                        r = c[p + 4 >> 2] | 0;
                                        p = i + 4 | 0;
                                        s = c[p >> 2] | 0;
                                        t = c[(c[s + 4 >> 2] | 0) + 12 >> 2] | 0;
                                        u = s + 8 | 0;
                                        v = c[u >> 2] | 0;
                                        w = t + 8 | 0;
                                        x = c[w >> 2] | 0;
                                        c[(c[v + 4 >> 2] | 0) + 12 >> 2] = t;
                                        c[(c[x + 4 >> 2] | 0) + 12 >> 2] = s;
                                        c[u >> 2] = x;
                                        c[w >> 2] = v;
                                        v = c[p >> 2] | 0;
                                        w = v + 8 | 0;
                                        x = c[w >> 2] | 0;
                                        u = r + 8 | 0;
                                        s = c[u >> 2] | 0;
                                        c[(c[x + 4 >> 2] | 0) + 12 >> 2] = r;
                                        c[(c[s + 4 >> 2] | 0) + 12 >> 2] = v;
                                        c[w >> 2] = s;
                                        c[u >> 2] = x;
                                        c[(c[p >> 2] | 0) + 16 >> 2] = c[r + 16 >> 2];
                                        x = r + 4 | 0;
                                        u = c[x >> 2] | 0;
                                        c[(c[u + 16 >> 2] | 0) + 8 >> 2] = u;
                                        c[(c[x >> 2] | 0) + 20 >> 2] = c[(c[p >> 2] | 0) + 20 >> 2];
                                        c[r + 28 >> 2] = c[i + 28 >> 2];
                                        c[(c[x >> 2] | 0) + 28 >> 2] = c[(c[p >> 2] | 0) + 28 >> 2];
                                        if ((r | 0) == 0) {
                                            break L595
                                        }
                                        q = c[i + 12 >> 2] | 0
                                    }
                                    r = q + 16 | 0;
                                    c[(c[r >> 2] | 0) + 12 >> 2] = o;
                                    h[(c[r >> 2] | 0) + 16 >> 3] = +h[g >> 3];
                                    h[(c[r >> 2] | 0) + 24 >> 3] = +h[j + 8 >> 3];
                                    h[(c[r >> 2] | 0) + 32 >> 3] = +h[j + 16 >> 3];
                                    c[q + 28 >> 2] = 1;
                                    c[(c[q + 4 >> 2] | 0) + 28 >> 2] = -1;
                                    c[m >> 2] = q;
                                    r = j + 32 | 0;
                                    if (r >>> 0 < k >>> 0) {
                                        j = r;
                                        i = q
                                    } else {
                                        y = m;
                                        break
                                    }
                                }
                            } else {
                                y = a + 4 | 0
                            }
                            c[n >> 2] = 0;
                            c[e >> 2] = 0;
                            c[y >> 2] = 0;
                            break L593
                        }
                    } while (0);
                    f = c[a + 3376 >> 2] | 0;
                    if ((f | 0) == 16) {
                        aH[c[a + 12 >> 2] & 31](100902);
                        return
                    } else {
                        aI[f & 31](100902, c[a + 3424 >> 2] | 0);
                        return
                    }
                }
            } while (0);
            z = +h[b >> 3];
            y = z < -1.0e+150;
            A = y ? -1.0e+150 : z;
            q = A > 1.0e+150;
            z = q ? 1.0e+150 : A;
            A = +h[b + 8 >> 3];
            f = A < -1.0e+150;
            B = f ? -1.0e+150 : A;
            m = B > 1.0e+150;
            A = m ? 1.0e+150 : B;
            B = +h[b + 16 >> 3];
            b = B < -1.0e+150;
            C = b ? -1.0e+150 : B;
            i = C > 1.0e+150;
            B = i ? 1.0e+150 : C;
            do {
                if (y | q | f | m | b | i) {
                    j = c[a + 3376 >> 2] | 0;
                    if ((j | 0) == 16) {
                        aH[c[a + 12 >> 2] & 31](100155);
                        break
                    } else {
                        aI[j & 31](100155, c[a + 3424 >> 2] | 0);
                        break
                    }
                }
            } while (0);
            i = a + 8 | 0;
            L624: do {
                if ((c[i >> 2] | 0) == 0) {
                    b = a + 156 | 0;
                    m = c[b >> 2] | 0;
                    if ((m | 0) < 100) {
                        c[a + 160 + (m << 5) + 24 >> 2] = d;
                        h[a + 160 + (m << 5) >> 3] = z;
                        h[a + 160 + (m << 5) + 8 >> 3] = A;
                        h[a + 160 + (m << 5) + 16 >> 3] = B;
                        c[b >> 2] = m + 1;
                        return
                    }
                    m = b0(160) | 0;
                    L630: do {
                        if ((m | 0) == 0) {
                            c[i >> 2] = 0
                        } else {
                            f = m;
                            q = m + 64 | 0;
                            y = q;
                            j = m + 92 | 0;
                            k = j;
                            l = m + 124 | 0;
                            r = l;
                            c[m + 4 >> 2] = f;
                            c[m >> 2] = f;
                            c[m + 8 >> 2] = 0;
                            c[m + 12 >> 2] = 0;
                            c[m + 68 >> 2] = y;
                            c[q >> 2] = y;
                            b9(m + 72 | 0, 0, 20);
                            c[j >> 2] = k;
                            c[m + 96 >> 2] = r;
                            b9(m + 100 | 0, 0, 24);
                            c[l >> 2] = r;
                            c[m + 128 >> 2] = k;
                            b9(m + 132 | 0, 0, 24);
                            c[i >> 2] = m;
                            k = c[b >> 2] | 0;
                            r = a + 160 + (k << 5) | 0;
                            if ((k | 0) > 0) {
                                k = a + 4 | 0;
                                l = a + 160 | 0;
                                j = c[k >> 2] | 0;
                                var tessellationLimit8 = 0;
                                while (1) {
                                    if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    y = l | 0;
                                    q = c[l + 24 >> 2] | 0;
                                    if ((j | 0) == 0) {
                                        f = a3(c[i >> 2] | 0) | 0;
                                        if ((f | 0) == 0) {
                                            break L630
                                        }
                                        if ((a4(f, c[f + 4 >> 2] | 0) | 0) == 0) {
                                            break L630
                                        } else {
                                            D = f
                                        }
                                    } else {
                                        f = a6(j) | 0;
                                        if ((f | 0) == 0) {
                                            break L630
                                        }
                                        g = c[f + 4 >> 2] | 0;
                                        f = j + 4 | 0;
                                        o = c[f >> 2] | 0;
                                        p = c[(c[o + 4 >> 2] | 0) + 12 >> 2] | 0;
                                        x = o + 8 | 0;
                                        u = c[x >> 2] | 0;
                                        s = p + 8 | 0;
                                        w = c[s >> 2] | 0;
                                        c[(c[u + 4 >> 2] | 0) + 12 >> 2] = p;
                                        c[(c[w + 4 >> 2] | 0) + 12 >> 2] = o;
                                        c[x >> 2] = w;
                                        c[s >> 2] = u;
                                        u = c[f >> 2] | 0;
                                        s = u + 8 | 0;
                                        w = c[s >> 2] | 0;
                                        x = g + 8 | 0;
                                        o = c[x >> 2] | 0;
                                        c[(c[w + 4 >> 2] | 0) + 12 >> 2] = g;
                                        c[(c[o + 4 >> 2] | 0) + 12 >> 2] = u;
                                        c[s >> 2] = o;
                                        c[x >> 2] = w;
                                        c[(c[f >> 2] | 0) + 16 >> 2] = c[g + 16 >> 2];
                                        w = g + 4 | 0;
                                        x = c[w >> 2] | 0;
                                        c[(c[x + 16 >> 2] | 0) + 8 >> 2] = x;
                                        c[(c[w >> 2] | 0) + 20 >> 2] = c[(c[f >> 2] | 0) + 20 >> 2];
                                        c[g + 28 >> 2] = c[j + 28 >> 2];
                                        c[(c[w >> 2] | 0) + 28 >> 2] = c[(c[f >> 2] | 0) + 28 >> 2];
                                        if ((g | 0) == 0) {
                                            break L630
                                        }
                                        D = c[j + 12 >> 2] | 0
                                    }
                                    g = D + 16 | 0;
                                    c[(c[g >> 2] | 0) + 12 >> 2] = q;
                                    h[(c[g >> 2] | 0) + 16 >> 3] = +h[y >> 3];
                                    h[(c[g >> 2] | 0) + 24 >> 3] = +h[l + 8 >> 3];
                                    h[(c[g >> 2] | 0) + 32 >> 3] = +h[l + 16 >> 3];
                                    c[D + 28 >> 2] = 1;
                                    c[(c[D + 4 >> 2] | 0) + 28 >> 2] = -1;
                                    c[k >> 2] = D;
                                    g = l + 32 | 0;
                                    if (g >>> 0 < r >>> 0) {
                                        l = g;
                                        j = D
                                    } else {
                                        break
                                    }
                                }
                            }
                            c[b >> 2] = 0;
                            c[e >> 2] = 0;
                            break L624
                        }
                    } while (0);
                    b = c[a + 3376 >> 2] | 0;
                    if ((b | 0) == 16) {
                        aH[c[a + 12 >> 2] & 31](100902);
                        return
                    } else {
                        aI[b & 31](100902, c[a + 3424 >> 2] | 0);
                        return
                    }
                }
            } while (0);
            e = a + 4 | 0;
            D = c[e >> 2] | 0;
            do {
                if ((D | 0) == 0) {
                    b = a3(c[i >> 2] | 0) | 0;
                    if ((b | 0) == 0) {
                        break
                    }
                    if ((a4(b, c[b + 4 >> 2] | 0) | 0) != 0) {
                        E = b;
                        F = 485
                    }
                } else {
                    b = a6(D) | 0;
                    if ((b | 0) == 0) {
                        break
                    }
                    m = c[b + 4 >> 2] | 0;
                    b = D + 4 | 0;
                    j = c[b >> 2] | 0;
                    l = c[(c[j + 4 >> 2] | 0) + 12 >> 2] | 0;
                    r = j + 8 | 0;
                    k = c[r >> 2] | 0;
                    n = l + 8 | 0;
                    g = c[n >> 2] | 0;
                    c[(c[k + 4 >> 2] | 0) + 12 >> 2] = l;
                    c[(c[g + 4 >> 2] | 0) + 12 >> 2] = j;
                    c[r >> 2] = g;
                    c[n >> 2] = k;
                    k = c[b >> 2] | 0;
                    n = k + 8 | 0;
                    g = c[n >> 2] | 0;
                    r = m + 8 | 0;
                    j = c[r >> 2] | 0;
                    c[(c[g + 4 >> 2] | 0) + 12 >> 2] = m;
                    c[(c[j + 4 >> 2] | 0) + 12 >> 2] = k;
                    c[n >> 2] = j;
                    c[r >> 2] = g;
                    c[(c[b >> 2] | 0) + 16 >> 2] = c[m + 16 >> 2];
                    g = m + 4 | 0;
                    r = c[g >> 2] | 0;
                    c[(c[r + 16 >> 2] | 0) + 8 >> 2] = r;
                    c[(c[g >> 2] | 0) + 20 >> 2] = c[(c[b >> 2] | 0) + 20 >> 2];
                    c[m + 28 >> 2] = c[D + 28 >> 2];
                    c[(c[g >> 2] | 0) + 28 >> 2] = c[(c[b >> 2] | 0) + 28 >> 2];
                    if ((m | 0) == 0) {
                        break
                    }
                    E = c[D + 12 >> 2] | 0;
                    F = 485
                }
            } while (0);
            if ((F | 0) == 485) {
                F = E + 16 | 0;
                c[(c[F >> 2] | 0) + 12 >> 2] = d;
                h[(c[F >> 2] | 0) + 16 >> 3] = z;
                h[(c[F >> 2] | 0) + 24 >> 3] = A;
                h[(c[F >> 2] | 0) + 32 >> 3] = B;
                c[E + 28 >> 2] = 1;
                c[(c[E + 4 >> 2] | 0) + 28 >> 2] = -1;
                c[e >> 2] = E;
                return
            }
            E = c[a + 3376 >> 2] | 0;
            if ((E | 0) == 16) {
                aH[c[a + 12 >> 2] & 31](100902);
                return
            } else {
                aI[E & 31](100902, c[a + 3424 >> 2] | 0);
                return
            }
        }

        function bv(a, b) {
            a = a | 0;
            b = b | 0;
            var c = 0.0,
                d = 0.0,
                e = 0;
            c = +h[a + 40 >> 3];
            d = +h[b + 40 >> 3];
            if (c < d) {
                e = 1;
                return e | 0
            }
            if (c != d) {
                e = 0;
                return e | 0
            }
            e = +h[a + 48 >> 3] <= +h[b + 48 >> 3] & 1;
            return e | 0
        }

        function bw(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0,
                P = 0,
                Q = 0,
                R = 0,
                S = 0,
                T = 0,
                U = 0,
                V = 0,
                W = 0,
                X = 0,
                Y = 0,
                Z = 0,
                _ = 0,
                $ = 0,
                aa = 0,
                ab = 0,
                ac = 0,
                ad = 0,
                ae = 0,
                ai = 0,
                aj = 0,
                am = 0,
                an = 0,
                ao = 0,
                ap = 0,
                aq = 0,
                ar = 0,
                as = 0;
            b = 1;
            d = 0;
            e = i;
            i = i + 168 | 0;
            c[e >> 2] = 0;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                switch (b | 0) {
                    case 1:
                        f = a + 3384 | 0;
                        g = b7(f | 0, b, e) | 0;
                        b = 79;
                        break;
                    case 79:
                        if ((g | 0) == 0) {
                            b = 5;
                            break
                        } else {
                            b = 2;
                            break
                        };
                    case 2:
                        j = c[a + 3376 >> 2] | 0;
                        if ((j | 0) == 16) {
                            b = 4;
                            break
                        } else {
                            b = 3;
                            break
                        };
                    case 3:
                        ag(j | 0, 100902, c[a + 3424 >> 2] | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 78;
                        break;
                    case 4:
                        af(c[a + 12 >> 2] | 0, 100902);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 78;
                        break;
                    case 5:
                        k = a | 0;
                        if ((c[k >> 2] | 0) == 1) {
                            b = 7;
                            break
                        } else {
                            b = 6;
                            break
                        };
                    case 6:
                        ag(24, a | 0, 1);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 7;
                        break;
                    case 7:
                        c[k >> 2] = 0;
                        l = a + 8 | 0;
                        if ((c[l >> 2] | 0) == 0) {
                            b = 8;
                            break
                        } else {
                            b = 25;
                            break
                        };
                    case 8:
                        if ((c[a + 120 >> 2] | 0) == 0) {
                            b = 9;
                            break
                        } else {
                            b = 12;
                            break
                        };
                    case 9:
                        if ((c[a + 148 >> 2] | 0) == 8) {
                            b = 10;
                            break
                        } else {
                            b = 12;
                            break
                        };
                    case 10:
                        m = ah(2, a | 0) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((m | 0) == 0) {
                            b = 12;
                            break
                        } else {
                            b = 11;
                            break
                        };
                    case 11:
                        c[a + 3424 >> 2] = 0;
                        b = 78;
                        break;
                    case 12:
                        n = ah(14, 160) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((n | 0) == 0) {
                            b = 13;
                            break
                        } else {
                            b = 14;
                            break
                        };
                    case 13:
                        c[l >> 2] = 0;
                        b = 24;
                        break;
                    case 14:
                        m = n;
                        o = n + 64 | 0;
                        r = o;
                        s = n + 92 | 0;
                        t = s;
                        u = n + 124 | 0;
                        v = u;
                        c[n + 4 >> 2] = m;
                        c[n >> 2] = m;
                        c[n + 8 >> 2] = 0;
                        c[n + 12 >> 2] = 0;
                        c[n + 68 >> 2] = r;
                        c[o >> 2] = r;
                        b9(n + 72 | 0, 0, 20);
                        c[s >> 2] = t;
                        c[n + 96 >> 2] = v;
                        b9(n + 100 | 0, 0, 24);
                        c[u >> 2] = v;
                        c[n + 128 >> 2] = t;
                        b9(n + 132 | 0, 0, 24);
                        c[l >> 2] = n;
                        w = a + 156 | 0;
                        t = c[w >> 2] | 0;
                        x = a + 160 + (t << 5) | 0;
                        if ((t | 0) > 0) {
                            b = 15;
                            break
                        } else {
                            b = 23;
                            break
                        };
                    case 15:
                        y = a + 4 | 0;
                        z = a + 160 | 0;
                        A = c[y >> 2] | 0;
                        b = 16;
                        break;
                    case 16:
                        B = z | 0;
                        C = c[z + 24 >> 2] | 0;
                        if ((A | 0) == 0) {
                            b = 17;
                            break
                        } else {
                            b = 19;
                            break
                        };
                    case 17:
                        D = ah(12, c[l >> 2] | 0) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((D | 0) == 0) {
                            b = 24;
                            break
                        } else {
                            b = 18;
                            break
                        };
                    case 18:
                        t = al(2, D | 0, c[D + 4 >> 2] | 0) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((t | 0) == 0) {
                            b = 24;
                            break
                        } else {
                            E = D;
                            b = 22;
                            break
                        };
                    case 19:
                        F = ah(4, A | 0) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((F | 0) == 0) {
                            b = 24;
                            break
                        } else {
                            b = 20;
                            break
                        };
                    case 20:
                        t = c[F + 4 >> 2] | 0;
                        v = A + 4 | 0;
                        u = c[v >> 2] | 0;
                        s = c[(c[u + 4 >> 2] | 0) + 12 >> 2] | 0;
                        r = u + 8 | 0;
                        o = c[r >> 2] | 0;
                        m = s + 8 | 0;
                        G = c[m >> 2] | 0;
                        c[(c[o + 4 >> 2] | 0) + 12 >> 2] = s;
                        c[(c[G + 4 >> 2] | 0) + 12 >> 2] = u;
                        c[r >> 2] = G;
                        c[m >> 2] = o;
                        o = c[v >> 2] | 0;
                        m = o + 8 | 0;
                        G = c[m >> 2] | 0;
                        r = t + 8 | 0;
                        u = c[r >> 2] | 0;
                        c[(c[G + 4 >> 2] | 0) + 12 >> 2] = t;
                        c[(c[u + 4 >> 2] | 0) + 12 >> 2] = o;
                        c[m >> 2] = u;
                        c[r >> 2] = G;
                        c[(c[v >> 2] | 0) + 16 >> 2] = c[t + 16 >> 2];
                        G = t + 4 | 0;
                        r = c[G >> 2] | 0;
                        c[(c[r + 16 >> 2] | 0) + 8 >> 2] = r;
                        c[(c[G >> 2] | 0) + 20 >> 2] = c[(c[v >> 2] | 0) + 20 >> 2];
                        c[t + 28 >> 2] = c[A + 28 >> 2];
                        c[(c[G >> 2] | 0) + 28 >> 2] = c[(c[v >> 2] | 0) + 28 >> 2];
                        if ((t | 0) == 0) {
                            b = 24;
                            break
                        } else {
                            b = 21;
                            break
                        };
                    case 21:
                        E = c[A + 12 >> 2] | 0;
                        b = 22;
                        break;
                    case 22:
                        t = E + 16 | 0;
                        c[(c[t >> 2] | 0) + 12 >> 2] = C;
                        h[(c[t >> 2] | 0) + 16 >> 3] = +h[B >> 3];
                        h[(c[t >> 2] | 0) + 24 >> 3] = +h[z + 8 >> 3];
                        h[(c[t >> 2] | 0) + 32 >> 3] = +h[z + 16 >> 3];
                        c[E + 28 >> 2] = 1;
                        c[(c[E + 4 >> 2] | 0) + 28 >> 2] = -1;
                        c[y >> 2] = E;
                        t = z + 32 | 0;
                        if (t >>> 0 < x >>> 0) {
                            z = t;
                            A = E;
                            b = 16;
                            break
                        } else {
                            b = 23;
                            break
                        };
                    case 23:
                        c[w >> 2] = 0;
                        c[a + 152 >> 2] = 0;
                        b = 25;
                        break;
                    case 24:
                        ag(10, f | 0, 1);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                    case 25:
                        af(6, a | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        t = ah(10, a | 0) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((t | 0) == 0) {
                            b = 26;
                            break
                        } else {
                            b = 27;
                            break
                        };
                    case 26:
                        ag(10, f | 0, 1);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                    case 27:
                        H = c[l >> 2] | 0;
                        if ((c[a + 100 >> 2] | 0) == 0) {
                            b = 28;
                            break
                        } else {
                            b = 71;
                            break
                        };
                    case 28:
                        I = a + 124 | 0;
                        if ((c[I >> 2] | 0) == 0) {
                            b = 34;
                            break
                        } else {
                            b = 29;
                            break
                        };
                    case 29:
                        J = H + 92 | 0;
                        t = c[J >> 2] | 0;
                        if ((t | 0) == (J | 0)) {
                            b = 39;
                            break
                        } else {
                            K = t;
                            b = 30;
                            break
                        };
                    case 30:
                        L = c[K >> 2] | 0;
                        M = c[(c[K + 20 >> 2] | 0) + 24 >> 2] | 0;
                        if ((c[(c[(c[K + 4 >> 2] | 0) + 20 >> 2] | 0) + 24 >> 2] | 0) == (M | 0)) {
                            b = 32;
                            break
                        } else {
                            b = 31;
                            break
                        };
                    case 31:
                        c[K + 28 >> 2] = (M | 0) != 0 ? 1 : -1;
                        b = 33;
                        break;
                    case 32:
                        t = ah(8, K | 0) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((t | 0) == 0) {
                            b = 38;
                            break
                        } else {
                            b = 33;
                            break
                        };
                    case 33:
                        if ((L | 0) == (J | 0)) {
                            b = 39;
                            break
                        } else {
                            K = L;
                            b = 30;
                            break
                        };
                    case 34:
                        N = H + 64 | 0;
                        t = c[N >> 2] | 0;
                        if ((t | 0) == (N | 0)) {
                            b = 39;
                            break
                        } else {
                            O = t;
                            b = 35;
                            break
                        };
                    case 35:
                        P = c[O >> 2] | 0;
                        if ((c[O + 24 >> 2] | 0) == 0) {
                            b = 37;
                            break
                        } else {
                            b = 36;
                            break
                        };
                    case 36:
                        t = ah(6, O | 0) | 0;
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((t | 0) == 0) {
                            b = 38;
                            break
                        } else {
                            b = 37;
                            break
                        };
                    case 37:
                        if ((P | 0) == (N | 0)) {
                            b = 39;
                            break
                        } else {
                            O = P;
                            b = 35;
                            break
                        };
                    case 38:
                        ag(10, f | 0, 1);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                    case 39:
                        af(2, H | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        Q = a + 132 | 0;
                        if ((c[Q >> 2] | 0) == 10) {
                            b = 40;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 40:
                        if ((c[a + 144 >> 2] | 0) == 2) {
                            b = 41;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 41:
                        if ((c[a + 140 >> 2] | 0) == 20) {
                            b = 42;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 42:
                        if ((c[a + 136 >> 2] | 0) == 16) {
                            b = 43;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 43:
                        if ((c[a + 3360 >> 2] | 0) == 26) {
                            b = 44;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 44:
                        if ((c[a + 3372 >> 2] | 0) == 4) {
                            b = 45;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 45:
                        if ((c[a + 3368 >> 2] | 0) == 6) {
                            b = 46;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 46:
                        if ((c[a + 3364 >> 2] | 0) == 20) {
                            b = 64;
                            break
                        } else {
                            b = 47;
                            break
                        };
                    case 47:
                        if ((c[I >> 2] | 0) == 0) {
                            b = 63;
                            break
                        } else {
                            b = 48;
                            break
                        };
                    case 48:
                        R = H + 64 | 0;
                        S = c[R >> 2] | 0;
                        if ((S | 0) == (R | 0)) {
                            b = 64;
                            break
                        } else {
                            b = 49;
                            break
                        };
                    case 49:
                        T = a + 3368 | 0;
                        U = a + 140 | 0;
                        V = a + 3424 | 0;
                        W = a + 3372 | 0;
                        X = a + 144 | 0;
                        Y = S;
                        b = 50;
                        break;
                    case 50:
                        if ((c[Y + 24 >> 2] | 0) == 0) {
                            b = 51;
                            break
                        } else {
                            b = 52;
                            break
                        };
                    case 51:
                        t = c[Y >> 2] | 0;
                        if ((t | 0) == (R | 0)) {
                            b = 64;
                            break
                        } else {
                            Y = t;
                            b = 50;
                            break
                        };
                    case 52:
                        Z = c[a + 3360 >> 2] | 0;
                        if ((Z | 0) == 26) {
                            b = 54;
                            break
                        } else {
                            b = 53;
                            break
                        };
                    case 53:
                        ag(Z | 0, 2, c[V >> 2] | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 55;
                        break;
                    case 54:
                        af(c[Q >> 2] | 0, 2);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 55;
                        break;
                    case 55:
                        _ = Y + 8 | 0;
                        $ = c[_ >> 2] | 0;
                        b = 56;
                        break;
                    case 56:
                        aa = c[T >> 2] | 0;
                        if ((aa | 0) == 6) {
                            b = 58;
                            break
                        } else {
                            b = 57;
                            break
                        };
                    case 57:
                        ag(aa | 0, c[(c[$ + 16 >> 2] | 0) + 12 >> 2] | 0, c[V >> 2] | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 59;
                        break;
                    case 58:
                        af(c[U >> 2] | 0, c[(c[$ + 16 >> 2] | 0) + 12 >> 2] | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 59;
                        break;
                    case 59:
                        t = c[$ + 12 >> 2] | 0;
                        if ((t | 0) == (c[_ >> 2] | 0)) {
                            b = 60;
                            break
                        } else {
                            $ = t;
                            b = 56;
                            break
                        };
                    case 60:
                        ab = c[W >> 2] | 0;
                        if ((ab | 0) == 4) {
                            b = 62;
                            break
                        } else {
                            b = 61;
                            break
                        };
                    case 61:
                        af(ab | 0, c[V >> 2] | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 51;
                        break;
                    case 62:
                        ak(c[X >> 2] | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 51;
                        break;
                    case 63:
                        ag(14, a | 0, H | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 64;
                        break;
                    case 64:
                        ac = a + 148 | 0;
                        ad = c[ac >> 2] | 0;
                        if ((ad | 0) == 8) {
                            b = 71;
                            break
                        } else {
                            b = 65;
                            break
                        };
                    case 65:
                        ae = H + 64 | 0;
                        t = c[ae >> 2] | 0;
                        if ((t | 0) == (ae | 0)) {
                            ai = ad;
                            b = 70;
                            break
                        } else {
                            aj = t;
                            b = 66;
                            break
                        };
                    case 66:
                        am = c[aj >> 2] | 0;
                        if ((c[aj + 24 >> 2] | 0) == 0) {
                            b = 68;
                            break
                        } else {
                            b = 67;
                            break
                        };
                    case 67:
                        if ((am | 0) == (ae | 0)) {
                            b = 69;
                            break
                        } else {
                            aj = am;
                            b = 66;
                            break
                        };
                    case 68:
                        af(12, aj | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        b = 67;
                        break;
                    case 69:
                        ai = c[ac >> 2] | 0;
                        b = 70;
                        break;
                    case 70:
                        af(ai | 0, H | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        c[l >> 2] = 0;
                        c[a + 3424 >> 2] = 0;
                        b = 78;
                        break;
                    case 71:
                        an = H + 64 | 0;
                        t = c[an >> 2] | 0;
                        if ((t | 0) == (an | 0)) {
                            b = 73;
                            break
                        } else {
                            ao = t;
                            b = 72;
                            break
                        };
                    case 72:
                        t = c[ao >> 2] | 0;
                        af(14, ao | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((t | 0) == (an | 0)) {
                            b = 73;
                            break
                        } else {
                            ao = t;
                            b = 72;
                            break
                        };
                    case 73:
                        ap = H | 0;
                        t = c[H >> 2] | 0;
                        if ((t | 0) == (ap | 0)) {
                            b = 75;
                            break
                        } else {
                            aq = t;
                            b = 74;
                            break
                        };
                    case 74:
                        t = c[aq >> 2] | 0;
                        af(14, aq | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((t | 0) == (ap | 0)) {
                            b = 75;
                            break
                        } else {
                            aq = t;
                            b = 74;
                            break
                        };
                    case 75:
                        ar = H + 92 | 0;
                        t = c[ar >> 2] | 0;
                        if ((t | 0) == (ar | 0)) {
                            b = 77;
                            break
                        } else {
                            as = t;
                            b = 76;
                            break
                        };
                    case 76:
                        t = c[as >> 2] | 0;
                        af(14, as | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        if ((t | 0) == (ar | 0)) {
                            b = 77;
                            break
                        } else {
                            as = t;
                            b = 76;
                            break
                        };
                    case 77:
                        af(14, H | 0);
                        if ((p | 0) != 0 & (q | 0) != 0) {
                            d = b8(c[p >> 2] | 0, e) | 0;
                            if ((d | 0) > 0) {
                                b = -1;
                                break
                            } else return
                        }
                        p = q = 0;
                        c[a + 3424 >> 2] = 0;
                        c[l >> 2] = 0;
                        b = 78;
                        break;
                    case 78:
                        return;
                    case -1:
                        if ((d | 0) == 1) {
                            g = q;
                            b = 79
                        }
                        p = q = 0;
                        break
                }
            }
        }

        function bx(a, b, c, d, e) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            e = e | 0;
            var f = 0.0,
                g = 0.0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0.0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0.0,
                s = 0,
                t = 0.0,
                u = 0.0,
                v = 0.0,
                w = 0.0,
                x = 0.0,
                y = 0.0,
                z = 0.0,
                A = 0.0,
                B = 0.0,
                C = 0.0,
                D = 0.0,
                E = 0.0,
                F = 0.0,
                G = 0.0,
                H = 0.0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0.0,
                N = 0,
                O = 0,
                P = 0,
                Q = 0,
                R = 0.0,
                S = 0.0,
                T = 0.0,
                U = 0.0,
                V = 0.0,
                W = 0.0,
                X = 0.0,
                Y = 0.0,
                Z = 0.0,
                _ = 0.0;
            f = +h[a + 40 >> 3];
            g = +h[b + 40 >> 3];
            do {
                if (f < g) {
                    i = a;
                    j = b
                } else {
                    if (f == g) {
                        if (+h[a + 48 >> 3] <= +h[b + 48 >> 3]) {
                            i = a;
                            j = b;
                            break
                        }
                    }
                    i = b;
                    j = a
                }
            } while (0);
            g = +h[c + 40 >> 3];
            f = +h[d + 40 >> 3];
            do {
                if (g < f) {
                    k = c;
                    l = d;
                    m = g
                } else {
                    if (g == f) {
                        if (+h[c + 48 >> 3] <= +h[d + 48 >> 3]) {
                            k = c;
                            l = d;
                            m = g;
                            break
                        }
                    }
                    k = d;
                    l = c;
                    m = f
                }
            } while (0);
            f = +h[i + 40 >> 3];
            do {
                if (f < m) {
                    n = i;
                    o = j;
                    p = k;
                    q = l;
                    r = m
                } else {
                    if (f == m) {
                        if (+h[i + 48 >> 3] <= +h[k + 48 >> 3]) {
                            n = i;
                            o = j;
                            p = k;
                            q = l;
                            r = m;
                            break
                        }
                    }
                    n = k;
                    o = l;
                    p = i;
                    q = j;
                    r = f
                }
            } while (0);
            j = p + 40 | 0;
            i = o + 40 | 0;
            f = +h[i >> 3];
            l = r < f;
            do {
                if (l) {
                    s = 521
                } else {
                    if (r == f) {
                        if (+h[p + 48 >> 3] <= +h[o + 48 >> 3]) {
                            s = 521;
                            break
                        }
                    }
                    h[e + 40 >> 3] = (r + f) * .5
                }
            } while (0);
            L699: do {
                if ((s | 0) == 521) {
                    m = +h[q + 40 >> 3];
                    k = f < m;
                    do {
                        if (!k) {
                            c = f == m;
                            if (c) {
                                if (+h[o + 48 >> 3] <= +h[q + 48 >> 3]) {
                                    break
                                }
                            }
                            g = +h[n + 40 >> 3];
                            do {
                                if (g >= r) {
                                    if (g != r) {
                                        ay(688, 85, 2464, 1384)
                                    }
                                    if (+h[n + 48 >> 3] <= +h[p + 48 >> 3]) {
                                        break
                                    }
                                    ay(688, 85, 2464, 1384)
                                }
                            } while (0);
                            do {
                                if (!l) {
                                    if (r != f) {
                                        ay(688, 85, 2464, 1384)
                                    }
                                    if (+h[p + 48 >> 3] <= +h[o + 48 >> 3]) {
                                        break
                                    }
                                    ay(688, 85, 2464, 1384)
                                }
                            } while (0);
                            t = r - g;
                            u = f - r;
                            if (t + u > 0.0) {
                                v = +h[p + 48 >> 3];
                                w = t * (v - +h[o + 48 >> 3]) + u * (v - +h[n + 48 >> 3])
                            } else {
                                w = 0.0
                            }
                            do {
                                if (g >= m) {
                                    if (g != m) {
                                        ay(688, 85, 2464, 1384)
                                    }
                                    if (+h[n + 48 >> 3] <= +h[q + 48 >> 3]) {
                                        break
                                    }
                                    ay(688, 85, 2464, 1384)
                                }
                            } while (0);
                            do {
                                if (m >= f) {
                                    if (!c) {
                                        ay(688, 85, 2464, 1384)
                                    }
                                    if (+h[q + 48 >> 3] <= +h[o + 48 >> 3]) {
                                        break
                                    }
                                    ay(688, 85, 2464, 1384)
                                }
                            } while (0);
                            v = m - g;
                            u = f - m;
                            if (v + u > 0.0) {
                                t = +h[q + 48 >> 3];
                                x = v * (t - +h[o + 48 >> 3]) + u * (t - +h[n + 48 >> 3])
                            } else {
                                x = 0.0
                            }
                            if (w - x < 0.0) {
                                y = -0.0 - w;
                                z = x
                            } else {
                                y = w;
                                z = -0.0 - x
                            }
                            t = y < 0.0 ? 0.0 : y;
                            u = z < 0.0 ? 0.0 : z;
                            do {
                                if (t > u) {
                                    A = m + (r - m) * (u / (u + t))
                                } else {
                                    if (u == 0.0) {
                                        A = (r + m) * .5;
                                        break
                                    } else {
                                        A = r + (m - r) * (t / (u + t));
                                        break
                                    }
                                }
                            } while (0);
                            h[e + 40 >> 3] = A;
                            break L699
                        }
                    } while (0);
                    t = +h[n + 40 >> 3];
                    do {
                        if (t >= r) {
                            if (t != r) {
                                ay(688, 61, 2480, 1384)
                            }
                            if (+h[n + 48 >> 3] <= +h[p + 48 >> 3]) {
                                break
                            }
                            ay(688, 61, 2480, 1384)
                        }
                    } while (0);
                    do {
                        if (!l) {
                            if (r != f) {
                                ay(688, 61, 2480, 1384)
                            }
                            if (+h[p + 48 >> 3] <= +h[o + 48 >> 3]) {
                                break
                            }
                            ay(688, 61, 2480, 1384)
                        }
                    } while (0);
                    u = r - t;
                    g = f - r;
                    v = u + g;
                    do {
                        if (v > 0.0) {
                            B = +h[p + 48 >> 3];
                            if (u < g) {
                                C = +h[n + 48 >> 3];
                                D = B - C + (C - +h[o + 48 >> 3]) * (u / v);
                                break
                            } else {
                                C = +h[o + 48 >> 3];
                                D = B - C + (C - +h[n + 48 >> 3]) * (g / v);
                                break
                            }
                        } else {
                            D = 0.0
                        }
                    } while (0);
                    do {
                        if (!l) {
                            if (r != f) {
                                ay(688, 61, 2480, 1384)
                            }
                            if (+h[p + 48 >> 3] <= +h[o + 48 >> 3]) {
                                break
                            }
                            ay(688, 61, 2480, 1384)
                        }
                    } while (0);
                    do {
                        if (!k) {
                            if (f != m) {
                                ay(688, 61, 2480, 1384)
                            }
                            if (+h[o + 48 >> 3] <= +h[q + 48 >> 3]) {
                                break
                            }
                            ay(688, 61, 2480, 1384)
                        }
                    } while (0);
                    v = m - f;
                    u = g + v;
                    do {
                        if (u > 0.0) {
                            t = +h[o + 48 >> 3];
                            if (g < v) {
                                C = +h[p + 48 >> 3];
                                E = t - C + (C - +h[q + 48 >> 3]) * (g / u);
                                break
                            } else {
                                C = +h[q + 48 >> 3];
                                E = t - C + (C - +h[p + 48 >> 3]) * (v / u);
                                break
                            }
                        } else {
                            E = 0.0
                        }
                    } while (0);
                    if (D + E < 0.0) {
                        F = -0.0 - D;
                        G = -0.0 - E
                    } else {
                        F = D;
                        G = E
                    }
                    u = F < 0.0 ? 0.0 : F;
                    v = G < 0.0 ? 0.0 : G;
                    do {
                        if (u > v) {
                            H = f + (r - f) * (v / (v + u))
                        } else {
                            if (v == 0.0) {
                                H = (r + f) * .5;
                                break
                            } else {
                                H = r + g * (u / (v + u));
                                break
                            }
                        }
                    } while (0);
                    h[e + 40 >> 3] = H
                }
            } while (0);
            H = +h[n + 48 >> 3];
            r = +h[o + 48 >> 3];
            do {
                if (H < r) {
                    I = n;
                    J = o
                } else {
                    if (H == r) {
                        if (+h[n + 40 >> 3] <= +h[i >> 3]) {
                            I = n;
                            J = o;
                            break
                        }
                    }
                    I = o;
                    J = n
                }
            } while (0);
            r = +h[p + 48 >> 3];
            H = +h[q + 48 >> 3];
            do {
                if (r < H) {
                    K = p;
                    L = q;
                    M = r
                } else {
                    if (r == H) {
                        if (+h[j >> 3] <= +h[q + 40 >> 3]) {
                            K = p;
                            L = q;
                            M = r;
                            break
                        }
                    }
                    K = q;
                    L = p;
                    M = H
                }
            } while (0);
            H = +h[I + 48 >> 3];
            do {
                if (H < M) {
                    N = I;
                    O = J;
                    P = K;
                    Q = L
                } else {
                    if (H == M) {
                        if (+h[I + 40 >> 3] <= +h[K + 40 >> 3]) {
                            N = I;
                            O = J;
                            P = K;
                            Q = L;
                            break
                        }
                    }
                    N = K;
                    O = L;
                    P = I;
                    Q = J
                }
            } while (0);
            M = +h[P + 48 >> 3];
            H = +h[O + 48 >> 3];
            J = M < H;
            do {
                if (!J) {
                    if (M == H) {
                        if (+h[P + 40 >> 3] <= +h[O + 40 >> 3]) {
                            break
                        }
                    }
                    h[e + 48 >> 3] = (M + H) * .5;
                    return
                }
            } while (0);
            r = +h[Q + 48 >> 3];
            I = H < r;
            do {
                if (!I) {
                    L = H == r;
                    if (L) {
                        if (+h[O + 40 >> 3] <= +h[Q + 40 >> 3]) {
                            break
                        }
                    }
                    f = +h[N + 48 >> 3];
                    do {
                        if (f >= M) {
                            if (f != M) {
                                ay(688, 140, 2216, 832)
                            }
                            if (+h[N + 40 >> 3] <= +h[P + 40 >> 3]) {
                                break
                            }
                            ay(688, 140, 2216, 832)
                        }
                    } while (0);
                    do {
                        if (!J) {
                            if (M != H) {
                                ay(688, 140, 2216, 832)
                            }
                            if (+h[P + 40 >> 3] <= +h[O + 40 >> 3]) {
                                break
                            }
                            ay(688, 140, 2216, 832)
                        }
                    } while (0);
                    G = M - f;
                    F = H - M;
                    if (G + F > 0.0) {
                        E = +h[P + 40 >> 3];
                        R = G * (E - +h[O + 40 >> 3]) + F * (E - +h[N + 40 >> 3])
                    } else {
                        R = 0.0
                    }
                    do {
                        if (f >= r) {
                            if (f != r) {
                                ay(688, 140, 2216, 832)
                            }
                            if (+h[N + 40 >> 3] <= +h[Q + 40 >> 3]) {
                                break
                            }
                            ay(688, 140, 2216, 832)
                        }
                    } while (0);
                    do {
                        if (r >= H) {
                            if (!L) {
                                ay(688, 140, 2216, 832)
                            }
                            if (+h[Q + 40 >> 3] <= +h[O + 40 >> 3]) {
                                break
                            }
                            ay(688, 140, 2216, 832)
                        }
                    } while (0);
                    E = r - f;
                    F = H - r;
                    if (E + F > 0.0) {
                        G = +h[Q + 40 >> 3];
                        S = E * (G - +h[O + 40 >> 3]) + F * (G - +h[N + 40 >> 3])
                    } else {
                        S = 0.0
                    }
                    if (R - S < 0.0) {
                        T = -0.0 - R;
                        U = S
                    } else {
                        T = R;
                        U = -0.0 - S
                    }
                    G = T < 0.0 ? 0.0 : T;
                    F = U < 0.0 ? 0.0 : U;
                    do {
                        if (G > F) {
                            V = r + (M - r) * (F / (F + G))
                        } else {
                            if (F == 0.0) {
                                V = (M + r) * .5;
                                break
                            } else {
                                V = M + (r - M) * (G / (F + G));
                                break
                            }
                        }
                    } while (0);
                    h[e + 48 >> 3] = V;
                    return
                }
            } while (0);
            V = +h[N + 48 >> 3];
            do {
                if (V >= M) {
                    if (V != M) {
                        ay(688, 116, 2232, 832)
                    }
                    if (+h[N + 40 >> 3] <= +h[P + 40 >> 3]) {
                        break
                    }
                    ay(688, 116, 2232, 832)
                }
            } while (0);
            do {
                if (!J) {
                    if (M != H) {
                        ay(688, 116, 2232, 832)
                    }
                    if (+h[P + 40 >> 3] <= +h[O + 40 >> 3]) {
                        break
                    }
                    ay(688, 116, 2232, 832)
                }
            } while (0);
            U = M - V;
            V = H - M;
            T = U + V;
            do {
                if (T > 0.0) {
                    S = +h[P + 40 >> 3];
                    if (U < V) {
                        R = +h[N + 40 >> 3];
                        W = S - R + (R - +h[O + 40 >> 3]) * (U / T);
                        break
                    } else {
                        R = +h[O + 40 >> 3];
                        W = S - R + (R - +h[N + 40 >> 3]) * (V / T);
                        break
                    }
                } else {
                    W = 0.0
                }
            } while (0);
            do {
                if (!J) {
                    if (M != H) {
                        ay(688, 116, 2232, 832)
                    }
                    if (+h[P + 40 >> 3] <= +h[O + 40 >> 3]) {
                        break
                    }
                    ay(688, 116, 2232, 832)
                }
            } while (0);
            do {
                if (!I) {
                    if (H != r) {
                        ay(688, 116, 2232, 832)
                    }
                    if (+h[O + 40 >> 3] <= +h[Q + 40 >> 3]) {
                        break
                    }
                    ay(688, 116, 2232, 832)
                }
            } while (0);
            T = r - H;
            r = V + T;
            do {
                if (r > 0.0) {
                    U = +h[O + 40 >> 3];
                    if (V < T) {
                        R = +h[P + 40 >> 3];
                        X = U - R + (R - +h[Q + 40 >> 3]) * (V / r);
                        break
                    } else {
                        R = +h[Q + 40 >> 3];
                        X = U - R + (R - +h[P + 40 >> 3]) * (T / r);
                        break
                    }
                } else {
                    X = 0.0
                }
            } while (0);
            if (W + X < 0.0) {
                Y = -0.0 - W;
                Z = -0.0 - X
            } else {
                Y = W;
                Z = X
            }
            X = Y < 0.0 ? 0.0 : Y;
            Y = Z < 0.0 ? 0.0 : Z;
            do {
                if (X > Y) {
                    _ = H + (M - H) * (Y / (Y + X))
                } else {
                    if (Y == 0.0) {
                        _ = (M + H) * .5;
                        break
                    } else {
                        _ = M + V * (X / (Y + X));
                        break
                    }
                }
            } while (0);
            h[e + 48 >> 3] = _;
            return
        }

        function by(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0.0,
                p = 0,
                q = 0.0,
                r = 0,
                s = 0.0,
                t = 0,
                u = 0,
                v = 0.0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0.0,
                G = 0.0,
                H = 0.0,
                I = 0.0,
                J = 0.0,
                K = 0.0,
                L = 0,
                M = 0,
                N = 0,
                O = 0,
                P = 0,
                Q = 0,
                R = 0,
                S = 0.0,
                T = 0.0,
                U = 0.0,
                V = 0.0,
                W = 0.0,
                X = 0.0,
                Y = 0.0,
                Z = 0.0,
                _ = 0.0,
                $ = 0.0,
                aa = 0.0,
                ab = 0.0,
                ac = 0.0,
                ad = 0,
                ae = 0.0,
                af = 0,
                ag = 0,
                ah = 0,
                ai = 0.0,
                aj = 0.0,
                ak = 0.0,
                al = 0.0,
                am = 0.0,
                an = 0.0,
                ao = 0.0,
                ap = 0.0,
                aq = 0.0,
                ar = 0.0,
                as = 0.0,
                at = 0.0,
                au = 0.0,
                av = 0.0,
                aw = 0.0,
                ax = 0,
                ay = 0,
                az = 0,
                aA = 0.0;
            b = i;
            i = i + 128 | 0;
            d = b | 0;
            e = b + 24 | 0;
            f = b + 48 | 0;
            g = b + 72 | 0;
            j = b + 88 | 0;
            k = b + 104 | 0;
            l = a + 8 | 0;
            m = c[l >> 2] | 0;
            n = m | 0;
            o = +h[a + 16 >> 3];
            p = k | 0;
            h[p >> 3] = o;
            q = +h[a + 24 >> 3];
            r = k + 8 | 0;
            h[r >> 3] = q;
            s = +h[a + 32 >> 3];
            t = k + 16 | 0;
            h[t >> 3] = s;
            do {
                if (o == 0.0) {
                    if (!(q == 0.0 & s == 0.0)) {
                        u = 0;
                        v = q;
                        w = 724;
                        break
                    }
                    x = d + 16 | 0;
                    h[x >> 3] = -2.0e+150;
                    y = d + 8 | 0;
                    h[y >> 3] = -2.0e+150;
                    z = d | 0;
                    h[z >> 3] = -2.0e+150;
                    A = e + 16 | 0;
                    h[A >> 3] = 2.0e+150;
                    B = e + 8 | 0;
                    h[B >> 3] = 2.0e+150;
                    C = e | 0;
                    h[C >> 3] = 2.0e+150;
                    D = c[m >> 2] | 0;
                    E = (D | 0) == (n | 0);
                    if (E) {
                        F = -2.0e+150;
                        G = 2.0e+150;
                        H = -2.0e+150;
                        I = 2.0e+150;
                        J = -2.0e+150;
                        K = 2.0e+150
                    } else {
                        L = j | 0;
                        M = g | 0;
                        N = j + 4 | 0;
                        O = g + 4 | 0;
                        P = j + 8 | 0;
                        Q = g + 8 | 0;
                        R = D;
                        S = 2.0e+150;
                        T = -2.0e+150;
                        U = 2.0e+150;
                        V = -2.0e+150;
                        W = 2.0e+150;
                        X = -2.0e+150;
                        var tessellationLimit6 = 0;
                        while (1) {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            Y = +h[R + 16 >> 3];
                            if (Y < S) {
                                h[C >> 3] = Y;
                                c[L >> 2] = R;
                                Z = Y
                            } else {
                                Z = S
                            }
                            if (Y > T) {
                                h[z >> 3] = Y;
                                c[M >> 2] = R;
                                _ = Y
                            } else {
                                _ = T
                            }
                            Y = +h[R + 24 >> 3];
                            if (Y < U) {
                                h[B >> 3] = Y;
                                c[N >> 2] = R;
                                $ = Y
                            } else {
                                $ = U
                            }
                            if (Y > V) {
                                h[y >> 3] = Y;
                                c[O >> 2] = R;
                                aa = Y
                            } else {
                                aa = V
                            }
                            Y = +h[R + 32 >> 3];
                            if (Y < W) {
                                h[A >> 3] = Y;
                                c[P >> 2] = R;
                                ab = Y
                            } else {
                                ab = W
                            }
                            if (Y > X) {
                                h[x >> 3] = Y;
                                c[Q >> 2] = R;
                                ac = Y
                            } else {
                                ac = X
                            }
                            ad = c[R >> 2] | 0;
                            if ((ad | 0) == (n | 0)) {
                                F = aa;
                                G = $;
                                H = _;
                                I = Z;
                                J = ac;
                                K = ab;
                                break
                            } else {
                                R = ad;
                                S = Z;
                                T = _;
                                U = $;
                                V = aa;
                                W = ab;
                                X = ac
                            }
                        }
                    }
                    R = F - G > H - I & 1;
                    Q = J - K > +h[d + (R << 3) >> 3] - +h[e + (R << 3) >> 3] ? 2 : R;
                    if (+h[e + (Q << 3) >> 3] >= +h[d + (Q << 3) >> 3]) {
                        b9(k | 0, 0, 16);
                        h[t >> 3] = 1.0;
                        ae = 0.0;
                        af = 1;
                        ag = a + 40 | 0;
                        ah = a + 64 | 0;
                        break
                    }
                    R = c[j + (Q << 2) >> 2] | 0;
                    x = c[g + (Q << 2) >> 2] | 0;
                    X = +h[x + 16 >> 3];
                    W = +h[R + 16 >> 3] - X;
                    h[f >> 3] = W;
                    V = +h[x + 24 >> 3];
                    U = +h[R + 24 >> 3] - V;
                    h[f + 8 >> 3] = U;
                    T = +h[x + 32 >> 3];
                    S = +h[R + 32 >> 3] - T;
                    h[f + 16 >> 3] = S;
                    if (!E) {
                        Y = 0.0;
                        R = D;
                        ai = q;
                        var tessellationLimit6 = 0;
                        while (1) {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            aj = +h[R + 16 >> 3] - X;
                            ak = +h[R + 24 >> 3] - V;
                            al = +h[R + 32 >> 3] - T;
                            am = U * al - S * ak;
                            an = S * aj - W * al;
                            al = W * ak - U * aj;
                            aj = al * al + (am * am + an * an);
                            if (aj > Y) {
                                h[p >> 3] = am;
                                h[r >> 3] = an;
                                h[t >> 3] = al;
                                ao = aj;
                                ap = an
                            } else {
                                ao = Y;
                                ap = ai
                            }
                            x = c[R >> 2] | 0;
                            if ((x | 0) == (n | 0)) {
                                break
                            } else {
                                Y = ao;
                                R = x;
                                ai = ap
                            }
                        }
                        if (ao > 0.0) {
                            u = 1;
                            v = ap;
                            w = 724;
                            break
                        }
                    }
                    b9(k | 0, 0, 24);
                    if (U < 0.0) {
                        aq = -0.0 - U
                    } else {
                        aq = U
                    }
                    if (W < 0.0) {
                        ar = -0.0 - W
                    } else {
                        ar = W
                    }
                    R = aq > ar & 1;
                    if (S < 0.0) {
                        as = -0.0 - S
                    } else {
                        as = S
                    }
                    ai = +h[f + (R << 3) >> 3];
                    if (ai < 0.0) {
                        at = -0.0 - ai
                    } else {
                        at = ai
                    }
                    h[k + ((as > at ? 2 : R) << 3) >> 3] = 1.0;
                    u = 1;
                    v = +h[r >> 3];
                    w = 724
                } else {
                    u = 0;
                    v = q;
                    w = 724
                }
            } while (0);
            do {
                if ((w | 0) == 724) {
                    r = a + 40 | 0;
                    f = a + 64 | 0;
                    if (v >= 0.0) {
                        ae = v;
                        af = u;
                        ag = r;
                        ah = f;
                        break
                    }
                    ae = -0.0 - v;
                    af = u;
                    ag = r;
                    ah = f
                }
            } while (0);
            v = +h[p >> 3];
            if (v < 0.0) {
                au = -0.0 - v
            } else {
                au = v
            }
            p = ae > au & 1;
            au = +h[t >> 3];
            if (au < 0.0) {
                av = -0.0 - au
            } else {
                av = au
            }
            au = +h[k + (p << 3) >> 3];
            if (au < 0.0) {
                aw = -0.0 - au
            } else {
                aw = au
            }
            t = av > aw ? 2 : p;
            h[a + 40 + (t << 3) >> 3] = 0.0;
            p = ((t + 1 | 0) >>> 0) % 3 >>> 0;
            h[a + 40 + (p << 3) >> 3] = 1.0;
            u = ((t + 2 | 0) >>> 0) % 3 >>> 0;
            h[a + 40 + (u << 3) >> 3] = 0.0;
            h[a + 64 + (t << 3) >> 3] = 0.0;
            w = +h[k + (t << 3) >> 3] > 0.0;
            h[a + 64 + (p << 3) >> 3] = w ? -0.0 : 0.0;
            h[a + 64 + (u << 3) >> 3] = w ? 1.0 : -1.0;
            w = c[m >> 2] | 0;
            if ((w | 0) != (n | 0)) {
                m = a + 48 | 0;
                u = a + 56 | 0;
                p = a + 72 | 0;
                t = a + 80 | 0;
                k = w;
                var tessellationLimit4 = 0;
                do {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    aw = +h[k + 16 >> 3];
                    av = +h[k + 24 >> 3];
                    au = +h[k + 32 >> 3];
                    h[k + 40 >> 3] = aw * +h[ag >> 3] + av * +h[m >> 3] + au * +h[u >> 3];
                    h[k + 48 >> 3] = aw * +h[ah >> 3] + av * +h[p >> 3] + au * +h[t >> 3];
                    k = c[k >> 2] | 0;
                } while ((k | 0) != (n | 0))
            }
            if ((af | 0) == 0) {
                i = b;
                return
            }
            af = c[l >> 2] | 0;
            l = af + 64 | 0;
            au = 0.0;
            n = l;
            var tessellationLimit3 = 0;
            L1000: while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                k = n;
                var tessellationLimit4 = 0;
                while (1) {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    ax = c[k >> 2] | 0;
                    if ((ax | 0) == (l | 0)) {
                        break L1000
                    }
                    ay = c[ax + 8 >> 2] | 0;
                    if ((c[ay + 28 >> 2] | 0) < 1) {
                        k = ax
                    } else {
                        az = ay;
                        aA = au;
                        break
                    }
                }
                var tessellationLimit4 = 0;
                while (1) {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    k = c[az + 16 >> 2] | 0;
                    t = c[(c[az + 4 >> 2] | 0) + 16 >> 2] | 0;
                    S = aA + (+h[k + 40 >> 3] - +h[t + 40 >> 3]) * (+h[k + 48 >> 3] + +h[t + 48 >> 3]);
                    t = c[az + 12 >> 2] | 0;
                    if ((t | 0) == (ay | 0)) {
                        au = S;
                        n = ax;
                        continue L1000
                    } else {
                        az = t;
                        aA = S
                    }
                }
            }
            az = af | 0;
            if (au >= 0.0) {
                i = b;
                return
            }
            ax = c[af >> 2] | 0;
            if ((ax | 0) != (az | 0)) {
                af = ax;
                var tessellationLimit4 = 0;
                do {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    ax = af + 48 | 0;
                    h[ax >> 3] = -0.0 - +h[ax >> 3];
                    af = c[af >> 2] | 0;
                } while ((af | 0) != (az | 0))
            }
            h[ah >> 3] = -0.0 - +h[ah >> 3];
            ah = a + 72 | 0;
            h[ah >> 3] = -0.0 - +h[ah >> 3];
            ah = a + 80 | 0;
            h[ah >> 3] = -0.0 - +h[ah >> 3];
            i = b;
            return
        }

        function bz(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0;
            b = b0(28) | 0;
            if ((b | 0) == 0) {
                d = 0;
                return d | 0
            }
            c[b + 8 >> 2] = 0;
            c[b + 12 >> 2] = 32;
            e = b0(132) | 0;
            f = b;
            c[f >> 2] = e;
            if ((e | 0) == 0) {
                b1(b);
                d = 0;
                return d | 0
            }
            e = b0(264) | 0;
            g = b + 4 | 0;
            c[g >> 2] = e;
            if ((e | 0) == 0) {
                b1(c[f >> 2] | 0);
                b1(b);
                d = 0;
                return d | 0
            } else {
                c[b + 20 >> 2] = 0;
                c[b + 16 >> 2] = 0;
                c[b + 24 >> 2] = a;
                c[(c[f >> 2] | 0) + 4 >> 2] = 1;
                c[(c[g >> 2] | 0) + 8 >> 2] = 0;
                d = b;
                return d | 0
            }
            return 0
        }

        function bA(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0.0,
                o = 0,
                p = 0.0,
                q = 0,
                r = 0,
                s = 0,
                t = 0;
            d = c[a >> 2] | 0;
            e = c[a + 4 >> 2] | 0;
            f = c[d + (b << 2) >> 2] | 0;
            g = a + 8 | 0;
            i = a + 12 | 0;
            a = e + (f << 3) | 0;
            j = b;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                b = j << 1;
                k = c[g >> 2] | 0;
                do {
                    if ((b | 0) < (k | 0)) {
                        l = b | 1;
                        m = c[e + (c[d + (l << 2) >> 2] << 3) >> 2] | 0;
                        n = +h[m + 40 >> 3];
                        o = c[e + (c[d + (b << 2) >> 2] << 3) >> 2] | 0;
                        p = +h[o + 40 >> 3];
                        if (n >= p) {
                            if (n != p) {
                                q = b;
                                break
                            }
                            if (+h[m + 48 >> 3] > +h[o + 48 >> 3]) {
                                q = b;
                                break
                            }
                        }
                        q = l
                    } else {
                        q = b
                    }
                } while (0);
                if ((q | 0) > (c[i >> 2] | 0)) {
                    r = 767;
                    break
                }
                b = c[d + (q << 2) >> 2] | 0;
                if ((q | 0) > (k | 0)) {
                    r = 776;
                    break
                }
                l = c[a >> 2] | 0;
                p = +h[l + 40 >> 3];
                o = c[e + (b << 3) >> 2] | 0;
                n = +h[o + 40 >> 3];
                if (p < n) {
                    r = 775;
                    break
                }
                if (p == n) {
                    if (+h[l + 48 >> 3] <= +h[o + 48 >> 3]) {
                        r = 774;
                        break
                    }
                }
                c[d + (j << 2) >> 2] = b;
                c[e + (b << 3) + 4 >> 2] = j;
                j = q
            }
            if ((r | 0) == 775) {
                s = d + (j << 2) | 0;
                c[s >> 2] = f;
                t = e + (f << 3) + 4 | 0;
                c[t >> 2] = j;
                return
            } else if ((r | 0) == 774) {
                s = d + (j << 2) | 0;
                c[s >> 2] = f;
                t = e + (f << 3) + 4 | 0;
                c[t >> 2] = j;
                return
            } else if ((r | 0) == 767) {
                ay(592, 112, 2584, 128)
            } else if ((r | 0) == 776) {
                s = d + (j << 2) | 0;
                c[s >> 2] = f;
                t = e + (f << 3) + 4 | 0;
                c[t >> 2] = j;
                return
            }
        }

        function bB(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0.0,
                q = 0,
                r = 0.0;
            d = a + 8 | 0;
            e = (c[d >> 2] | 0) + 1 | 0;
            c[d >> 2] = e;
            d = a + 12 | 0;
            f = c[d >> 2] | 0;
            do {
                if ((e << 1 | 0) > (f | 0)) {
                    g = a | 0;
                    i = c[g >> 2] | 0;
                    j = a + 4 | 0;
                    k = c[j >> 2] | 0;
                    c[d >> 2] = f << 1;
                    l = b2(i, f << 3 | 4) | 0;
                    c[g >> 2] = l;
                    if ((l | 0) == 0) {
                        c[g >> 2] = i;
                        m = 2147483647;
                        return m | 0
                    }
                    i = b2(c[j >> 2] | 0, (c[d >> 2] << 3) + 8 | 0) | 0;
                    c[j >> 2] = i;
                    if ((i | 0) != 0) {
                        break
                    }
                    c[j >> 2] = k;
                    m = 2147483647;
                    return m | 0
                }
            } while (0);
            d = a + 16 | 0;
            f = c[d >> 2] | 0;
            k = a + 4 | 0;
            if ((f | 0) == 0) {
                n = e
            } else {
                c[d >> 2] = c[(c[k >> 2] | 0) + (f << 3) + 4 >> 2];
                n = f
            }
            f = a | 0;
            c[(c[f >> 2] | 0) + (e << 2) >> 2] = n;
            c[(c[k >> 2] | 0) + (n << 3) + 4 >> 2] = e;
            c[(c[k >> 2] | 0) + (n << 3) >> 2] = b;
            if ((c[a + 20 >> 2] | 0) != 0) {
                a = c[f >> 2] | 0;
                f = c[k >> 2] | 0;
                k = c[a + (e << 2) >> 2] | 0;
                b = e >> 1;
                L1065: do {
                    if ((b | 0) == 0) {
                        o = e
                    } else {
                        d = f + (k << 3) | 0;
                        j = e;
                        i = b;
                        var tessellationLimit6 = 0;
                        while (1) {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            g = c[a + (i << 2) >> 2] | 0;
                            l = c[f + (g << 3) >> 2] | 0;
                            p = +h[l + 40 >> 3];
                            q = c[d >> 2] | 0;
                            r = +h[q + 40 >> 3];
                            if (p < r) {
                                o = j;
                                break L1065
                            }
                            if (p == r) {
                                if (+h[l + 48 >> 3] <= +h[q + 48 >> 3]) {
                                    o = j;
                                    break L1065
                                }
                            }
                            c[a + (j << 2) >> 2] = g;
                            c[f + (g << 3) + 4 >> 2] = j;
                            g = i >> 1;
                            if ((g | 0) == 0) {
                                o = i;
                                break
                            } else {
                                j = i;
                                i = g
                            }
                        }
                    }
                } while (0);
                c[a + (o << 2) >> 2] = k;
                c[f + (k << 3) + 4 >> 2] = o
            }
            if ((n | 0) == 2147483647) {
                ay(592, 207, 2360, 1168);
                return 0
            } else {
                m = n;
                return m | 0
            }
            return 0
        }

        function bC(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0.0,
                r = 0,
                s = 0.0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0;
            d = a | 0;
            e = c[d >> 2] | 0;
            f = a + 4 | 0;
            g = c[f >> 2] | 0;
            if ((b | 0) <= 0) {
                ay(592, 241, 2384, 776)
            }
            if ((c[a + 12 >> 2] | 0) < (b | 0)) {
                ay(592, 241, 2384, 776)
            }
            i = g + (b << 3) | 0;
            if ((c[i >> 2] | 0) == 0) {
                ay(592, 241, 2384, 776)
            }
            j = g + (b << 3) + 4 | 0;
            k = c[j >> 2] | 0;
            l = a + 8 | 0;
            m = c[e + (c[l >> 2] << 2) >> 2] | 0;
            n = e + (k << 2) | 0;
            c[n >> 2] = m;
            c[g + (m << 3) + 4 >> 2] = k;
            m = (c[l >> 2] | 0) - 1 | 0;
            c[l >> 2] = m;
            if ((k | 0) > (m | 0)) {
                c[i >> 2] = 0;
                o = a + 16 | 0;
                p = c[o >> 2] | 0;
                c[j >> 2] = p;
                c[o >> 2] = b;
                return
            }
            do {
                if ((k | 0) >= 2) {
                    m = k >> 1;
                    l = c[g + (c[e + (m << 2) >> 2] << 3) >> 2] | 0;
                    q = +h[l + 40 >> 3];
                    r = c[g + (c[n >> 2] << 3) >> 2] | 0;
                    s = +h[r + 40 >> 3];
                    if (q < s) {
                        break
                    }
                    if (q == s) {
                        if (+h[l + 48 >> 3] <= +h[r + 48 >> 3]) {
                            break
                        }
                    }
                    r = c[d >> 2] | 0;
                    l = c[f >> 2] | 0;
                    t = c[r + (k << 2) >> 2] | 0;
                    L1097: do {
                        if ((m | 0) == 0) {
                            u = k
                        } else {
                            v = l + (t << 3) | 0;
                            w = k;
                            x = m;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                y = c[r + (x << 2) >> 2] | 0;
                                z = c[l + (y << 3) >> 2] | 0;
                                s = +h[z + 40 >> 3];
                                A = c[v >> 2] | 0;
                                q = +h[A + 40 >> 3];
                                if (s < q) {
                                    u = w;
                                    break L1097
                                }
                                if (s == q) {
                                    if (+h[z + 48 >> 3] <= +h[A + 48 >> 3]) {
                                        u = w;
                                        break L1097
                                    }
                                }
                                c[r + (w << 2) >> 2] = y;
                                c[l + (y << 3) + 4 >> 2] = w;
                                y = x >> 1;
                                if ((y | 0) == 0) {
                                    u = x;
                                    break
                                } else {
                                    w = x;
                                    x = y
                                }
                            }
                        }
                    } while (0);
                    c[r + (u << 2) >> 2] = t;
                    c[l + (t << 3) + 4 >> 2] = u;
                    c[i >> 2] = 0;
                    o = a + 16 | 0;
                    p = c[o >> 2] | 0;
                    c[j >> 2] = p;
                    c[o >> 2] = b;
                    return
                }
            } while (0);
            bA(a, k);
            c[i >> 2] = 0;
            o = a + 16 | 0;
            p = c[o >> 2] | 0;
            c[j >> 2] = p;
            c[o >> 2] = b;
            return
        }

        function bD(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0;
            b = b0(28) | 0;
            if ((b | 0) == 0) {
                d = 0;
                return d | 0
            }
            e = bz(a) | 0;
            f = b;
            c[f >> 2] = e;
            if ((e | 0) == 0) {
                b1(b);
                d = 0;
                return d | 0
            }
            e = b0(128) | 0;
            c[b + 4 >> 2] = e;
            if ((e | 0) == 0) {
                e = c[f >> 2] | 0;
                b1(c[e + 4 >> 2] | 0);
                b1(c[e >> 2] | 0);
                b1(e);
                b1(b);
                d = 0;
                return d | 0
            } else {
                c[b + 12 >> 2] = 0;
                c[b + 16 >> 2] = 32;
                c[b + 20 >> 2] = 0;
                c[b + 24 >> 2] = a;
                d = b;
                return d | 0
            }
            return 0
        }

        function bE(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0.0,
                s = 0.0,
                t = 0,
                u = 0;
            b = a + 12 | 0;
            d = c[b >> 2] | 0;
            if ((d | 0) == 0) {
                e = c[a >> 2] | 0;
                f = c[e >> 2] | 0;
                g = c[e + 4 >> 2] | 0;
                i = f + 4 | 0;
                j = c[i >> 2] | 0;
                k = g + (j << 3) | 0;
                l = c[k >> 2] | 0;
                m = e + 8 | 0;
                n = c[m >> 2] | 0;
                if ((n | 0) <= 0) {
                    o = l;
                    return o | 0
                }
                p = c[f + (n << 2) >> 2] | 0;
                c[i >> 2] = p;
                c[g + (p << 3) + 4 >> 2] = 1;
                c[k >> 2] = 0;
                k = e + 16 | 0;
                c[g + (j << 3) + 4 >> 2] = c[k >> 2];
                c[k >> 2] = j;
                j = (c[m >> 2] | 0) - 1 | 0;
                c[m >> 2] = j;
                if ((j | 0) <= 0) {
                    o = l;
                    return o | 0
                }
                bA(e, 1);
                o = l;
                return o | 0
            }
            l = c[a + 8 >> 2] | 0;
            e = c[c[l + (d - 1 << 2) >> 2] >> 2] | 0;
            j = c[a >> 2] | 0;
            a = j + 8 | 0;
            m = c[a >> 2] | 0;
            do {
                if ((m | 0) == 0) {
                    q = d
                } else {
                    k = c[j >> 2] | 0;
                    g = k + 4 | 0;
                    p = c[g >> 2] | 0;
                    i = c[j + 4 >> 2] | 0;
                    n = i + (p << 3) | 0;
                    f = c[n >> 2] | 0;
                    r = +h[f + 40 >> 3];
                    s = +h[e + 40 >> 3];
                    if (r >= s) {
                        if (r != s) {
                            q = d;
                            break
                        }
                        if (+h[f + 48 >> 3] > +h[e + 48 >> 3]) {
                            q = d;
                            break
                        }
                    }
                    if ((m | 0) <= 0) {
                        o = f;
                        return o | 0
                    }
                    t = c[k + (m << 2) >> 2] | 0;
                    c[g >> 2] = t;
                    c[i + (t << 3) + 4 >> 2] = 1;
                    c[n >> 2] = 0;
                    n = j + 16 | 0;
                    c[i + (p << 3) + 4 >> 2] = c[n >> 2];
                    c[n >> 2] = p;
                    p = (c[a >> 2] | 0) - 1 | 0;
                    c[a >> 2] = p;
                    if ((p | 0) <= 0) {
                        o = f;
                        return o | 0
                    }
                    bA(j, 1);
                    o = f;
                    return o | 0
                }
            } while (0);
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                j = q - 1 | 0;
                c[b >> 2] = j;
                if ((j | 0) <= 0) {
                    o = e;
                    u = 850;
                    break
                }
                if ((c[c[l + (q - 2 << 2) >> 2] >> 2] | 0) == 0) {
                    q = j
                } else {
                    o = e;
                    u = 852;
                    break
                }
            }
            if ((u | 0) == 850) {
                return o | 0
            } else if ((u | 0) == 852) {
                return o | 0
            }
            return 0
        }

        function bF(a) {
            a = a | 0;
            var b = 0,
                d = 0;
            if ((a | 0) == 0) {
                ay(672, 78, 2304, 576)
            }
            b = c[a >> 2] | 0;
            if ((b | 0) != 0) {
                b1(c[b + 4 >> 2] | 0);
                b1(c[b >> 2] | 0);
                b1(b)
            }
            b = c[a + 8 >> 2] | 0;
            if ((b | 0) != 0) {
                b1(b)
            }
            b = c[a + 4 >> 2] | 0;
            if ((b | 0) == 0) {
                d = a;
                b1(d);
                return
            }
            b1(b);
            d = a;
            b1(d);
            return
        }

        function bG(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0.0,
                G = 0,
                H = 0.0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0.0,
                P = 0,
                Q = 0,
                R = 0,
                S = 0,
                T = 0.0,
                U = 0,
                V = 0,
                W = 0,
                X = 0,
                Y = 0,
                Z = 0,
                $ = 0,
                aa = 0,
                ab = 0,
                ac = 0;
            b = i;
            i = i + 400 | 0;
            d = b | 0;
            e = d | 0;
            f = a + 12 | 0;
            g = b0((c[f >> 2] << 2) + 4 | 0) | 0;
            j = g;
            k = a + 8 | 0;
            c[k >> 2] = j;
            if ((g | 0) == 0) {
                l = 0;
                i = b;
                return l | 0
            }
            g = j + ((c[f >> 2] | 0) - 1 << 2) | 0;
            if (j >>> 0 <= g >>> 0) {
                m = j;
                n = c[a + 4 >> 2] | 0;
                var tessellationLimit4 = 0;
                while (1) {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    c[m >> 2] = n;
                    o = m + 4 | 0;
                    if (o >>> 0 > g >>> 0) {
                        break
                    } else {
                        m = o;
                        n = n + 4 | 0
                    }
                }
            }
            c[d >> 2] = j;
            c[d + 4 >> 2] = g;
            g = d + 8 | 0;
            d = 2016473283;
            j = e;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                n = c[j >> 2] | 0;
                m = c[g - 8 + 4 >> 2] | 0;
                L1177: do {
                    if (m >>> 0 > (n + 40 | 0) >>> 0) {
                        o = m;
                        p = j;
                        q = d;
                        r = n;
                        var tessellationLimit6 = 0;
                        while (1) {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            s = o;
                            t = o + 4 | 0;
                            u = p;
                            v = q;
                            w = r;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                x = (_(v, 1539415821) | 0) + 1 | 0;
                                y = w;
                                z = w + ((x >>> 0) % (((s - y >> 2) + 1 | 0) >>> 0) >>> 0 << 2) | 0;
                                A = c[z >> 2] | 0;
                                c[z >> 2] = c[w >> 2];
                                c[w >> 2] = A;
                                z = t;
                                B = w - 4 | 0;
                                var tessellationLimit8 = 0;
                                while (1) {
                                    if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    C = B + 4 | 0;
                                    D = c[C >> 2] | 0;
                                    E = c[D >> 2] | 0;
                                    F = +h[E + 40 >> 3];
                                    G = c[A >> 2] | 0;
                                    H = +h[G + 40 >> 3];
                                    L1184: do {
                                        if (F < H) {
                                            I = B;
                                            J = C;
                                            K = D
                                        } else {
                                            L = B;
                                            M = C;
                                            N = E;
                                            O = F;
                                            P = D;
                                            var tessellationLimit11 = 0;
                                            while (1) {
                                                if (++tessellationLimit11 > TESSELLATION_LIMIT) {
                                                    throw new Error("Tessellate limit exceeded");
                                                }
                                                if (O == H) {
                                                    if (+h[N + 48 >> 3] <= +h[G + 48 >> 3]) {
                                                        I = L;
                                                        J = M;
                                                        K = P;
                                                        break L1184
                                                    }
                                                }
                                                Q = M + 4 | 0;
                                                R = c[Q >> 2] | 0;
                                                S = c[R >> 2] | 0;
                                                T = +h[S + 40 >> 3];
                                                if (T < H) {
                                                    I = M;
                                                    J = Q;
                                                    K = R;
                                                    break
                                                } else {
                                                    L = M;
                                                    M = Q;
                                                    N = S;
                                                    O = T;
                                                    P = R
                                                }
                                            }
                                        }
                                    } while (0);
                                    D = z - 4 | 0;
                                    E = c[D >> 2] | 0;
                                    C = c[E >> 2] | 0;
                                    F = +h[C + 40 >> 3];
                                    L1191: do {
                                        if (H < F) {
                                            U = z;
                                            V = D;
                                            W = E
                                        } else {
                                            P = z;
                                            N = D;
                                            M = C;
                                            O = F;
                                            L = E;
                                            var tessellationLimit11 = 0;
                                            while (1) {
                                                if (++tessellationLimit11 > TESSELLATION_LIMIT) {
                                                    throw new Error("Tessellate limit exceeded");
                                                }
                                                if (H == O) {
                                                    if (+h[G + 48 >> 3] <= +h[M + 48 >> 3]) {
                                                        U = P;
                                                        V = N;
                                                        W = L;
                                                        break L1191
                                                    }
                                                }
                                                R = N - 4 | 0;
                                                S = c[R >> 2] | 0;
                                                Q = c[S >> 2] | 0;
                                                T = +h[Q + 40 >> 3];
                                                if (H < T) {
                                                    U = N;
                                                    V = R;
                                                    W = S;
                                                    break
                                                } else {
                                                    P = N;
                                                    N = R;
                                                    M = Q;
                                                    O = T;
                                                    L = S
                                                }
                                            }
                                        }
                                    } while (0);
                                    c[J >> 2] = W;
                                    c[V >> 2] = K;
                                    if (J >>> 0 < V >>> 0) {
                                        z = V;
                                        B = J
                                    } else {
                                        break
                                    }
                                }
                                B = c[J >> 2] | 0;
                                c[J >> 2] = K;
                                c[V >> 2] = B;
                                X = u | 0;
                                if ((J - y | 0) < (s - V | 0)) {
                                    break
                                }
                                c[X >> 2] = w;
                                c[u + 4 >> 2] = I;
                                B = u + 8 | 0;
                                if (o >>> 0 > (U + 40 | 0) >>> 0) {
                                    u = B;
                                    v = x;
                                    w = U
                                } else {
                                    Y = B;
                                    Z = x;
                                    $ = U;
                                    aa = o;
                                    break L1177
                                }
                            }
                            c[X >> 2] = U;
                            c[u + 4 >> 2] = o;
                            v = u + 8 | 0;
                            if (I >>> 0 > (w + 40 | 0) >>> 0) {
                                o = I;
                                p = v;
                                q = x;
                                r = w
                            } else {
                                Y = v;
                                Z = x;
                                $ = w;
                                aa = I;
                                break
                            }
                        }
                    } else {
                        Y = j;
                        Z = d;
                        $ = n;
                        aa = m
                    }
                } while (0);
                m = $ + 4 | 0;
                if (m >>> 0 <= aa >>> 0) {
                    n = m;
                    var tessellationLimit5 = 0;
                    do {
                        if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        m = c[n >> 2] | 0;
                        L1205: do {
                            if (n >>> 0 > $ >>> 0) {
                                r = n;
                                var tessellationLimit9 = 0;
                                while (1) {
                                    if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    q = c[m >> 2] | 0;
                                    H = +h[q + 40 >> 3];
                                    p = r - 4 | 0;
                                    o = c[p >> 2] | 0;
                                    v = c[o >> 2] | 0;
                                    F = +h[v + 40 >> 3];
                                    if (H < F) {
                                        ab = r;
                                        break L1205
                                    }
                                    if (H == F) {
                                        if (+h[q + 48 >> 3] <= +h[v + 48 >> 3]) {
                                            ab = r;
                                            break L1205
                                        }
                                    }
                                    c[r >> 2] = o;
                                    if (p >>> 0 > $ >>> 0) {
                                        r = p
                                    } else {
                                        ab = p;
                                        break
                                    }
                                }
                            } else {
                                ab = n
                            }
                        } while (0);
                        c[ab >> 2] = m;
                        n = n + 4 | 0;
                    } while (n >>> 0 <= aa >>> 0)
                }
                n = Y - 8 | 0;
                if (n >>> 0 < e >>> 0) {
                    break
                } else {
                    g = Y;
                    d = Z;
                    j = n
                }
            }
            c[a + 16 >> 2] = c[f >> 2];
            c[a + 20 >> 2] = 1;
            j = c[a >> 2] | 0;
            a = c[j + 8 >> 2] | 0;
            if ((a | 0) > 0) {
                Z = a;
                do {
                    bA(j, Z);
                    Z = Z - 1 | 0;
                } while ((Z | 0) > 0)
            }
            c[j + 20 >> 2] = 1;
            j = c[k >> 2] | 0;
            k = (c[f >> 2] | 0) - 1 | 0;
            f = j + (k << 2) | 0;
            if ((k | 0) <= 0) {
                l = 1;
                i = b;
                return l | 0
            }
            k = c[c[j >> 2] >> 2] | 0;
            Z = j;
            j = k;
            F = +h[k + 40 >> 3];
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                k = Z + 4 | 0;
                a = c[c[k >> 2] >> 2] | 0;
                H = +h[a + 40 >> 3];
                if (H >= F) {
                    if (H != F) {
                        ac = 904;
                        break
                    }
                    if (+h[a + 48 >> 3] > +h[j + 48 >> 3]) {
                        ac = 905;
                        break
                    }
                }
                if (k >>> 0 < f >>> 0) {
                    Z = k;
                    j = a;
                    F = H
                } else {
                    l = 1;
                    ac = 908;
                    break
                }
            }
            if ((ac | 0) == 904) {
                ay(672, 164, 2288, 488);
                return 0
            } else if ((ac | 0) == 905) {
                ay(672, 164, 2288, 488);
                return 0
            } else if ((ac | 0) == 908) {
                i = b;
                return l | 0
            }
            return 0
        }

        function bH(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0,
                P = 0,
                Q = 0,
                R = 0,
                S = 0.0,
                T = 0.0,
                U = 0,
                V = 0,
                W = 0,
                X = 0;
            b = i;
            i = i + 80 | 0;
            d = b | 0;
            e = b + 24 | 0;
            f = b + 48 | 0;
            g = b + 64 | 0;
            c[a + 100 >> 2] = 0;
            j = a + 8 | 0;
            k = (c[j >> 2] | 0) + 92 | 0;
            l = c[k >> 2] | 0;
            L1233: do {
                if ((l | 0) != (k | 0)) {
                    m = f;
                    n = g;
                    o = f | 0;
                    p = f + 4 | 0;
                    q = g | 0;
                    r = e | 0;
                    s = e + 8 | 0;
                    t = e + 16 | 0;
                    u = a + 3380 | 0;
                    v = a + 116 | 0;
                    w = a + 3424 | 0;
                    x = l;
                    L1235: {
                        var tessellationLimit5 = 0;
                        while (1) {
                            if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            y = c[x >> 2] | 0;
                            z = c[x + 12 >> 2] | 0;
                            A = x + 16 | 0;
                            B = c[A >> 2] | 0;
                            C = c[(c[x + 4 >> 2] | 0) + 16 >> 2] | 0;
                            do {
                                if (+h[B + 40 >> 3] == +h[C + 40 >> 3]) {
                                    if (+h[B + 48 >> 3] != +h[C + 48 >> 3]) {
                                        D = x;
                                        E = z;
                                        break
                                    }
                                    F = z + 12 | 0;
                                    if ((c[F >> 2] | 0) == (x | 0)) {
                                        D = x;
                                        E = z;
                                        break
                                    }
                                    b9(m | 0, 0, 16);
                                    c[n >> 2] = c[684];
                                    c[n + 4 >> 2] = c[685];
                                    c[n + 8 >> 2] = c[686];
                                    c[n + 12 >> 2] = c[687];
                                    G = c[z + 16 >> 2] | 0;
                                    H = G + 12 | 0;
                                    c[o >> 2] = c[H >> 2];
                                    c[p >> 2] = c[(c[A >> 2] | 0) + 12 >> 2];
                                    h[r >> 3] = +h[G + 16 >> 3];
                                    h[s >> 3] = +h[G + 24 >> 3];
                                    h[t >> 3] = +h[G + 32 >> 3];
                                    c[H >> 2] = 0;
                                    G = c[u >> 2] | 0;
                                    if ((G | 0) == 4) {
                                        aO[c[v >> 2] & 3](r, o, q, H)
                                    } else {
                                        aG[G & 7](r, o, q, H, c[w >> 2] | 0)
                                    }
                                    if ((c[H >> 2] | 0) == 0) {
                                        c[H >> 2] = c[o >> 2]
                                    }
                                    if ((a4(z, x) | 0) == 0) {
                                        I = 920;
                                        break L1235
                                    }
                                    if ((a5(x) | 0) == 0) {
                                        I = 922;
                                        break L1235
                                    }
                                    D = z;
                                    E = c[F >> 2] | 0
                                } else {
                                    D = x;
                                    E = z
                                }
                            } while (0);
                            if ((c[E + 12 >> 2] | 0) == (D | 0)) {
                                if ((E | 0) == (D | 0)) {
                                    J = y
                                } else {
                                    if ((E | 0) == (y | 0)) {
                                        I = 928
                                    } else {
                                        if ((E | 0) == (c[y + 4 >> 2] | 0)) {
                                            I = 928
                                        } else {
                                            K = y
                                        }
                                    }
                                    if ((I | 0) == 928) {
                                        I = 0;
                                        K = c[y >> 2] | 0
                                    }
                                    if ((a5(E) | 0) == 0) {
                                        I = 930;
                                        break
                                    } else {
                                        J = K
                                    }
                                }
                                if ((D | 0) == (J | 0)) {
                                    I = 933
                                } else {
                                    if ((D | 0) == (c[J + 4 >> 2] | 0)) {
                                        I = 933
                                    } else {
                                        L = J
                                    }
                                }
                                if ((I | 0) == 933) {
                                    I = 0;
                                    L = c[J >> 2] | 0
                                }
                                if ((a5(D) | 0) == 0) {
                                    I = 936;
                                    break
                                } else {
                                    M = L
                                }
                            } else {
                                M = y
                            }
                            if ((M | 0) == (k | 0)) {
                                break L1233
                            } else {
                                x = M
                            }
                        }
                    }
                    if ((I | 0) == 920) {
                        aw(a + 3384 | 0, 1);
                        return 0
                    } else if ((I | 0) == 922) {
                        aw(a + 3384 | 0, 1);
                        return 0
                    } else if ((I | 0) == 930) {
                        aw(a + 3384 | 0, 1);
                        return 0
                    } else if ((I | 0) == 936) {
                        aw(a + 3384 | 0, 1);
                        return 0
                    }
                }
            } while (0);
            M = bD(4) | 0;
            k = a + 108 | 0;
            c[k >> 2] = M;
            if ((M | 0) == 0) {
                N = 0;
                i = b;
                return N | 0
            }
            L = c[j >> 2] | 0;
            D = L | 0;
            J = M + 20 | 0;
            K = M + 12 | 0;
            E = M + 16 | 0;
            l = M + 4 | 0;
            e = M | 0;
            x = L | 0;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                O = c[x >> 2] | 0;
                if ((O | 0) == (D | 0)) {
                    I = 949;
                    break
                }
                L = O;
                if ((c[J >> 2] | 0) == 0) {
                    o = c[K >> 2] | 0;
                    w = o + 1 | 0;
                    c[K >> 2] = w;
                    q = c[E >> 2] | 0;
                    if ((w | 0) >= (q | 0)) {
                        P = c[l >> 2] | 0;
                        c[E >> 2] = q << 1;
                        w = b2(P, q << 3) | 0;
                        c[l >> 2] = w;
                        if ((w | 0) == 0) {
                            I = 944;
                            break
                        }
                    }
                    if ((o | 0) == 2147483647) {
                        I = 946;
                        break
                    }
                    c[(c[l >> 2] | 0) + (o << 2) >> 2] = L;
                    Q = o ^ -1
                } else {
                    Q = bB(c[e >> 2] | 0, L) | 0
                }
                c[O + 56 >> 2] = Q;
                if ((Q | 0) == 2147483647) {
                    break
                } else {
                    x = O | 0
                }
            }
            do {
                if ((I | 0) == 949) {
                    if ((bG(M) | 0) == 0) {
                        break
                    }
                    x = b0(20) | 0;
                    if ((x | 0) == 0) {
                        c[a + 104 >> 2] = 0;
                        aw(a + 3384 | 0, 1);
                        return 0
                    }
                    Q = x;
                    c[x >> 2] = 0;
                    c[x + 4 >> 2] = Q;
                    c[x + 8 >> 2] = Q;
                    c[x + 12 >> 2] = a;
                    c[x + 16 >> 2] = 2;
                    Q = a + 104 | 0;
                    c[Q >> 2] = x;
                    bR(a, -4.0e+150);
                    bR(a, 4.0e+150);
                    x = bE(c[k >> 2] | 0) | 0;
                    L1293: do {
                        if ((x | 0) != 0) {
                            e = f;
                            E = g;
                            K = f | 0;
                            J = f + 4 | 0;
                            D = g | 0;
                            L = d | 0;
                            o = d + 8 | 0;
                            w = d + 16 | 0;
                            q = a + 3380 | 0;
                            r = a + 116 | 0;
                            v = a + 3424 | 0;
                            u = x;
                            L1295: {
                                var tessellationLimit7 = 0;
                                while (1) {
                                    if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    t = u;
                                    s = u + 40 | 0;
                                    p = u + 48 | 0;
                                    n = u + 8 | 0;
                                    var tessellationLimit8 = 0;
                                    while (1) {
                                        if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                            throw new Error("Tessellate limit exceeded");
                                        }
                                        m = c[k >> 2] | 0;
                                        z = c[m + 12 >> 2] | 0;
                                        L1299: do {
                                            if ((z | 0) == 0) {
                                                A = c[m >> 2] | 0;
                                                R = c[(c[A + 4 >> 2] | 0) + (c[(c[A >> 2] | 0) + 4 >> 2] << 3) >> 2] | 0
                                            } else {
                                                A = c[c[(c[m + 8 >> 2] | 0) + (z - 1 << 2) >> 2] >> 2] | 0;
                                                C = c[m >> 2] | 0;
                                                do {
                                                    if ((c[C + 8 >> 2] | 0) != 0) {
                                                        B = c[(c[C + 4 >> 2] | 0) + (c[(c[C >> 2] | 0) + 4 >> 2] << 3) >> 2] | 0;
                                                        S = +h[B + 40 >> 3];
                                                        T = +h[A + 40 >> 3];
                                                        if (S < T) {
                                                            R = B;
                                                            break L1299
                                                        }
                                                        if (S != T) {
                                                            break
                                                        }
                                                        if (+h[B + 48 >> 3] <= +h[A + 48 >> 3]) {
                                                            R = B;
                                                            break L1299
                                                        }
                                                    }
                                                } while (0);
                                                R = A
                                            }
                                        } while (0);
                                        if ((R | 0) == 0) {
                                            break
                                        }
                                        if (+h[R + 40 >> 3] != +h[s >> 3]) {
                                            break
                                        }
                                        if (+h[R + 48 >> 3] != +h[p >> 3]) {
                                            break
                                        }
                                        z = bE(m) | 0;
                                        C = c[n >> 2] | 0;
                                        B = c[z + 8 >> 2] | 0;
                                        b9(e | 0, 0, 16);
                                        c[E >> 2] = c[684];
                                        c[E + 4 >> 2] = c[685];
                                        c[E + 8 >> 2] = c[686];
                                        c[E + 12 >> 2] = c[687];
                                        z = c[C + 16 >> 2] | 0;
                                        F = z + 12 | 0;
                                        c[K >> 2] = c[F >> 2];
                                        c[J >> 2] = c[(c[B + 16 >> 2] | 0) + 12 >> 2];
                                        h[L >> 3] = +h[z + 16 >> 3];
                                        h[o >> 3] = +h[z + 24 >> 3];
                                        h[w >> 3] = +h[z + 32 >> 3];
                                        c[F >> 2] = 0;
                                        z = c[q >> 2] | 0;
                                        if ((z | 0) == 4) {
                                            aO[c[r >> 2] & 3](L, K, D, F)
                                        } else {
                                            aG[z & 7](L, K, D, F, c[v >> 2] | 0)
                                        }
                                        if ((c[F >> 2] | 0) == 0) {
                                            c[F >> 2] = c[K >> 2]
                                        }
                                        if ((a4(C, B) | 0) == 0) {
                                            break L1295
                                        }
                                    }
                                    bI(a, t);
                                    u = bE(c[k >> 2] | 0) | 0;
                                    if ((u | 0) == 0) {
                                        break L1293
                                    }
                                }
                                aw(a + 3384 | 0, 1);
                                return 0
                            }
                        }
                    } while (0);
                    x = c[Q >> 2] | 0;
                    u = x + 4 | 0;
                    c[a + 112 >> 2] = c[(c[c[c[u >> 2] >> 2] >> 2] | 0) + 16 >> 2];
                    K = c[u >> 2] | 0;
                    u = c[K >> 2] | 0;
                    L1321: do {
                        if ((u | 0) == 0) {
                            U = x;
                            V = K
                        } else {
                            v = 0;
                            D = u;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                if ((c[D + 16 >> 2] | 0) == 0) {
                                    if ((c[D + 24 >> 2] | 0) == 0) {
                                        I = 977;
                                        break
                                    }
                                    if ((v | 0) == 0) {
                                        W = v + 1 | 0
                                    } else {
                                        I = 979;
                                        break
                                    }
                                } else {
                                    W = v
                                }
                                if ((c[D + 8 >> 2] | 0) != 0) {
                                    I = 981;
                                    break
                                }
                                L = c[D >> 2] | 0;
                                if ((c[D + 24 >> 2] | 0) != 0) {
                                    if ((c[L + 28 >> 2] | 0) != 0) {
                                        I = 984;
                                        break
                                    }
                                }
                                c[L + 24 >> 2] = 0;
                                L = c[D + 4 >> 2] | 0;
                                r = L + 8 | 0;
                                q = L + 4 | 0;
                                c[(c[q >> 2] | 0) + 8 >> 2] = c[r >> 2];
                                c[(c[r >> 2] | 0) + 4 >> 2] = c[q >> 2];
                                b1(L);
                                b1(D);
                                L = c[Q >> 2] | 0;
                                q = c[L + 4 >> 2] | 0;
                                r = c[q >> 2] | 0;
                                if ((r | 0) == 0) {
                                    U = L;
                                    V = q;
                                    break L1321
                                } else {
                                    v = W;
                                    D = r
                                }
                            }
                            if ((I | 0) == 979) {
                                ay(512, 1189, 2616, 648);
                                return 0
                            } else if ((I | 0) == 981) {
                                ay(512, 1191, 2616, 552);
                                return 0
                            } else if ((I | 0) == 977) {
                                ay(512, 1188, 2616, 752);
                                return 0
                            } else if ((I | 0) == 984) {
                                ay(512, 158, 2632, 464);
                                return 0
                            }
                        }
                    } while (0);
                    Q = U | 0;
                    if ((V | 0) != (Q | 0)) {
                        u = V;
                        var tessellationLimit6 = 0;
                        while (1) {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            K = c[u + 4 >> 2] | 0;
                            b1(u);
                            if ((K | 0) == (Q | 0)) {
                                break
                            } else {
                                u = K
                            }
                        }
                    }
                    b1(U);
                    bF(c[k >> 2] | 0);
                    u = c[j >> 2] | 0;
                    Q = u + 64 | 0;
                    K = c[Q >> 2] | 0;
                    do {
                        if ((K | 0) == (Q | 0)) {
                            X = u
                        } else {
                            x = K;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                D = c[x >> 2] | 0;
                                v = c[x + 8 >> 2] | 0;
                                r = c[v + 12 >> 2] | 0;
                                if ((r | 0) == (v | 0)) {
                                    I = 990;
                                    break
                                }
                                if ((c[r + 12 >> 2] | 0) == (v | 0)) {
                                    r = v + 8 | 0;
                                    q = (c[r >> 2] | 0) + 28 | 0;
                                    c[q >> 2] = (c[q >> 2] | 0) + (c[v + 28 >> 2] | 0);
                                    q = (c[(c[r >> 2] | 0) + 4 >> 2] | 0) + 28 | 0;
                                    c[q >> 2] = (c[q >> 2] | 0) + (c[(c[v + 4 >> 2] | 0) + 28 >> 2] | 0);
                                    if ((a5(v) | 0) == 0) {
                                        N = 0;
                                        I = 999;
                                        break
                                    }
                                }
                                if ((D | 0) == (Q | 0)) {
                                    I = 994;
                                    break
                                } else {
                                    x = D
                                }
                            }
                            if ((I | 0) == 990) {
                                ay(512, 1290, 2544, 1112);
                                return 0
                            } else if ((I | 0) == 999) {
                                i = b;
                                return N | 0
                            } else if ((I | 0) == 994) {
                                X = c[j >> 2] | 0;
                                break
                            }
                        }
                    } while (0);
                    a9(X);
                    N = 1;
                    i = b;
                    return N | 0
                } else if ((I | 0) == 946) {
                    ay(672, 194, 2264, 424);
                    return 0
                } else if ((I | 0) == 944) {
                    c[l >> 2] = P;
                    c[O + 56 >> 2] = 2147483647
                }
            } while (0);
            bF(c[k >> 2] | 0);
            c[k >> 2] = 0;
            N = 0;
            i = b;
            return N | 0
        }

        function bI(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0.0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0.0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0,
                P = 0,
                Q = 0.0,
                R = 0.0,
                S = 0.0,
                T = 0,
                U = 0,
                V = 0;
            d = i;
            i = i + 32 | 0;
            e = d | 0;
            f = a + 112 | 0;
            c[f >> 2] = b;
            g = b + 8 | 0;
            j = c[g >> 2] | 0;
            k = j;
            var tessellationLimit3 = 0;
            do {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                l = c[k + 24 >> 2] | 0;
                if ((l | 0) != 0) {
                    m = 1052;
                    break
                }
                k = c[k + 8 >> 2] | 0;
            } while ((k | 0) != (j | 0));
            if ((m | 0) == 1052) {
                k = c[(c[l >> 2] | 0) + 16 >> 2] | 0;
                n = l;
                var tessellationLimit4 = 0;
                do {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    o = c[c[(c[n + 4 >> 2] | 0) + 4 >> 2] >> 2] | 0;
                    n = o;
                    p = o;
                    q = c[p >> 2] | 0;
                } while ((c[q + 16 >> 2] | 0) == (k | 0));
                k = o + 24 | 0;
                do {
                    if ((c[k >> 2] | 0) == 0) {
                        r = n
                    } else {
                        l = o + 4 | 0;
                        s = a7(c[(c[c[c[(c[l >> 2] | 0) + 8 >> 2] >> 2] >> 2] | 0) + 4 >> 2] | 0, c[q + 12 >> 2] | 0) | 0;
                        if ((s | 0) == 0) {
                            t = a + 3384 | 0;
                            aw(t | 0, 1)
                        }
                        if ((c[k >> 2] | 0) == 0) {
                            ay(512, 171, 2600, 752)
                        }
                        if ((a5(c[p >> 2] | 0) | 0) == 0) {
                            t = a + 3384 | 0;
                            aw(t | 0, 1)
                        } else {
                            c[k >> 2] = 0;
                            c[p >> 2] = s;
                            c[s + 24 >> 2] = n;
                            r = c[c[(c[l >> 2] | 0) + 4 >> 2] >> 2] | 0;
                            break
                        }
                    }
                } while (0);
                if ((r | 0) == 0) {
                    t = a + 3384 | 0;
                    aw(t | 0, 1)
                }
                t = r + 4 | 0;
                n = c[c[(c[t >> 2] | 0) + 8 >> 2] >> 2] | 0;
                p = c[n >> 2] | 0;
                k = bJ(a, n, 0) | 0;
                n = k + 8 | 0;
                q = c[n >> 2] | 0;
                if ((q | 0) != (p | 0)) {
                    bK(a, r, q, p, p, 1);
                    i = d;
                    return
                }
                q = c[c[(c[t >> 2] | 0) + 8 >> 2] >> 2] | 0;
                t = q;
                o = r | 0;
                l = c[o >> 2] | 0;
                s = c[q >> 2] | 0;
                q = s + 4 | 0;
                if ((c[(c[l + 4 >> 2] | 0) + 16 >> 2] | 0) != (c[(c[q >> 2] | 0) + 16 >> 2] | 0)) {
                    bN(a, r) | 0
                }
                u = l + 16 | 0;
                v = c[u >> 2] | 0;
                w = c[f >> 2] | 0;
                x = +h[w + 40 >> 3];
                do {
                    if (+h[v + 40 >> 3] == x) {
                        if (+h[v + 48 >> 3] != +h[w + 48 >> 3]) {
                            y = 0;
                            z = r;
                            A = p;
                            B = w;
                            C = x;
                            break
                        }
                        if ((a4(c[(c[p + 4 >> 2] | 0) + 12 >> 2] | 0, l) | 0) == 0) {
                            aw(a + 3384 | 0, 1)
                        }
                        D = c[(c[o >> 2] | 0) + 16 >> 2] | 0;
                        E = r;
                        var tessellationLimit6 = 0;
                        do {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            F = c[c[(c[E + 4 >> 2] | 0) + 4 >> 2] >> 2] | 0;
                            E = F;
                            G = F;
                            H = c[G >> 2] | 0;
                        } while ((c[H + 16 >> 2] | 0) == (D | 0));
                        D = F + 24 | 0;
                        do {
                            if ((c[D >> 2] | 0) == 0) {
                                I = E
                            } else {
                                J = F + 4 | 0;
                                K = a7(c[(c[c[c[(c[J >> 2] | 0) + 8 >> 2] >> 2] >> 2] | 0) + 4 >> 2] | 0, c[H + 12 >> 2] | 0) | 0;
                                if ((K | 0) == 0) {
                                    L = a + 3384 | 0;
                                    aw(L | 0, 1)
                                }
                                if ((c[D >> 2] | 0) == 0) {
                                    ay(512, 171, 2600, 752)
                                }
                                if ((a5(c[G >> 2] | 0) | 0) == 0) {
                                    L = a + 3384 | 0;
                                    aw(L | 0, 1)
                                } else {
                                    c[D >> 2] = 0;
                                    c[G >> 2] = K;
                                    c[K + 24 >> 2] = E;
                                    I = c[c[(c[J >> 2] | 0) + 4 >> 2] >> 2] | 0;
                                    break
                                }
                            }
                        } while (0);
                        if ((I | 0) == 0) {
                            L = a + 3384 | 0;
                            aw(L | 0, 1)
                        } else {
                            E = c[c[(c[I + 4 >> 2] | 0) + 8 >> 2] >> 2] | 0;
                            D = E;
                            J = c[E >> 2] | 0;
                            bJ(a, D, t) | 0;
                            D = c[f >> 2] | 0;
                            y = 1;
                            z = I;
                            A = J;
                            B = D;
                            C = +h[D + 40 >> 3];
                            break
                        }
                    } else {
                        y = 0;
                        z = r;
                        A = p;
                        B = w;
                        C = x
                    }
                } while (0);
                w = c[s + 16 >> 2] | 0;
                x = +h[w + 40 >> 3];
                do {
                    if (x == C) {
                        if (+h[w + 48 >> 3] != +h[B + 48 >> 3]) {
                            m = 1085;
                            break
                        }
                        if ((a4(k, c[(c[q >> 2] | 0) + 12 >> 2] | 0) | 0) == 0) {
                            aw(a + 3384 | 0, 1)
                        } else {
                            M = bJ(a, t, 0) | 0;
                            break
                        }
                    } else {
                        m = 1085
                    }
                } while (0);
                do {
                    if ((m | 0) == 1085) {
                        if ((y | 0) != 0) {
                            M = k;
                            break
                        }
                        t = c[u >> 2] | 0;
                        C = +h[t + 40 >> 3];
                        do {
                            if (x < C) {
                                m = 1090
                            } else {
                                if (x != C) {
                                    N = l;
                                    break
                                }
                                if (+h[w + 48 >> 3] > +h[t + 48 >> 3]) {
                                    N = l
                                } else {
                                    m = 1090
                                }
                            }
                        } while (0);
                        if ((m | 0) == 1090) {
                            N = c[(c[q >> 2] | 0) + 12 >> 2] | 0
                        }
                        t = a7(c[(c[n >> 2] | 0) + 4 >> 2] | 0, N) | 0;
                        if ((t | 0) == 0) {
                            aw(a + 3384 | 0, 1)
                        }
                        B = c[t + 8 >> 2] | 0;
                        bK(a, z, t, B, B, 0);
                        c[(c[(c[t + 4 >> 2] | 0) + 24 >> 2] | 0) + 24 >> 2] = 1;
                        bM(a, z);
                        i = d;
                        return
                    }
                } while (0);
                bK(a, z, c[M + 8 >> 2] | 0, A, A, 1);
                i = d;
                return
            }
            A = e;
            c[e >> 2] = c[j + 4 >> 2];
            j = a + 104 | 0;
            e = c[j >> 2] | 0;
            M = e + 16 | 0;
            z = e + 12 | 0;
            N = e | 0;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                e = c[N + 4 >> 2] | 0;
                O = e | 0;
                n = c[O >> 2] | 0;
                if ((n | 0) == 0) {
                    P = 0;
                    break
                }
                if ((aK[c[M >> 2] & 3](c[z >> 2] | 0, A, n) | 0) == 0) {
                    N = e
                } else {
                    m = 1007;
                    break
                }
            }
            if ((m | 0) == 1007) {
                P = c[O >> 2] | 0
            }
            O = P;
            m = P + 4 | 0;
            N = c[c[(c[m >> 2] | 0) + 8 >> 2] >> 2] | 0;
            A = N;
            z = c[P >> 2] | 0;
            M = c[N >> 2] | 0;
            N = c[(c[z + 4 >> 2] | 0) + 16 >> 2] | 0;
            e = c[z + 16 >> 2] | 0;
            x = +h[N + 40 >> 3];
            C = +h[b + 40 >> 3];
            do {
                if (x >= C) {
                    if (x != C) {
                        ay(688, 85, 2464, 1384)
                    }
                    if (+h[N + 48 >> 3] <= +h[b + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384)
                }
            } while (0);
            Q = +h[e + 40 >> 3];
            do {
                if (C >= Q) {
                    if (C != Q) {
                        ay(688, 85, 2464, 1384)
                    }
                    if (+h[b + 48 >> 3] <= +h[e + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384)
                }
            } while (0);
            R = C - x;
            S = Q - C;
            do {
                if (R + S > 0.0) {
                    C = +h[b + 48 >> 3];
                    Q = +h[N + 48 >> 3];
                    if (R * (C - +h[e + 48 >> 3]) + S * (C - Q) == 0.0) {
                        break
                    }
                    n = c[M + 4 >> 2] | 0;
                    q = c[n + 16 >> 2] | 0;
                    C = +h[q + 40 >> 3];
                    do {
                        if (C < x) {
                            T = O
                        } else {
                            if (C == x) {
                                if (+h[q + 48 >> 3] <= Q) {
                                    T = O;
                                    break
                                }
                            }
                            T = A
                        }
                    } while (0);
                    do {
                        if ((c[P + 12 >> 2] | 0) == 0) {
                            if ((c[T + 24 >> 2] | 0) != 0) {
                                break
                            }
                            q = c[g >> 2] | 0;
                            bK(a, O, q, q, 0, 1);
                            i = d;
                            return
                        }
                    } while (0);
                    do {
                        if ((T | 0) == (O | 0)) {
                            q = a7(c[(c[g >> 2] | 0) + 4 >> 2] | 0, c[z + 12 >> 2] | 0) | 0;
                            if ((q | 0) != 0) {
                                U = q;
                                break
                            }
                            aw(a + 3384 | 0, 1)
                        } else {
                            q = a7(c[(c[n + 8 >> 2] | 0) + 4 >> 2] | 0, c[g >> 2] | 0) | 0;
                            if ((q | 0) == 0) {
                                aw(a + 3384 | 0, 1)
                            } else {
                                U = c[q + 4 >> 2] | 0;
                                break
                            }
                        }
                    } while (0);
                    n = T + 24 | 0;
                    do {
                        if ((c[n >> 2] | 0) == 0) {
                            q = b0(28) | 0;
                            l = q;
                            if ((q | 0) == 0) {
                                aw(a + 3384 | 0, 1)
                            }
                            w = q;
                            c[w >> 2] = U;
                            u = c[j >> 2] | 0;
                            k = u + 16 | 0;
                            y = u + 12 | 0;
                            u = c[m >> 2] | 0;
                            var tessellationLimit7 = 0;
                            do {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                u = c[u + 8 >> 2] | 0;
                                t = c[u >> 2] | 0;
                                if ((t | 0) == 0) {
                                    break
                                }
                            } while ((aK[c[k >> 2] & 3](c[y >> 2] | 0, t, q) | 0) == 0);
                            y = b0(12) | 0;
                            k = y;
                            if ((y | 0) == 0) {
                                c[q + 4 >> 2] = 0;
                                aw(a + 3384 | 0, 1)
                            }
                            c[y >> 2] = q;
                            t = u + 4 | 0;
                            c[y + 4 >> 2] = c[t >> 2];
                            c[(c[t >> 2] | 0) + 8 >> 2] = k;
                            c[y + 8 >> 2] = u;
                            c[t >> 2] = k;
                            t = q + 4 | 0;
                            c[t >> 2] = k;
                            c[q + 24 >> 2] = 0;
                            c[q + 16 >> 2] = 0;
                            c[q + 20 >> 2] = 0;
                            c[U + 24 >> 2] = l;
                            k = (c[(c[w >> 2] | 0) + 28 >> 2] | 0) + (c[(c[c[(c[t >> 2] | 0) + 4 >> 2] >> 2] | 0) + 8 >> 2] | 0) | 0;
                            c[q + 8 >> 2] = k;
                            t = c[a + 96 >> 2] | 0;
                            do {
                                if ((t | 0) == 100130) {
                                    V = k & 1
                                } else if ((t | 0) == 100131) {
                                    V = (k | 0) != 0 & 1
                                } else if ((t | 0) == 100132) {
                                    V = (k | 0) > 0 & 1
                                } else if ((t | 0) == 100133) {
                                    V = k >>> 31
                                } else if ((t | 0) == 100134) {
                                    if ((k | 0) > 1) {
                                        V = 1;
                                        break
                                    }
                                    V = (k | 0) < -1 & 1
                                } else {
                                    ay(512, 253, 2568, 928)
                                }
                            } while (0);
                            c[q + 12 >> 2] = V
                        } else {
                            k = T | 0;
                            if ((a5(c[k >> 2] | 0) | 0) == 0) {
                                aw(a + 3384 | 0, 1)
                            } else {
                                c[n >> 2] = 0;
                                c[k >> 2] = U;
                                c[U + 24 >> 2] = T;
                                break
                            }
                        }
                    } while (0);
                    bI(a, b);
                    i = d;
                    return
                }
            } while (0);
            bP(a, O, b);
            i = d;
            return
        }

        function bJ(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            var e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0;
            e = c[b >> 2] | 0;
            if ((b | 0) == (d | 0)) {
                f = e;
                return f | 0
            } else {
                g = b;
                h = e
            }
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                i = g + 24 | 0;
                c[i >> 2] = 0;
                j = g + 4 | 0;
                e = c[c[(c[j >> 2] | 0) + 8 >> 2] >> 2] | 0;
                b = e;
                k = e;
                l = c[k >> 2] | 0;
                if ((c[l + 16 >> 2] | 0) == (c[h + 16 >> 2] | 0)) {
                    m = l;
                    n = h + 8 | 0
                } else {
                    o = e + 24 | 0;
                    if ((c[o >> 2] | 0) == 0) {
                        p = 1116;
                        break
                    }
                    e = h + 8 | 0;
                    q = a7(c[(c[e >> 2] | 0) + 4 >> 2] | 0, c[l + 4 >> 2] | 0) | 0;
                    if ((q | 0) == 0) {
                        p = 1121;
                        break
                    }
                    if ((c[o >> 2] | 0) == 0) {
                        p = 1123;
                        break
                    }
                    if ((a5(c[k >> 2] | 0) | 0) == 0) {
                        p = 1126;
                        break
                    }
                    c[o >> 2] = 0;
                    c[k >> 2] = q;
                    c[q + 24 >> 2] = b;
                    m = q;
                    n = e
                }
                if ((c[n >> 2] | 0) != (m | 0)) {
                    if ((a4(c[(c[m + 4 >> 2] | 0) + 12 >> 2] | 0, m) | 0) == 0) {
                        p = 1129;
                        break
                    }
                    if ((a4(h, m) | 0) == 0) {
                        p = 1131;
                        break
                    }
                }
                e = g | 0;
                q = c[e >> 2] | 0;
                o = c[q + 20 >> 2] | 0;
                c[o + 24 >> 2] = c[g + 12 >> 2];
                c[o + 8 >> 2] = q;
                q = c[e >> 2] | 0;
                if ((c[i >> 2] | 0) != 0) {
                    if ((c[q + 28 >> 2] | 0) != 0) {
                        p = 1134;
                        break
                    }
                }
                c[q + 24 >> 2] = 0;
                q = c[j >> 2] | 0;
                e = q + 8 | 0;
                o = q + 4 | 0;
                c[(c[o >> 2] | 0) + 8 >> 2] = c[e >> 2];
                c[(c[e >> 2] | 0) + 4 >> 2] = c[o >> 2];
                b1(q);
                b1(g);
                q = c[k >> 2] | 0;
                if ((b | 0) == (d | 0)) {
                    f = q;
                    p = 1137;
                    break
                } else {
                    g = b;
                    h = q
                }
            }
            if ((p | 0) == 1129) {
                aw(a + 3384 | 0, 1);
                return 0
            } else if ((p | 0) == 1131) {
                aw(a + 3384 | 0, 1);
                return 0
            } else if ((p | 0) == 1123) {
                ay(512, 171, 2600, 752);
                return 0
            } else if ((p | 0) == 1116) {
                d = g | 0;
                m = c[d >> 2] | 0;
                n = c[m + 20 >> 2] | 0;
                c[n + 24 >> 2] = c[g + 12 >> 2];
                c[n + 8 >> 2] = m;
                m = c[d >> 2] | 0;
                do {
                    if ((c[i >> 2] | 0) != 0) {
                        if ((c[m + 28 >> 2] | 0) == 0) {
                            break
                        }
                        ay(512, 158, 2632, 464);
                        return 0
                    }
                } while (0);
                c[m + 24 >> 2] = 0;
                m = c[j >> 2] | 0;
                j = m + 8 | 0;
                i = m + 4 | 0;
                c[(c[i >> 2] | 0) + 8 >> 2] = c[j >> 2];
                c[(c[j >> 2] | 0) + 4 >> 2] = c[i >> 2];
                b1(m);
                b1(g);
                f = h;
                return f | 0
            } else if ((p | 0) == 1134) {
                ay(512, 158, 2632, 464);
                return 0
            } else if ((p | 0) == 1126) {
                aw(a + 3384 | 0, 1);
                return 0
            } else if ((p | 0) == 1121) {
                aw(a + 3384 | 0, 1);
                return 0
            } else if ((p | 0) == 1137) {
                return f | 0
            }
            return 0
        }

        function bK(a, b, d, e, f, g) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            e = e | 0;
            f = f | 0;
            g = g | 0;
            var i = 0,
                j = 0,
                k = 0,
                l = 0.0,
                m = 0,
                n = 0,
                o = 0.0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0;
            i = a + 104 | 0;
            j = b + 4 | 0;
            k = d;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                d = c[k + 16 >> 2] | 0;
                l = +h[d + 40 >> 3];
                m = c[k + 4 >> 2] | 0;
                n = c[m + 16 >> 2] | 0;
                o = +h[n + 40 >> 3];
                if (l >= o) {
                    if (l != o) {
                        p = 1184;
                        break
                    }
                    if (+h[d + 48 >> 3] > +h[n + 48 >> 3]) {
                        p = 1183;
                        break
                    }
                }
                q = b0(28) | 0;
                n = q;
                if ((q | 0) == 0) {
                    p = 1146;
                    break
                }
                c[q >> 2] = m;
                d = c[i >> 2] | 0;
                r = d + 16 | 0;
                s = d + 12 | 0;
                d = c[j >> 2] | 0;
                var tessellationLimit4 = 0;
                do {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    d = c[d + 8 >> 2] | 0;
                    t = c[d >> 2] | 0;
                    if ((t | 0) == 0) {
                        break
                    }
                } while ((aK[c[r >> 2] & 3](c[s >> 2] | 0, t, q) | 0) == 0);
                s = b0(12) | 0;
                r = s;
                if ((s | 0) == 0) {
                    p = 1151;
                    break
                }
                c[s >> 2] = q;
                t = d + 4 | 0;
                c[s + 4 >> 2] = c[t >> 2];
                c[(c[t >> 2] | 0) + 8 >> 2] = r;
                c[s + 8 >> 2] = d;
                c[t >> 2] = r;
                c[q + 4 >> 2] = r;
                c[q + 24 >> 2] = 0;
                c[q + 16 >> 2] = 0;
                c[q + 20 >> 2] = 0;
                c[m + 24 >> 2] = n;
                r = c[k + 8 >> 2] | 0;
                if ((r | 0) == (e | 0)) {
                    p = 1153;
                    break
                } else {
                    k = r
                }
            }
            if ((p | 0) == 1146) {
                aw(a + 3384 | 0, 1)
            } else if ((p | 0) == 1151) {
                c[q + 4 >> 2] = 0;
                aw(a + 3384 | 0, 1)
            } else if ((p | 0) == 1183) {
                ay(512, 361, 2720, 392)
            } else if ((p | 0) == 1184) {
                ay(512, 361, 2720, 392)
            } else if ((p | 0) == 1153) {
                q = c[c[(c[j >> 2] | 0) + 8 >> 2] >> 2] | 0;
                k = c[(c[q >> 2] | 0) + 4 >> 2] | 0;
                if ((f | 0) == 0) {
                    u = c[k + 8 >> 2] | 0
                } else {
                    u = f
                }
                L1564: do {
                    if ((c[k + 16 >> 2] | 0) == (c[u + 16 >> 2] | 0)) {
                        f = a + 96 | 0;
                        e = b;
                        i = u;
                        r = 1;
                        t = j;
                        s = q;
                        v = k;
                        L1566: {
                            var tessellationLimit6 = 0;
                            while (1) {
                                if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                w = s;
                                if ((c[v + 8 >> 2] | 0) != (i | 0)) {
                                    if ((a4(c[(c[v + 4 >> 2] | 0) + 12 >> 2] | 0, v) | 0) == 0) {
                                        p = 1159;
                                        break
                                    }
                                    if ((a4(c[(c[i + 4 >> 2] | 0) + 12 >> 2] | 0, v) | 0) == 0) {
                                        p = 1161;
                                        break
                                    }
                                }
                                x = c[e + 8 >> 2] | 0;
                                y = v + 28 | 0;
                                z = c[y >> 2] | 0;
                                A = x - z | 0;
                                c[s + 8 >> 2] = A;
                                B = c[f >> 2] | 0;
                                do {
                                    if ((B | 0) == 100131) {
                                        C = (x | 0) != (z | 0) & 1
                                    } else if ((B | 0) == 100132) {
                                        C = (A | 0) > 0 & 1
                                    } else if ((B | 0) == 100130) {
                                        C = A & 1
                                    } else if ((B | 0) == 100133) {
                                        C = A >>> 31
                                    } else if ((B | 0) == 100134) {
                                        if ((A | 0) > 1) {
                                            C = 1;
                                            break
                                        }
                                        C = (A | 0) < -1 & 1
                                    } else {
                                        p = 1169;
                                        break L1566
                                    }
                                } while (0);
                                c[s + 12 >> 2] = C;
                                c[e + 20 >> 2] = 1;
                                do {
                                    if ((r | 0) == 0) {
                                        if ((bL(a, e) | 0) == 0) {
                                            break
                                        }
                                        c[y >> 2] = (c[y >> 2] | 0) + (c[i + 28 >> 2] | 0);
                                        A = (c[v + 4 >> 2] | 0) + 28 | 0;
                                        c[A >> 2] = (c[A >> 2] | 0) + (c[(c[i + 4 >> 2] | 0) + 28 >> 2] | 0);
                                        A = c[e >> 2] | 0;
                                        if ((c[e + 24 >> 2] | 0) != 0) {
                                            if ((c[A + 28 >> 2] | 0) != 0) {
                                                p = 1174;
                                                break L1566
                                            }
                                        }
                                        c[A + 24 >> 2] = 0;
                                        A = c[t >> 2] | 0;
                                        B = A + 8 | 0;
                                        z = A + 4 | 0;
                                        c[(c[z >> 2] | 0) + 8 >> 2] = c[B >> 2];
                                        c[(c[B >> 2] | 0) + 4 >> 2] = c[z >> 2];
                                        b1(A);
                                        b1(e);
                                        if ((a5(i) | 0) == 0) {
                                            p = 1177;
                                            break L1566
                                        }
                                    }
                                } while (0);
                                y = s + 4 | 0;
                                A = c[c[(c[y >> 2] | 0) + 8 >> 2] >> 2] | 0;
                                z = c[(c[A >> 2] | 0) + 4 >> 2] | 0;
                                if ((c[z + 16 >> 2] | 0) == (c[v + 16 >> 2] | 0)) {
                                    e = w;
                                    i = v;
                                    r = 0;
                                    t = y;
                                    s = A;
                                    v = z
                                } else {
                                    D = w;
                                    E = A;
                                    F = z;
                                    break L1564
                                }
                            }
                        }
                        if ((p | 0) == 1161) {
                            aw(a + 3384 | 0, 1)
                        } else if ((p | 0) == 1169) {
                            ay(512, 253, 2568, 928)
                        } else if ((p | 0) == 1174) {
                            ay(512, 158, 2632, 464)
                        } else if ((p | 0) == 1159) {
                            aw(a + 3384 | 0, 1)
                        } else if ((p | 0) == 1177) {
                            aw(a + 3384 | 0, 1)
                        }
                    } else {
                        D = b;
                        E = q;
                        F = k
                    }
                } while (0);
                c[D + 20 >> 2] = 1;
                if (((c[D + 8 >> 2] | 0) - (c[F + 28 >> 2] | 0) | 0) != (c[E + 8 >> 2] | 0)) {
                    ay(512, 403, 2720, 200)
                }
                if ((g | 0) == 0) {
                    return
                }
                bM(a, D);
                return
            }
        }

        function bL(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0.0,
                q = 0,
                r = 0.0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0.0,
                x = 0.0,
                y = 0.0,
                z = 0.0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0;
            d = i;
            i = i + 56 | 0;
            e = d | 0;
            f = d + 24 | 0;
            g = d + 40 | 0;
            j = c[b + 4 >> 2] | 0;
            k = c[c[j + 8 >> 2] >> 2] | 0;
            l = c[b >> 2] | 0;
            m = c[k >> 2] | 0;
            n = l + 16 | 0;
            o = c[n >> 2] | 0;
            p = +h[o + 40 >> 3];
            q = c[m + 16 >> 2] | 0;
            r = +h[q + 40 >> 3];
            s = p < r;
            do {
                if (!s) {
                    t = p == r;
                    if (t) {
                        if (+h[o + 48 >> 3] <= +h[q + 48 >> 3]) {
                            break
                        }
                    }
                    u = l + 4 | 0;
                    v = c[(c[u >> 2] | 0) + 16 >> 2] | 0;
                    w = +h[v + 40 >> 3];
                    do {
                        if (w >= r) {
                            if (w != r) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[v + 48 >> 3] <= +h[q + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    do {
                        if (r >= p) {
                            if (!t) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[q + 48 >> 3] <= +h[o + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    x = r - w;
                    y = p - r;
                    do {
                        if (x + y > 0.0) {
                            z = +h[q + 48 >> 3];
                            if (x * (z - +h[o + 48 >> 3]) + y * (z - +h[v + 48 >> 3]) < 0.0) {
                                A = 0
                            } else {
                                break
                            }
                            i = d;
                            return A | 0
                        }
                    } while (0);
                    c[b + 20 >> 2] = 1;
                    c[(c[c[j + 4 >> 2] >> 2] | 0) + 20 >> 2] = 1;
                    v = c[u >> 2] | 0;
                    t = a6(v) | 0;
                    if ((t | 0) == 0) {
                        B = a + 3384 | 0;
                        aw(B | 0, 1);
                        return 0
                    }
                    C = c[t + 4 >> 2] | 0;
                    t = v + 4 | 0;
                    D = c[t >> 2] | 0;
                    E = c[(c[D + 4 >> 2] | 0) + 12 >> 2] | 0;
                    F = D + 8 | 0;
                    G = c[F >> 2] | 0;
                    H = E + 8 | 0;
                    I = c[H >> 2] | 0;
                    c[(c[G + 4 >> 2] | 0) + 12 >> 2] = E;
                    c[(c[I + 4 >> 2] | 0) + 12 >> 2] = D;
                    c[F >> 2] = I;
                    c[H >> 2] = G;
                    G = c[t >> 2] | 0;
                    H = G + 8 | 0;
                    I = c[H >> 2] | 0;
                    F = C + 8 | 0;
                    D = c[F >> 2] | 0;
                    c[(c[I + 4 >> 2] | 0) + 12 >> 2] = C;
                    c[(c[D + 4 >> 2] | 0) + 12 >> 2] = G;
                    c[H >> 2] = D;
                    c[F >> 2] = I;
                    c[(c[t >> 2] | 0) + 16 >> 2] = c[C + 16 >> 2];
                    I = C + 4 | 0;
                    F = c[I >> 2] | 0;
                    c[(c[F + 16 >> 2] | 0) + 8 >> 2] = F;
                    c[(c[I >> 2] | 0) + 20 >> 2] = c[(c[t >> 2] | 0) + 20 >> 2];
                    c[C + 28 >> 2] = c[v + 28 >> 2];
                    c[(c[I >> 2] | 0) + 28 >> 2] = c[(c[t >> 2] | 0) + 28 >> 2];
                    if ((C | 0) == 0) {
                        B = a + 3384 | 0;
                        aw(B | 0, 1);
                        return 0
                    }
                    if ((a4(c[(c[m + 4 >> 2] | 0) + 12 >> 2] | 0, l) | 0) == 0) {
                        aw(a + 3384 | 0, 1);
                        return 0
                    } else {
                        A = 1;
                        i = d;
                        return A | 0
                    }
                }
            } while (0);
            B = m + 4 | 0;
            m = c[B >> 2] | 0;
            j = c[m + 16 >> 2] | 0;
            y = +h[j + 40 >> 3];
            do {
                if (y >= p) {
                    if (y != p) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[j + 48 >> 3] <= +h[o + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            do {
                if (!s) {
                    if (p != r) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[o + 48 >> 3] <= +h[q + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            x = p - y;
            y = r - p;
            do {
                if (x + y > 0.0) {
                    w = +h[o + 48 >> 3];
                    if (x * (w - +h[q + 48 >> 3]) + y * (w - +h[j + 48 >> 3]) > 0.0) {
                        A = 0
                    } else {
                        break
                    }
                    i = d;
                    return A | 0
                }
            } while (0);
            do {
                if (p == r) {
                    if (+h[o + 48 >> 3] != +h[q + 48 >> 3]) {
                        break
                    }
                    if ((o | 0) == (q | 0)) {
                        A = 1;
                        i = d;
                        return A | 0
                    }
                    j = c[a + 108 >> 2] | 0;
                    s = c[o + 56 >> 2] | 0;
                    L1659: do {
                        if ((s | 0) > -1) {
                            bC(c[j >> 2] | 0, s)
                        } else {
                            C = s ^ -1;
                            if ((c[j + 16 >> 2] | 0) <= (C | 0)) {
                                ay(672, 254, 2336, 264);
                                return 0
                            }
                            t = (c[j + 4 >> 2] | 0) + (C << 2) | 0;
                            if ((c[t >> 2] | 0) == 0) {
                                ay(672, 254, 2336, 264);
                                return 0
                            }
                            c[t >> 2] = 0;
                            t = j + 12 | 0;
                            C = c[t >> 2] | 0;
                            if ((C | 0) <= 0) {
                                break
                            }
                            I = c[j + 8 >> 2] | 0;
                            v = C;
                            var tessellationLimit7 = 0;
                            do {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                v = v - 1 | 0;
                                if ((c[c[I + (v << 2) >> 2] >> 2] | 0) != 0) {
                                    break L1659
                                }
                                c[t >> 2] = v;
                            } while ((v | 0) > 0)
                        }
                    } while (0);
                    j = c[(c[B >> 2] | 0) + 12 >> 2] | 0;
                    s = g;
                    b9(f | 0, 0, 16);
                    c[s >> 2] = c[684];
                    c[s + 4 >> 2] = c[685];
                    c[s + 8 >> 2] = c[686];
                    c[s + 12 >> 2] = c[687];
                    s = c[j + 16 >> 2] | 0;
                    u = s + 12 | 0;
                    v = f | 0;
                    c[v >> 2] = c[u >> 2];
                    c[f + 4 >> 2] = c[(c[n >> 2] | 0) + 12 >> 2];
                    t = g | 0;
                    I = e | 0;
                    h[I >> 3] = +h[s + 16 >> 3];
                    h[e + 8 >> 3] = +h[s + 24 >> 3];
                    h[e + 16 >> 3] = +h[s + 32 >> 3];
                    c[u >> 2] = 0;
                    s = c[a + 3380 >> 2] | 0;
                    if ((s | 0) == 4) {
                        aO[c[a + 116 >> 2] & 3](I, v, t, u)
                    } else {
                        aG[s & 7](I, v, t, u, c[a + 3424 >> 2] | 0)
                    }
                    if ((c[u >> 2] | 0) == 0) {
                        c[u >> 2] = c[v >> 2]
                    }
                    if ((a4(j, l) | 0) == 0) {
                        aw(a + 3384 | 0, 1);
                        return 0
                    } else {
                        A = 1;
                        i = d;
                        return A | 0
                    }
                }
            } while (0);
            e = a6(m) | 0;
            if ((e | 0) == 0) {
                J = a + 3384 | 0;
                aw(J | 0, 1);
                return 0
            }
            g = c[e + 4 >> 2] | 0;
            e = m + 4 | 0;
            n = c[e >> 2] | 0;
            f = c[(c[n + 4 >> 2] | 0) + 12 >> 2] | 0;
            o = n + 8 | 0;
            q = c[o >> 2] | 0;
            j = f + 8 | 0;
            v = c[j >> 2] | 0;
            c[(c[q + 4 >> 2] | 0) + 12 >> 2] = f;
            c[(c[v + 4 >> 2] | 0) + 12 >> 2] = n;
            c[o >> 2] = v;
            c[j >> 2] = q;
            q = c[e >> 2] | 0;
            j = q + 8 | 0;
            v = c[j >> 2] | 0;
            o = g + 8 | 0;
            n = c[o >> 2] | 0;
            c[(c[v + 4 >> 2] | 0) + 12 >> 2] = g;
            c[(c[n + 4 >> 2] | 0) + 12 >> 2] = q;
            c[j >> 2] = n;
            c[o >> 2] = v;
            c[(c[e >> 2] | 0) + 16 >> 2] = c[g + 16 >> 2];
            v = g + 4 | 0;
            o = c[v >> 2] | 0;
            c[(c[o + 16 >> 2] | 0) + 8 >> 2] = o;
            c[(c[v >> 2] | 0) + 20 >> 2] = c[(c[e >> 2] | 0) + 20 >> 2];
            c[g + 28 >> 2] = c[m + 28 >> 2];
            c[(c[v >> 2] | 0) + 28 >> 2] = c[(c[e >> 2] | 0) + 28 >> 2];
            if ((g | 0) == 0) {
                J = a + 3384 | 0;
                aw(J | 0, 1);
                return 0
            }
            if ((a4(l, c[(c[B >> 2] | 0) + 12 >> 2] | 0) | 0) == 0) {
                aw(a + 3384 | 0, 1);
                return 0
            }
            c[k + 20 >> 2] = 1;
            c[b + 20 >> 2] = 1;
            A = 1;
            i = d;
            return A | 0
        }

        function bM(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0.0,
                z = 0,
                A = 0,
                B = 0.0,
                C = 0,
                D = 0,
                E = 0.0,
                F = 0.0,
                G = 0.0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0;
            d = a + 112 | 0;
            e = c[c[(c[b + 4 >> 2] | 0) + 8 >> 2] >> 2] | 0;
            f = b;
            L1695: {
                var tessellationLimit3 = 0;
                while (1) {
                    if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    if ((c[e + 20 >> 2] | 0) != 0) {
                        f = e;
                        e = c[c[(c[e + 4 >> 2] | 0) + 8 >> 2] >> 2] | 0;
                        continue
                    }
                    if ((c[f + 20 >> 2] | 0) == 0) {
                        b = c[c[(c[f + 4 >> 2] | 0) + 4 >> 2] >> 2] | 0;
                        if ((b | 0) == 0) {
                            g = 1339;
                            break
                        }
                        if ((c[b + 20 >> 2] | 0) == 0) {
                            g = 1340;
                            break
                        } else {
                            i = f;
                            j = b
                        }
                    } else {
                        i = e;
                        j = f
                    }
                    b = j + 20 | 0;
                    c[b >> 2] = 0;
                    k = j | 0;
                    l = c[k >> 2] | 0;
                    m = i | 0;
                    n = c[m >> 2] | 0;
                    o = l + 4 | 0;
                    p = c[(c[o >> 2] | 0) + 16 >> 2] | 0;
                    L1704: do {
                        if ((p | 0) == (c[(c[n + 4 >> 2] | 0) + 16 >> 2] | 0)) {
                            q = n;
                            r = l;
                            s = i;
                            t = j
                        } else {
                            u = j + 4 | 0;
                            v = c[u >> 2] | 0;
                            w = c[c[v + 8 >> 2] >> 2] | 0;
                            x = c[w >> 2] | 0;
                            y = +h[p + 40 >> 3];
                            z = x + 4 | 0;
                            A = c[(c[z >> 2] | 0) + 16 >> 2] | 0;
                            B = +h[A + 40 >> 3];
                            C = y == B;
                            if (C) {
                                if (+h[p + 48 >> 3] == +h[A + 48 >> 3]) {
                                    g = 1268;
                                    break L1695
                                }
                            }
                            L1709: do {
                                if (y < B) {
                                    g = 1274
                                } else {
                                    do {
                                        if (C) {
                                            if (+h[p + 48 >> 3] > +h[A + 48 >> 3]) {
                                                break
                                            }
                                            g = 1274;
                                            break L1709
                                        }
                                    } while (0);
                                    D = c[x + 16 >> 2] | 0;
                                    if (B >= y) {
                                        if (!C) {
                                            g = 1338;
                                            break L1695
                                        }
                                        if (+h[A + 48 >> 3] > +h[p + 48 >> 3]) {
                                            g = 1335;
                                            break L1695
                                        }
                                    }
                                    E = +h[D + 40 >> 3];
                                    if (y >= E) {
                                        if (y != E) {
                                            g = 1336;
                                            break L1695
                                        }
                                        if (+h[p + 48 >> 3] > +h[D + 48 >> 3]) {
                                            g = 1337;
                                            break L1695
                                        }
                                    }
                                    F = y - B;
                                    G = E - y;
                                    if (F + G > 0.0) {
                                        E = +h[p + 48 >> 3];
                                        if (F * (E - +h[D + 48 >> 3]) + G * (E - +h[A + 48 >> 3]) > 0.0) {
                                            q = n;
                                            r = l;
                                            s = i;
                                            t = j;
                                            break L1704
                                        }
                                    }
                                    c[w + 20 >> 2] = 1;
                                    c[b >> 2] = 1;
                                    D = a6(x) | 0;
                                    if ((D | 0) == 0) {
                                        g = 1334;
                                        break L1695
                                    }
                                    H = c[D + 4 >> 2] | 0;
                                    D = c[z >> 2] | 0;
                                    I = c[(c[D + 4 >> 2] | 0) + 12 >> 2] | 0;
                                    J = D + 8 | 0;
                                    K = c[J >> 2] | 0;
                                    L = I + 8 | 0;
                                    M = c[L >> 2] | 0;
                                    c[(c[K + 4 >> 2] | 0) + 12 >> 2] = I;
                                    c[(c[M + 4 >> 2] | 0) + 12 >> 2] = D;
                                    c[J >> 2] = M;
                                    c[L >> 2] = K;
                                    K = c[z >> 2] | 0;
                                    L = K + 8 | 0;
                                    M = c[L >> 2] | 0;
                                    J = H + 8 | 0;
                                    D = c[J >> 2] | 0;
                                    c[(c[M + 4 >> 2] | 0) + 12 >> 2] = H;
                                    c[(c[D + 4 >> 2] | 0) + 12 >> 2] = K;
                                    c[L >> 2] = D;
                                    c[J >> 2] = M;
                                    c[(c[z >> 2] | 0) + 16 >> 2] = c[H + 16 >> 2];
                                    M = H + 4 | 0;
                                    J = c[M >> 2] | 0;
                                    c[(c[J + 16 >> 2] | 0) + 8 >> 2] = J;
                                    c[(c[M >> 2] | 0) + 20 >> 2] = c[(c[z >> 2] | 0) + 20 >> 2];
                                    c[H + 28 >> 2] = c[x + 28 >> 2];
                                    c[(c[M >> 2] | 0) + 28 >> 2] = c[(c[z >> 2] | 0) + 28 >> 2];
                                    if ((H | 0) == 0) {
                                        g = 1333;
                                        break L1695
                                    }
                                    if ((a4(c[l + 12 >> 2] | 0, c[z >> 2] | 0) | 0) == 0) {
                                        g = 1299;
                                        break L1695
                                    }
                                    c[(c[(c[M >> 2] | 0) + 20 >> 2] | 0) + 24 >> 2] = c[j + 12 >> 2]
                                }
                            } while (0);
                            if ((g | 0) == 1274) {
                                g = 0;
                                x = c[l + 16 >> 2] | 0;
                                E = +h[x + 40 >> 3];
                                if (B >= E) {
                                    if (B != E) {
                                        g = 1329;
                                        break L1695
                                    }
                                    if (+h[A + 48 >> 3] > +h[x + 48 >> 3]) {
                                        g = 1330;
                                        break L1695
                                    }
                                }
                                G = B - y;
                                F = E - B;
                                if (G + F > 0.0) {
                                    E = +h[A + 48 >> 3];
                                    if (G * (E - +h[x + 48 >> 3]) + F * (E - +h[p + 48 >> 3]) < 0.0) {
                                        q = n;
                                        r = l;
                                        s = i;
                                        t = j;
                                        break
                                    }
                                }
                                c[b >> 2] = 1;
                                c[(c[c[v + 4 >> 2] >> 2] | 0) + 20 >> 2] = 1;
                                x = a6(l) | 0;
                                if ((x | 0) == 0) {
                                    g = 1331;
                                    break L1695
                                }
                                w = c[x + 4 >> 2] | 0;
                                x = c[o >> 2] | 0;
                                C = c[(c[x + 4 >> 2] | 0) + 12 >> 2] | 0;
                                M = x + 8 | 0;
                                H = c[M >> 2] | 0;
                                J = C + 8 | 0;
                                D = c[J >> 2] | 0;
                                c[(c[H + 4 >> 2] | 0) + 12 >> 2] = C;
                                c[(c[D + 4 >> 2] | 0) + 12 >> 2] = x;
                                c[M >> 2] = D;
                                c[J >> 2] = H;
                                H = c[o >> 2] | 0;
                                J = H + 8 | 0;
                                D = c[J >> 2] | 0;
                                M = w + 8 | 0;
                                x = c[M >> 2] | 0;
                                c[(c[D + 4 >> 2] | 0) + 12 >> 2] = w;
                                c[(c[x + 4 >> 2] | 0) + 12 >> 2] = H;
                                c[J >> 2] = x;
                                c[M >> 2] = D;
                                c[(c[o >> 2] | 0) + 16 >> 2] = c[w + 16 >> 2];
                                D = w + 4 | 0;
                                M = c[D >> 2] | 0;
                                c[(c[M + 16 >> 2] | 0) + 8 >> 2] = M;
                                c[(c[D >> 2] | 0) + 20 >> 2] = c[(c[o >> 2] | 0) + 20 >> 2];
                                c[w + 28 >> 2] = c[l + 28 >> 2];
                                c[(c[D >> 2] | 0) + 28 >> 2] = c[(c[o >> 2] | 0) + 28 >> 2];
                                if ((w | 0) == 0) {
                                    g = 1332;
                                    break L1695
                                }
                                if ((a4(c[z >> 2] | 0, w) | 0) == 0) {
                                    g = 1284;
                                    break L1695
                                }
                                c[(c[w + 20 >> 2] | 0) + 24 >> 2] = c[j + 12 >> 2]
                            }
                            if ((c[i + 24 >> 2] | 0) != 0) {
                                w = c[m >> 2] | 0;
                                if ((c[w + 28 >> 2] | 0) != 0) {
                                    g = 1303;
                                    break L1695
                                }
                                c[w + 24 >> 2] = 0;
                                w = c[i + 4 >> 2] | 0;
                                D = w + 8 | 0;
                                M = w + 4 | 0;
                                c[(c[M >> 2] | 0) + 8 >> 2] = c[D >> 2];
                                c[(c[D >> 2] | 0) + 4 >> 2] = c[M >> 2];
                                b1(w);
                                b1(i);
                                if ((a5(n) | 0) == 0) {
                                    g = 1305;
                                    break L1695
                                }
                                w = c[c[(c[u >> 2] | 0) + 8 >> 2] >> 2] | 0;
                                q = c[w >> 2] | 0;
                                r = l;
                                s = w;
                                t = j;
                                break
                            }
                            if ((c[j + 24 >> 2] | 0) == 0) {
                                q = n;
                                r = l;
                                s = i;
                                t = j;
                                break
                            }
                            w = c[k >> 2] | 0;
                            if ((c[w + 28 >> 2] | 0) != 0) {
                                g = 1309;
                                break L1695
                            }
                            c[w + 24 >> 2] = 0;
                            w = c[u >> 2] | 0;
                            M = w + 8 | 0;
                            D = w + 4 | 0;
                            c[(c[D >> 2] | 0) + 8 >> 2] = c[M >> 2];
                            c[(c[M >> 2] | 0) + 4 >> 2] = c[D >> 2];
                            b1(w);
                            b1(j);
                            if ((a5(l) | 0) == 0) {
                                g = 1311;
                                break L1695
                            }
                            w = c[c[(c[i + 4 >> 2] | 0) + 4 >> 2] >> 2] | 0;
                            q = n;
                            r = c[w >> 2] | 0;
                            s = i;
                            t = w
                        }
                    } while (0);
                    n = r + 16 | 0;
                    l = q + 16 | 0;
                    L1752: do {
                        if ((c[n >> 2] | 0) != (c[l >> 2] | 0)) {
                            k = c[(c[r + 4 >> 2] | 0) + 16 >> 2] | 0;
                            m = c[(c[q + 4 >> 2] | 0) + 16 >> 2] | 0;
                            do {
                                if ((k | 0) != (m | 0)) {
                                    if ((c[t + 24 >> 2] | 0) != 0) {
                                        break
                                    }
                                    if ((c[s + 24 >> 2] | 0) != 0) {
                                        break
                                    }
                                    o = c[d >> 2] | 0;
                                    if (!((k | 0) == (o | 0) | (m | 0) == (o | 0))) {
                                        break
                                    }
                                    if ((bN(a, t) | 0) == 0) {
                                        break L1752
                                    } else {
                                        g = 1341;
                                        break L1695
                                    }
                                }
                            } while (0);
                            bL(a, t) | 0
                        }
                    } while (0);
                    if ((c[n >> 2] | 0) != (c[l >> 2] | 0)) {
                        e = s;
                        f = t;
                        continue
                    }
                    m = r + 4 | 0;
                    k = c[q + 4 >> 2] | 0;
                    if ((c[(c[m >> 2] | 0) + 16 >> 2] | 0) != (c[k + 16 >> 2] | 0)) {
                        e = s;
                        f = t;
                        continue
                    }
                    u = q + 28 | 0;
                    c[u >> 2] = (c[u >> 2] | 0) + (c[r + 28 >> 2] | 0);
                    u = k + 28 | 0;
                    c[u >> 2] = (c[u >> 2] | 0) + (c[(c[m >> 2] | 0) + 28 >> 2] | 0);
                    m = c[t >> 2] | 0;
                    if ((c[t + 24 >> 2] | 0) != 0) {
                        if ((c[m + 28 >> 2] | 0) != 0) {
                            g = 1324;
                            break
                        }
                    }
                    c[m + 24 >> 2] = 0;
                    m = c[t + 4 >> 2] | 0;
                    u = m + 8 | 0;
                    k = m + 4 | 0;
                    c[(c[k >> 2] | 0) + 8 >> 2] = c[u >> 2];
                    c[(c[u >> 2] | 0) + 4 >> 2] = c[k >> 2];
                    b1(m);
                    b1(t);
                    if ((a5(r) | 0) == 0) {
                        g = 1326;
                        break
                    }
                    e = s;
                    f = c[c[(c[s + 4 >> 2] | 0) + 4 >> 2] >> 2] | 0
                }
            }
            if ((g | 0) == 1284) {
                aw(a + 3384 | 0, 1)
            } else if ((g | 0) == 1324) {
                ay(512, 158, 2632, 464)
            } else if ((g | 0) == 1326) {
                aw(a + 3384 | 0, 1)
            } else if ((g | 0) == 1299) {
                aw(a + 3384 | 0, 1)
            } else if ((g | 0) == 1311) {
                aw(a + 3384 | 0, 1)
            } else if ((g | 0) == 1309) {
                ay(512, 158, 2632, 464)
            } else if ((g | 0) == 1268) {
                ay(512, 581, 2672, 936)
            } else if ((g | 0) == 1303) {
                ay(512, 158, 2632, 464)
            } else if ((g | 0) == 1333) {
                N = a + 3384 | 0;
                aw(N | 0, 1)
            } else if ((g | 0) == 1334) {
                N = a + 3384 | 0;
                aw(N | 0, 1)
            } else if ((g | 0) == 1335) {
                ay(688, 85, 2464, 1384)
            } else if ((g | 0) == 1336) {
                ay(688, 85, 2464, 1384)
            } else if ((g | 0) == 1337) {
                ay(688, 85, 2464, 1384)
            } else if ((g | 0) == 1338) {
                ay(688, 85, 2464, 1384)
            } else if ((g | 0) == 1339) {
                return
            } else if ((g | 0) == 1340) {
                return
            } else if ((g | 0) == 1341) {
                return
            } else if ((g | 0) == 1305) {
                aw(a + 3384 | 0, 1)
            } else if ((g | 0) == 1329) {
                ay(688, 85, 2464, 1384)
            } else if ((g | 0) == 1330) {
                ay(688, 85, 2464, 1384)
            } else if ((g | 0) == 1331) {
                O = a + 3384 | 0;
                aw(O | 0, 1)
            } else if ((g | 0) == 1332) {
                O = a + 3384 | 0;
                aw(O | 0, 1)
            }
        }

        function bN(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0.0,
                z = 0,
                A = 0.0,
                B = 0,
                C = 0,
                D = 0.0,
                E = 0,
                F = 0.0,
                G = 0.0,
                H = 0.0,
                I = 0.0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0.0,
                P = 0,
                Q = 0,
                R = 0.0,
                S = 0.0,
                T = 0.0,
                U = 0.0,
                V = 0.0,
                W = 0.0,
                X = 0.0,
                Y = 0,
                Z = 0.0,
                _ = 0.0,
                $ = 0.0,
                aa = 0.0,
                ab = 0.0,
                ac = 0,
                ad = 0,
                ae = 0,
                af = 0,
                ag = 0,
                ah = 0,
                ai = 0,
                aj = 0,
                ak = 0,
                al = 0,
                am = 0,
                an = 0,
                ao = 0,
                ap = 0,
                aq = 0,
                ar = 0.0,
                as = 0.0,
                at = 0,
                au = 0;
            d = i;
            i = i + 120 | 0;
            e = d | 0;
            f = d + 24 | 0;
            g = d + 40 | 0;
            j = d + 56 | 0;
            k = b + 4 | 0;
            l = c[c[(c[k >> 2] | 0) + 8 >> 2] >> 2] | 0;
            m = l;
            n = b | 0;
            o = c[n >> 2] | 0;
            p = c[l >> 2] | 0;
            q = o + 16 | 0;
            r = c[q >> 2] | 0;
            s = p + 16 | 0;
            t = c[s >> 2] | 0;
            u = o + 4 | 0;
            v = c[(c[u >> 2] | 0) + 16 >> 2] | 0;
            w = p + 4 | 0;
            p = c[(c[w >> 2] | 0) + 16 >> 2] | 0;
            x = p + 40 | 0;
            y = +h[x >> 3];
            z = v + 40 | 0;
            A = +h[z >> 3];
            do {
                if (y == A) {
                    if (+h[p + 48 >> 3] != +h[v + 48 >> 3]) {
                        break
                    }
                    ay(512, 628, 2696, 96);
                    return 0
                }
            } while (0);
            B = a + 112 | 0;
            C = c[B >> 2] | 0;
            D = +h[C + 40 >> 3];
            do {
                if (A >= D) {
                    if (A != D) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[v + 48 >> 3] <= +h[C + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            E = r + 40 | 0;
            F = +h[E >> 3];
            do {
                if (D >= F) {
                    if (D != F) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[C + 48 >> 3] <= +h[r + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            G = D - A;
            H = F - D;
            do {
                if (G + H > 0.0) {
                    I = +h[C + 48 >> 3];
                    if (G * (I - +h[r + 48 >> 3]) + H * (I - +h[v + 48 >> 3]) <= 0.0) {
                        break
                    }
                    ay(512, 629, 2696, 32);
                    return 0
                }
            } while (0);
            do {
                if (y >= D) {
                    if (y != D) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[p + 48 >> 3] <= +h[C + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            J = t + 40 | 0;
            H = +h[J >> 3];
            do {
                if (D >= H) {
                    if (D != H) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[C + 48 >> 3] <= +h[t + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            G = D - y;
            I = H - D;
            do {
                if (G + I > 0.0) {
                    D = +h[C + 48 >> 3];
                    if (G * (D - +h[t + 48 >> 3]) + I * (D - +h[p + 48 >> 3]) >= 0.0) {
                        break
                    }
                    ay(512, 630, 2696, 1600);
                    return 0
                }
            } while (0);
            if ((r | 0) == (C | 0) | (t | 0) == (C | 0)) {
                ay(512, 631, 2696, 1520);
                return 0
            }
            if ((c[b + 24 >> 2] | 0) != 0) {
                ay(512, 632, 2696, 1440);
                return 0
            }
            if ((c[l + 24 >> 2] | 0) != 0) {
                ay(512, 632, 2696, 1440);
                return 0
            }
            if ((r | 0) == (t | 0)) {
                K = 0;
                i = d;
                return K | 0
            }
            C = r + 48 | 0;
            I = +h[C >> 3];
            L = v + 48 | 0;
            G = +h[L >> 3];
            M = t + 48 | 0;
            D = +h[M >> 3];
            N = p + 48 | 0;
            O = +h[N >> 3];
            if ((I > G ? G : I) > (D < O ? O : D)) {
                K = 0;
                i = d;
                return K | 0
            }
            P = F < H;
            do {
                if (P) {
                    Q = 1374
                } else {
                    if (!(F != H | I > D)) {
                        Q = 1374;
                        break
                    }
                    do {
                        if (A >= H) {
                            if (!(A != H | G > D)) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    do {
                        if (H >= F) {
                            if (!(H != F | D > I)) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    R = H - A;
                    S = F - H;
                    if (R + S <= 0.0) {
                        break
                    }
                    if (R * (D - I) + S * (D - G) < 0.0) {
                        K = 0
                    } else {
                        break
                    }
                    i = d;
                    return K | 0
                }
            } while (0);
            do {
                if ((Q | 0) == 1374) {
                    do {
                        if (y >= F) {
                            if (!(y != F | O > I)) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    do {
                        if (!P) {
                            if (!(F != H | I > D)) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    G = F - y;
                    A = H - F;
                    if (G + A <= 0.0) {
                        break
                    }
                    if (G * (I - D) + A * (I - O) > 0.0) {
                        K = 0
                    } else {
                        break
                    }
                    i = d;
                    return K | 0
                }
            } while (0);
            bx(v, r, p, t, j);
            O = +h[C >> 3];
            I = +h[L >> 3];
            L = j + 48 | 0;
            D = +h[L >> 3];
            if ((O > I ? I : O) > D) {
                ay(512, 651, 2696, 1344);
                return 0
            }
            F = +h[M >> 3];
            H = +h[N >> 3];
            if (D > (F < H ? H : F)) {
                ay(512, 652, 2696, 1128);
                return 0
            }
            y = +h[x >> 3];
            A = +h[z >> 3];
            z = j + 40 | 0;
            G = +h[z >> 3];
            if ((y > A ? A : y) > G) {
                ay(512, 653, 2696, 1072);
                return 0
            }
            S = +h[J >> 3];
            R = +h[E >> 3];
            if (G > (S < R ? R : S)) {
                ay(512, 654, 2696, 1032);
                return 0
            }
            E = c[B >> 2] | 0;
            T = +h[E + 40 >> 3];
            do {
                if (G < T) {
                    U = +h[E + 48 >> 3];
                    Q = 1400
                } else {
                    if (G != T) {
                        V = G;
                        W = D;
                        break
                    }
                    X = +h[E + 48 >> 3];
                    if (D > X) {
                        V = G;
                        W = D
                    } else {
                        U = X;
                        Q = 1400
                    }
                }
            } while (0);
            if ((Q | 0) == 1400) {
                h[z >> 3] = T;
                h[L >> 3] = U;
                V = T;
                W = U
            }
            if (R < S) {
                Y = r;
                Z = R
            } else {
                J = R != S | O > F;
                Y = J ? t : r;
                Z = J ? S : R
            }
            do {
                if (Z < V) {
                    _ = +h[Y + 48 >> 3];
                    Q = 1407
                } else {
                    if (Z != V) {
                        $ = V;
                        aa = W;
                        break
                    }
                    U = +h[Y + 48 >> 3];
                    if (U > W) {
                        $ = V;
                        aa = W
                    } else {
                        _ = U;
                        Q = 1407
                    }
                }
            } while (0);
            if ((Q | 0) == 1407) {
                h[z >> 3] = Z;
                h[L >> 3] = _;
                $ = Z;
                aa = _
            }
            do {
                if (!($ == R & aa == O)) {
                    if ($ == S & aa == F) {
                        break
                    }
                    L = A == T;
                    if (L) {
                        _ = +h[E + 48 >> 3];
                        if (I == _) {
                            ab = _;
                            Q = 1422
                        } else {
                            Q = 1413
                        }
                    } else {
                        Q = 1413
                    }
                    do {
                        if ((Q | 0) == 1413) {
                            do {
                                if (A >= T) {
                                    if (!L) {
                                        ay(688, 85, 2464, 1384);
                                        return 0
                                    }
                                    if (I <= +h[E + 48 >> 3]) {
                                        break
                                    }
                                    ay(688, 85, 2464, 1384);
                                    return 0
                                }
                            } while (0);
                            do {
                                if (T >= $) {
                                    if (T != $) {
                                        ay(688, 85, 2464, 1384);
                                        return 0
                                    }
                                    if (+h[E + 48 >> 3] <= aa) {
                                        break
                                    }
                                    ay(688, 85, 2464, 1384);
                                    return 0
                                }
                            } while (0);
                            _ = T - A;
                            Z = $ - T;
                            if (_ + Z <= 0.0) {
                                break
                            }
                            W = +h[E + 48 >> 3];
                            if (_ * (W - aa) + Z * (W - I) < 0.0) {
                                ab = W;
                                Q = 1422
                            }
                        }
                    } while (0);
                    do {
                        if ((Q | 0) == 1422) {
                            z = y == T;
                            if (!(z & H == ab)) {
                                do {
                                    if (y >= T) {
                                        if (!(H > ab | z ^ 1)) {
                                            break
                                        }
                                        ay(688, 85, 2464, 1384);
                                        return 0
                                    }
                                } while (0);
                                do {
                                    if (T >= $) {
                                        if (!(T != $ | ab > aa)) {
                                            break
                                        }
                                        ay(688, 85, 2464, 1384);
                                        return 0
                                    }
                                } while (0);
                                W = T - y;
                                Z = $ - T;
                                if (W + Z <= 0.0) {
                                    break
                                }
                                if (W * (ab - aa) + Z * (ab - H) <= 0.0) {
                                    break
                                }
                            }
                            z = c[u >> 2] | 0;
                            Y = a6(z) | 0;
                            if ((Y | 0) == 0) {
                                ac = a + 3384 | 0;
                                aw(ac | 0, 1);
                                return 0
                            }
                            J = c[Y + 4 >> 2] | 0;
                            Y = z + 4 | 0;
                            j = c[Y >> 2] | 0;
                            M = c[(c[j + 4 >> 2] | 0) + 12 >> 2] | 0;
                            C = j + 8 | 0;
                            P = c[C >> 2] | 0;
                            ad = M + 8 | 0;
                            ae = c[ad >> 2] | 0;
                            c[(c[P + 4 >> 2] | 0) + 12 >> 2] = M;
                            c[(c[ae + 4 >> 2] | 0) + 12 >> 2] = j;
                            c[C >> 2] = ae;
                            c[ad >> 2] = P;
                            P = c[Y >> 2] | 0;
                            ad = P + 8 | 0;
                            ae = c[ad >> 2] | 0;
                            C = J + 8 | 0;
                            j = c[C >> 2] | 0;
                            c[(c[ae + 4 >> 2] | 0) + 12 >> 2] = J;
                            c[(c[j + 4 >> 2] | 0) + 12 >> 2] = P;
                            c[ad >> 2] = j;
                            c[C >> 2] = ae;
                            c[(c[Y >> 2] | 0) + 16 >> 2] = c[J + 16 >> 2];
                            ae = J + 4 | 0;
                            C = c[ae >> 2] | 0;
                            c[(c[C + 16 >> 2] | 0) + 8 >> 2] = C;
                            c[(c[ae >> 2] | 0) + 20 >> 2] = c[(c[Y >> 2] | 0) + 20 >> 2];
                            c[J + 28 >> 2] = c[z + 28 >> 2];
                            c[(c[ae >> 2] | 0) + 28 >> 2] = c[(c[Y >> 2] | 0) + 28 >> 2];
                            if ((J | 0) == 0) {
                                ac = a + 3384 | 0;
                                aw(ac | 0, 1);
                                return 0
                            }
                            J = c[w >> 2] | 0;
                            Y = a6(J) | 0;
                            if ((Y | 0) == 0) {
                                af = a + 3384 | 0;
                                aw(af | 0, 1);
                                return 0
                            }
                            ae = c[Y + 4 >> 2] | 0;
                            Y = J + 4 | 0;
                            z = c[Y >> 2] | 0;
                            C = c[(c[z + 4 >> 2] | 0) + 12 >> 2] | 0;
                            j = z + 8 | 0;
                            ad = c[j >> 2] | 0;
                            P = C + 8 | 0;
                            M = c[P >> 2] | 0;
                            c[(c[ad + 4 >> 2] | 0) + 12 >> 2] = C;
                            c[(c[M + 4 >> 2] | 0) + 12 >> 2] = z;
                            c[j >> 2] = M;
                            c[P >> 2] = ad;
                            ad = c[Y >> 2] | 0;
                            P = ad + 8 | 0;
                            M = c[P >> 2] | 0;
                            j = ae + 8 | 0;
                            z = c[j >> 2] | 0;
                            c[(c[M + 4 >> 2] | 0) + 12 >> 2] = ae;
                            c[(c[z + 4 >> 2] | 0) + 12 >> 2] = ad;
                            c[P >> 2] = z;
                            c[j >> 2] = M;
                            c[(c[Y >> 2] | 0) + 16 >> 2] = c[ae + 16 >> 2];
                            M = ae + 4 | 0;
                            j = c[M >> 2] | 0;
                            c[(c[j + 16 >> 2] | 0) + 8 >> 2] = j;
                            c[(c[M >> 2] | 0) + 20 >> 2] = c[(c[Y >> 2] | 0) + 20 >> 2];
                            c[ae + 28 >> 2] = c[J + 28 >> 2];
                            c[(c[M >> 2] | 0) + 28 >> 2] = c[(c[Y >> 2] | 0) + 28 >> 2];
                            if ((ae | 0) == 0) {
                                af = a + 3384 | 0;
                                aw(af | 0, 1);
                                return 0
                            }
                            if ((a4(c[(c[w >> 2] | 0) + 12 >> 2] | 0, o) | 0) == 0) {
                                aw(a + 3384 | 0, 1);
                                return 0
                            }
                            h[(c[q >> 2] | 0) + 40 >> 3] = $;
                            h[(c[q >> 2] | 0) + 48 >> 3] = aa;
                            ae = a + 108 | 0;
                            Y = c[ae >> 2] | 0;
                            M = c[q >> 2] | 0;
                            L1952: do {
                                if ((c[Y + 20 >> 2] | 0) == 0) {
                                    J = Y + 12 | 0;
                                    j = c[J >> 2] | 0;
                                    z = j + 1 | 0;
                                    c[J >> 2] = z;
                                    J = Y + 16 | 0;
                                    P = c[J >> 2] | 0;
                                    do {
                                        if ((z | 0) >= (P | 0)) {
                                            ad = Y + 4 | 0;
                                            C = c[ad >> 2] | 0;
                                            c[J >> 2] = P << 1;
                                            ag = b2(C, P << 3) | 0;
                                            c[ad >> 2] = ag;
                                            if ((ag | 0) != 0) {
                                                break
                                            }
                                            c[ad >> 2] = C;
                                            ah = 2147483647;
                                            break L1952
                                        }
                                    } while (0);
                                    if ((j | 0) == 2147483647) {
                                        ay(672, 194, 2264, 424);
                                        return 0
                                    } else {
                                        c[(c[Y + 4 >> 2] | 0) + (j << 2) >> 2] = M;
                                        ah = j ^ -1;
                                        break
                                    }
                                } else {
                                    ah = bB(c[Y >> 2] | 0, M) | 0
                                }
                            } while (0);
                            c[(c[q >> 2] | 0) + 56 >> 2] = ah;
                            M = c[q >> 2] | 0;
                            if ((c[M + 56 >> 2] | 0) == 2147483647) {
                                bF(c[ae >> 2] | 0);
                                c[ae >> 2] = 0;
                                aw(a + 3384 | 0, 1);
                                return 0
                            }
                            Y = f | 0;
                            c[Y >> 2] = c[r + 12 >> 2];
                            c[f + 4 >> 2] = c[v + 12 >> 2];
                            c[f + 8 >> 2] = c[t + 12 >> 2];
                            c[f + 12 >> 2] = c[p + 12 >> 2];
                            P = M + 16 | 0;
                            J = g | 0;
                            b9(P | 0, 0, 24);
                            bO(M, r, v, J);
                            bO(M, t, p, g + 8 | 0);
                            z = e | 0;
                            h[z >> 3] = +h[P >> 3];
                            h[e + 8 >> 3] = +h[M + 24 >> 3];
                            h[e + 16 >> 3] = +h[M + 32 >> 3];
                            P = M + 12 | 0;
                            c[P >> 2] = 0;
                            M = c[a + 3380 >> 2] | 0;
                            if ((M | 0) == 4) {
                                aO[c[a + 116 >> 2] & 3](z, Y, J, P)
                            } else {
                                aG[M & 7](z, Y, J, P, c[a + 3424 >> 2] | 0)
                            }
                            do {
                                if ((c[P >> 2] | 0) == 0) {
                                    J = a + 100 | 0;
                                    if ((c[J >> 2] | 0) != 0) {
                                        break
                                    }
                                    Y = c[a + 3376 >> 2] | 0;
                                    if ((Y | 0) == 16) {
                                        aH[c[a + 12 >> 2] & 31](100156)
                                    } else {
                                        aI[Y & 31](100156, c[a + 3424 >> 2] | 0)
                                    }
                                    c[J >> 2] = 1
                                }
                            } while (0);
                            c[l + 20 >> 2] = 1;
                            c[b + 20 >> 2] = 1;
                            c[(c[c[(c[k >> 2] | 0) + 4 >> 2] >> 2] | 0) + 20 >> 2] = 1;
                            K = 0;
                            i = d;
                            return K | 0
                        }
                    } while (0);
                    if ((p | 0) == (E | 0)) {
                        P = c[u >> 2] | 0;
                        ae = a6(P) | 0;
                        if ((ae | 0) == 0) {
                            ai = a + 3384 | 0;
                            aw(ai | 0, 1);
                            return 0
                        }
                        J = c[ae + 4 >> 2] | 0;
                        ae = P + 4 | 0;
                        Y = c[ae >> 2] | 0;
                        z = c[(c[Y + 4 >> 2] | 0) + 12 >> 2] | 0;
                        M = Y + 8 | 0;
                        C = c[M >> 2] | 0;
                        ad = z + 8 | 0;
                        ag = c[ad >> 2] | 0;
                        c[(c[C + 4 >> 2] | 0) + 12 >> 2] = z;
                        c[(c[ag + 4 >> 2] | 0) + 12 >> 2] = Y;
                        c[M >> 2] = ag;
                        c[ad >> 2] = C;
                        C = c[ae >> 2] | 0;
                        ad = C + 8 | 0;
                        ag = c[ad >> 2] | 0;
                        M = J + 8 | 0;
                        Y = c[M >> 2] | 0;
                        c[(c[ag + 4 >> 2] | 0) + 12 >> 2] = J;
                        c[(c[Y + 4 >> 2] | 0) + 12 >> 2] = C;
                        c[ad >> 2] = Y;
                        c[M >> 2] = ag;
                        c[(c[ae >> 2] | 0) + 16 >> 2] = c[J + 16 >> 2];
                        ag = J + 4 | 0;
                        M = c[ag >> 2] | 0;
                        c[(c[M + 16 >> 2] | 0) + 8 >> 2] = M;
                        c[(c[ag >> 2] | 0) + 20 >> 2] = c[(c[ae >> 2] | 0) + 20 >> 2];
                        c[J + 28 >> 2] = c[P + 28 >> 2];
                        c[(c[ag >> 2] | 0) + 28 >> 2] = c[(c[ae >> 2] | 0) + 28 >> 2];
                        if ((J | 0) == 0) {
                            ai = a + 3384 | 0;
                            aw(ai | 0, 1);
                            return 0
                        }
                        if ((a4(c[w >> 2] | 0, o) | 0) == 0) {
                            aw(a + 3384 | 0, 1);
                            return 0
                        }
                        J = c[(c[n >> 2] | 0) + 16 >> 2] | 0;
                        ae = b;
                        var tessellationLimit6 = 0;
                        do {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            aj = c[c[(c[ae + 4 >> 2] | 0) + 4 >> 2] >> 2] | 0;
                            ae = aj;
                            ak = aj;
                            al = c[ak >> 2] | 0;
                        } while ((c[al + 16 >> 2] | 0) == (J | 0));
                        J = aj + 24 | 0;
                        do {
                            if ((c[J >> 2] | 0) == 0) {
                                am = ae
                            } else {
                                ag = aj + 4 | 0;
                                P = a7(c[(c[c[c[(c[ag >> 2] | 0) + 8 >> 2] >> 2] >> 2] | 0) + 4 >> 2] | 0, c[al + 12 >> 2] | 0) | 0;
                                if ((P | 0) == 0) {
                                    an = a + 3384 | 0;
                                    aw(an | 0, 1);
                                    return 0
                                }
                                if ((c[J >> 2] | 0) == 0) {
                                    ay(512, 171, 2600, 752);
                                    return 0
                                }
                                if ((a5(c[ak >> 2] | 0) | 0) == 0) {
                                    an = a + 3384 | 0;
                                    aw(an | 0, 1);
                                    return 0
                                } else {
                                    c[J >> 2] = 0;
                                    c[ak >> 2] = P;
                                    c[P + 24 >> 2] = ae;
                                    am = c[c[(c[ag >> 2] | 0) + 4 >> 2] >> 2] | 0;
                                    break
                                }
                            }
                        } while (0);
                        if ((am | 0) == 0) {
                            an = a + 3384 | 0;
                            aw(an | 0, 1);
                            return 0
                        }
                        ae = c[c[(c[am + 4 >> 2] | 0) + 8 >> 2] >> 2] | 0;
                        J = c[ae >> 2] | 0;
                        bJ(a, ae, m) | 0;
                        bK(a, am, c[(c[J + 4 >> 2] | 0) + 12 >> 2] | 0, J, J, 1);
                        K = 1;
                        i = d;
                        return K | 0
                    }
                    if ((v | 0) == (E | 0)) {
                        J = c[w >> 2] | 0;
                        ae = a6(J) | 0;
                        if ((ae | 0) == 0) {
                            ao = a + 3384 | 0;
                            aw(ao | 0, 1);
                            return 0
                        }
                        ag = c[ae + 4 >> 2] | 0;
                        ae = J + 4 | 0;
                        P = c[ae >> 2] | 0;
                        M = c[(c[P + 4 >> 2] | 0) + 12 >> 2] | 0;
                        Y = P + 8 | 0;
                        ad = c[Y >> 2] | 0;
                        C = M + 8 | 0;
                        z = c[C >> 2] | 0;
                        c[(c[ad + 4 >> 2] | 0) + 12 >> 2] = M;
                        c[(c[z + 4 >> 2] | 0) + 12 >> 2] = P;
                        c[Y >> 2] = z;
                        c[C >> 2] = ad;
                        ad = c[ae >> 2] | 0;
                        C = ad + 8 | 0;
                        z = c[C >> 2] | 0;
                        Y = ag + 8 | 0;
                        P = c[Y >> 2] | 0;
                        c[(c[z + 4 >> 2] | 0) + 12 >> 2] = ag;
                        c[(c[P + 4 >> 2] | 0) + 12 >> 2] = ad;
                        c[C >> 2] = P;
                        c[Y >> 2] = z;
                        c[(c[ae >> 2] | 0) + 16 >> 2] = c[ag + 16 >> 2];
                        z = ag + 4 | 0;
                        Y = c[z >> 2] | 0;
                        c[(c[Y + 16 >> 2] | 0) + 8 >> 2] = Y;
                        c[(c[z >> 2] | 0) + 20 >> 2] = c[(c[ae >> 2] | 0) + 20 >> 2];
                        c[ag + 28 >> 2] = c[J + 28 >> 2];
                        c[(c[z >> 2] | 0) + 28 >> 2] = c[(c[ae >> 2] | 0) + 28 >> 2];
                        if ((ag | 0) == 0) {
                            ao = a + 3384 | 0;
                            aw(ao | 0, 1);
                            return 0
                        }
                        if ((a4(c[o + 12 >> 2] | 0, c[(c[w >> 2] | 0) + 12 >> 2] | 0) | 0) == 0) {
                            aw(a + 3384 | 0, 1);
                            return 0
                        }
                        ag = c[(c[(c[n >> 2] | 0) + 4 >> 2] | 0) + 16 >> 2] | 0;
                        ae = b;
                        var tessellationLimit6 = 0;
                        do {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            ap = c[c[(c[ae + 4 >> 2] | 0) + 4 >> 2] >> 2] | 0;
                            ae = ap;
                        } while ((c[(c[(c[ap >> 2] | 0) + 4 >> 2] | 0) + 16 >> 2] | 0) == (ag | 0));
                        ag = c[(c[(c[c[c[(c[ap + 4 >> 2] | 0) + 8 >> 2] >> 2] >> 2] | 0) + 4 >> 2] | 0) + 8 >> 2] | 0;
                        c[n >> 2] = c[(c[w >> 2] | 0) + 12 >> 2];
                        z = c[(bJ(a, b, 0) | 0) + 8 >> 2] | 0;
                        bK(a, ae, z, c[(c[u >> 2] | 0) + 8 >> 2] | 0, ag, 1);
                        K = 1;
                        i = d;
                        return K | 0
                    }
                    do {
                        if (A >= T) {
                            if (!L) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (I <= +h[E + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    do {
                        if (T >= $) {
                            if (T != $) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[E + 48 >> 3] <= aa) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    Z = T - A;
                    W = $ - T;
                    if (Z + W > 0.0) {
                        _ = +h[E + 48 >> 3];
                        if (Z * (_ - aa) + W * (_ - I) < 0.0) {
                            aq = E;
                            ar = y;
                            as = T
                        } else {
                            Q = 1465
                        }
                    } else {
                        Q = 1465
                    }
                    do {
                        if ((Q | 0) == 1465) {
                            c[b + 20 >> 2] = 1;
                            c[(c[c[(c[k >> 2] | 0) + 4 >> 2] >> 2] | 0) + 20 >> 2] = 1;
                            L = c[u >> 2] | 0;
                            ae = a6(L) | 0;
                            if ((ae | 0) == 0) {
                                at = a + 3384 | 0;
                                aw(at | 0, 1);
                                return 0
                            }
                            ag = c[ae + 4 >> 2] | 0;
                            ae = L + 4 | 0;
                            z = c[ae >> 2] | 0;
                            J = c[(c[z + 4 >> 2] | 0) + 12 >> 2] | 0;
                            Y = z + 8 | 0;
                            P = c[Y >> 2] | 0;
                            C = J + 8 | 0;
                            ad = c[C >> 2] | 0;
                            c[(c[P + 4 >> 2] | 0) + 12 >> 2] = J;
                            c[(c[ad + 4 >> 2] | 0) + 12 >> 2] = z;
                            c[Y >> 2] = ad;
                            c[C >> 2] = P;
                            P = c[ae >> 2] | 0;
                            C = P + 8 | 0;
                            ad = c[C >> 2] | 0;
                            Y = ag + 8 | 0;
                            z = c[Y >> 2] | 0;
                            c[(c[ad + 4 >> 2] | 0) + 12 >> 2] = ag;
                            c[(c[z + 4 >> 2] | 0) + 12 >> 2] = P;
                            c[C >> 2] = z;
                            c[Y >> 2] = ad;
                            c[(c[ae >> 2] | 0) + 16 >> 2] = c[ag + 16 >> 2];
                            ad = ag + 4 | 0;
                            Y = c[ad >> 2] | 0;
                            c[(c[Y + 16 >> 2] | 0) + 8 >> 2] = Y;
                            c[(c[ad >> 2] | 0) + 20 >> 2] = c[(c[ae >> 2] | 0) + 20 >> 2];
                            c[ag + 28 >> 2] = c[L + 28 >> 2];
                            c[(c[ad >> 2] | 0) + 28 >> 2] = c[(c[ae >> 2] | 0) + 28 >> 2];
                            if ((ag | 0) == 0) {
                                at = a + 3384 | 0;
                                aw(at | 0, 1);
                                return 0
                            } else {
                                h[(c[q >> 2] | 0) + 40 >> 3] = +h[(c[B >> 2] | 0) + 40 >> 3];
                                h[(c[q >> 2] | 0) + 48 >> 3] = +h[(c[B >> 2] | 0) + 48 >> 3];
                                ag = c[B >> 2] | 0;
                                aq = ag;
                                ar = +h[x >> 3];
                                as = +h[ag + 40 >> 3];
                                break
                            }
                        }
                    } while (0);
                    do {
                        if (ar >= as) {
                            if (ar != as) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[N >> 3] <= +h[aq + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    do {
                        if (as >= $) {
                            if (as != $) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[aq + 48 >> 3] <= aa) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    _ = as - ar;
                    W = $ - as;
                    do {
                        if (_ + W > 0.0) {
                            Z = +h[aq + 48 >> 3];
                            if (_ * (Z - aa) + W * (Z - +h[N >> 3]) > 0.0) {
                                K = 0
                            } else {
                                break
                            }
                            i = d;
                            return K | 0
                        }
                    } while (0);
                    c[l + 20 >> 2] = 1;
                    c[b + 20 >> 2] = 1;
                    ag = c[w >> 2] | 0;
                    ae = a6(ag) | 0;
                    if ((ae | 0) == 0) {
                        au = a + 3384 | 0;
                        aw(au | 0, 1);
                        return 0
                    }
                    ad = c[ae + 4 >> 2] | 0;
                    ae = ag + 4 | 0;
                    L = c[ae >> 2] | 0;
                    Y = c[(c[L + 4 >> 2] | 0) + 12 >> 2] | 0;
                    z = L + 8 | 0;
                    C = c[z >> 2] | 0;
                    P = Y + 8 | 0;
                    J = c[P >> 2] | 0;
                    c[(c[C + 4 >> 2] | 0) + 12 >> 2] = Y;
                    c[(c[J + 4 >> 2] | 0) + 12 >> 2] = L;
                    c[z >> 2] = J;
                    c[P >> 2] = C;
                    C = c[ae >> 2] | 0;
                    P = C + 8 | 0;
                    J = c[P >> 2] | 0;
                    z = ad + 8 | 0;
                    L = c[z >> 2] | 0;
                    c[(c[J + 4 >> 2] | 0) + 12 >> 2] = ad;
                    c[(c[L + 4 >> 2] | 0) + 12 >> 2] = C;
                    c[P >> 2] = L;
                    c[z >> 2] = J;
                    c[(c[ae >> 2] | 0) + 16 >> 2] = c[ad + 16 >> 2];
                    J = ad + 4 | 0;
                    z = c[J >> 2] | 0;
                    c[(c[z + 16 >> 2] | 0) + 8 >> 2] = z;
                    c[(c[J >> 2] | 0) + 20 >> 2] = c[(c[ae >> 2] | 0) + 20 >> 2];
                    c[ad + 28 >> 2] = c[ag + 28 >> 2];
                    c[(c[J >> 2] | 0) + 28 >> 2] = c[(c[ae >> 2] | 0) + 28 >> 2];
                    if ((ad | 0) == 0) {
                        au = a + 3384 | 0;
                        aw(au | 0, 1);
                        return 0
                    }
                    h[(c[s >> 2] | 0) + 40 >> 3] = +h[(c[B >> 2] | 0) + 40 >> 3];
                    h[(c[s >> 2] | 0) + 48 >> 3] = +h[(c[B >> 2] | 0) + 48 >> 3];
                    K = 0;
                    i = d;
                    return K | 0
                }
            } while (0);
            bL(a, b) | 0;
            K = 0;
            i = d;
            return K | 0
        }

        function bO(a, b, c, d) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            var e = 0.0,
                f = 0.0,
                i = 0.0,
                j = 0.0,
                k = 0.0,
                l = 0.0,
                m = 0.0,
                n = 0,
                o = 0;
            e = +h[a + 40 >> 3];
            f = +h[b + 40 >> 3] - e;
            if (f < 0.0) {
                i = -0.0 - f
            } else {
                i = f
            }
            f = +h[a + 48 >> 3];
            j = +h[b + 48 >> 3] - f;
            if (j < 0.0) {
                k = -0.0 - j
            } else {
                k = j
            }
            j = i + k;
            k = +h[c + 40 >> 3] - e;
            if (k < 0.0) {
                l = -0.0 - k
            } else {
                l = k
            }
            k = +h[c + 48 >> 3] - f;
            if (k < 0.0) {
                m = -0.0 - k
            } else {
                m = k
            }
            k = l + m;
            m = j + k;
            l = k * .5 / m;
            g[d >> 2] = l;
            k = j * .5 / m;
            n = d + 4 | 0;
            g[n >> 2] = k;
            o = a + 16 | 0;
            h[o >> 3] = +h[o >> 3] + (l * +h[b + 16 >> 3] + k * +h[c + 16 >> 3]);
            o = a + 24 | 0;
            h[o >> 3] = +h[o >> 3] + (+g[d >> 2] * +h[b + 24 >> 3] + +g[n >> 2] * +h[c + 24 >> 3]);
            o = a + 32 | 0;
            h[o >> 3] = +h[o >> 3] + (+g[d >> 2] * +h[b + 32 >> 3] + +g[n >> 2] * +h[c + 32 >> 3]);
            return
        }

        function bP(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            var e = 0,
                f = 0,
                g = 0.0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0;
            e = c[b >> 2] | 0;
            f = c[e + 16 >> 2] | 0;
            g = +h[d + 40 >> 3];
            do {
                if (+h[f + 40 >> 3] == g) {
                    if (+h[f + 48 >> 3] != +h[d + 48 >> 3]) {
                        break
                    }
                    ay(512, 957, 2648, 904)
                }
            } while (0);
            f = c[e + 4 >> 2] | 0;
            i = c[f + 16 >> 2] | 0;
            do {
                if (+h[i + 40 >> 3] == g) {
                    if (+h[i + 48 >> 3] != +h[d + 48 >> 3]) {
                        break
                    }
                    ay(512, 978, 2648, 904)
                }
            } while (0);
            i = a6(f) | 0;
            if ((i | 0) == 0) {
                j = a + 3384 | 0;
                aw(j | 0, 1)
            }
            k = c[i + 4 >> 2] | 0;
            i = f + 4 | 0;
            l = c[i >> 2] | 0;
            m = c[(c[l + 4 >> 2] | 0) + 12 >> 2] | 0;
            n = l + 8 | 0;
            o = c[n >> 2] | 0;
            p = m + 8 | 0;
            q = c[p >> 2] | 0;
            c[(c[o + 4 >> 2] | 0) + 12 >> 2] = m;
            c[(c[q + 4 >> 2] | 0) + 12 >> 2] = l;
            c[n >> 2] = q;
            c[p >> 2] = o;
            o = c[i >> 2] | 0;
            p = o + 8 | 0;
            q = c[p >> 2] | 0;
            n = k + 8 | 0;
            l = c[n >> 2] | 0;
            c[(c[q + 4 >> 2] | 0) + 12 >> 2] = k;
            c[(c[l + 4 >> 2] | 0) + 12 >> 2] = o;
            c[p >> 2] = l;
            c[n >> 2] = q;
            c[(c[i >> 2] | 0) + 16 >> 2] = c[k + 16 >> 2];
            q = k + 4 | 0;
            n = c[q >> 2] | 0;
            c[(c[n + 16 >> 2] | 0) + 8 >> 2] = n;
            c[(c[q >> 2] | 0) + 20 >> 2] = c[(c[i >> 2] | 0) + 20 >> 2];
            c[k + 28 >> 2] = c[f + 28 >> 2];
            c[(c[q >> 2] | 0) + 28 >> 2] = c[(c[i >> 2] | 0) + 28 >> 2];
            if ((k | 0) == 0) {
                j = a + 3384 | 0;
                aw(j | 0, 1)
            }
            j = b + 24 | 0;
            do {
                if ((c[j >> 2] | 0) != 0) {
                    if ((a5(c[e + 8 >> 2] | 0) | 0) == 0) {
                        aw(a + 3384 | 0, 1)
                    } else {
                        c[j >> 2] = 0;
                        break
                    }
                }
            } while (0);
            if ((a4(c[d + 8 >> 2] | 0, e) | 0) == 0) {
                aw(a + 3384 | 0, 1)
            } else {
                bI(a, d);
                return
            }
        }

        function bQ(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            var e = 0,
                f = 0,
                g = 0,
                i = 0,
                j = 0.0,
                k = 0.0,
                l = 0,
                m = 0.0,
                n = 0.0,
                o = 0.0,
                p = 0,
                q = 0,
                r = 0.0,
                s = 0.0,
                t = 0.0;
            e = c[a + 112 >> 2] | 0;
            a = c[b >> 2] | 0;
            b = c[d >> 2] | 0;
            d = c[(c[a + 4 >> 2] | 0) + 16 >> 2] | 0;
            f = c[(c[b + 4 >> 2] | 0) + 16 >> 2] | 0;
            g = (f | 0) == (e | 0);
            if ((d | 0) != (e | 0)) {
                i = c[a + 16 >> 2] | 0;
                j = +h[d + 40 >> 3];
                k = +h[e + 40 >> 3];
                l = j < k;
                if (g) {
                    do {
                        if (!l) {
                            if (j != k) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[d + 48 >> 3] <= +h[e + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    m = +h[i + 40 >> 3];
                    do {
                        if (k >= m) {
                            if (k != m) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[e + 48 >> 3] <= +h[i + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    n = k - j;
                    o = m - k;
                    if (n + o <= 0.0) {
                        p = 1;
                        q = p & 1;
                        return q | 0
                    }
                    m = +h[e + 48 >> 3];
                    p = n * (m - +h[i + 48 >> 3]) + o * (m - +h[d + 48 >> 3]) >= 0.0;
                    q = p & 1;
                    return q | 0
                }
                do {
                    if (!l) {
                        if (j != k) {
                            ay(688, 61, 2480, 1384);
                            return 0
                        }
                        if (+h[d + 48 >> 3] <= +h[e + 48 >> 3]) {
                            break
                        }
                        ay(688, 61, 2480, 1384);
                        return 0
                    }
                } while (0);
                m = +h[i + 40 >> 3];
                do {
                    if (k >= m) {
                        if (k != m) {
                            ay(688, 61, 2480, 1384);
                            return 0
                        }
                        if (+h[e + 48 >> 3] <= +h[i + 48 >> 3]) {
                            break
                        }
                        ay(688, 61, 2480, 1384);
                        return 0
                    }
                } while (0);
                o = k - j;
                j = m - k;
                m = o + j;
                do {
                    if (m > 0.0) {
                        n = +h[e + 48 >> 3];
                        if (o < j) {
                            r = +h[d + 48 >> 3];
                            s = n - r + (r - +h[i + 48 >> 3]) * (o / m);
                            break
                        } else {
                            r = +h[i + 48 >> 3];
                            s = n - r + (r - +h[d + 48 >> 3]) * (j / m);
                            break
                        }
                    } else {
                        s = 0.0
                    }
                } while (0);
                d = c[b + 16 >> 2] | 0;
                m = +h[f + 40 >> 3];
                do {
                    if (m >= k) {
                        if (m != k) {
                            ay(688, 61, 2480, 1384);
                            return 0
                        }
                        if (+h[f + 48 >> 3] <= +h[e + 48 >> 3]) {
                            break
                        }
                        ay(688, 61, 2480, 1384);
                        return 0
                    }
                } while (0);
                j = +h[d + 40 >> 3];
                do {
                    if (k >= j) {
                        if (k != j) {
                            ay(688, 61, 2480, 1384);
                            return 0
                        }
                        if (+h[e + 48 >> 3] <= +h[d + 48 >> 3]) {
                            break
                        }
                        ay(688, 61, 2480, 1384);
                        return 0
                    }
                } while (0);
                o = k - m;
                m = j - k;
                k = o + m;
                do {
                    if (k > 0.0) {
                        j = +h[e + 48 >> 3];
                        if (o < m) {
                            r = +h[f + 48 >> 3];
                            t = j - r + (r - +h[d + 48 >> 3]) * (o / k);
                            break
                        } else {
                            r = +h[d + 48 >> 3];
                            t = j - r + (r - +h[f + 48 >> 3]) * (m / k);
                            break
                        }
                    } else {
                        t = 0.0
                    }
                } while (0);
                p = s >= t;
                q = p & 1;
                return q | 0
            }
            if (!g) {
                g = c[b + 16 >> 2] | 0;
                t = +h[f + 40 >> 3];
                s = +h[e + 40 >> 3];
                do {
                    if (t >= s) {
                        if (t != s) {
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                        if (+h[f + 48 >> 3] <= +h[e + 48 >> 3]) {
                            break
                        }
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                } while (0);
                k = +h[g + 40 >> 3];
                do {
                    if (s >= k) {
                        if (s != k) {
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                        if (+h[e + 48 >> 3] <= +h[g + 48 >> 3]) {
                            break
                        }
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                } while (0);
                m = s - t;
                t = k - s;
                if (m + t <= 0.0) {
                    p = 1;
                    q = p & 1;
                    return q | 0
                }
                s = +h[e + 48 >> 3];
                p = m * (s - +h[g + 48 >> 3]) + t * (s - +h[f + 48 >> 3]) <= 0.0;
                q = p & 1;
                return q | 0
            }
            f = c[a + 16 >> 2] | 0;
            s = +h[f + 40 >> 3];
            a = c[b + 16 >> 2] | 0;
            t = +h[a + 40 >> 3];
            b = s < t;
            do {
                if (!b) {
                    g = s == t;
                    if (g) {
                        if (+h[f + 48 >> 3] <= +h[a + 48 >> 3]) {
                            break
                        }
                    }
                    m = +h[e + 40 >> 3];
                    do {
                        if (m >= t) {
                            if (m != t) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[e + 48 >> 3] <= +h[a + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    do {
                        if (t >= s) {
                            if (!g) {
                                ay(688, 85, 2464, 1384);
                                return 0
                            }
                            if (+h[a + 48 >> 3] <= +h[f + 48 >> 3]) {
                                break
                            }
                            ay(688, 85, 2464, 1384);
                            return 0
                        }
                    } while (0);
                    k = t - m;
                    o = s - t;
                    if (k + o <= 0.0) {
                        p = 1;
                        q = p & 1;
                        return q | 0
                    }
                    r = +h[a + 48 >> 3];
                    p = k * (r - +h[f + 48 >> 3]) + o * (r - +h[e + 48 >> 3]) >= 0.0;
                    q = p & 1;
                    return q | 0
                }
            } while (0);
            r = +h[e + 40 >> 3];
            do {
                if (r >= s) {
                    if (r != s) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[e + 48 >> 3] <= +h[f + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            do {
                if (!b) {
                    if (s != t) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    }
                    if (+h[f + 48 >> 3] <= +h[a + 48 >> 3]) {
                        break
                    }
                    ay(688, 85, 2464, 1384);
                    return 0
                }
            } while (0);
            o = s - r;
            r = t - s;
            if (o + r <= 0.0) {
                p = 1;
                q = p & 1;
                return q | 0
            }
            s = +h[f + 48 >> 3];
            p = o * (s - +h[a + 48 >> 3]) + r * (s - +h[e + 48 >> 3]) <= 0.0;
            q = p & 1;
            return q | 0
        }

        function bR(a, b) {
            a = a | 0;
            b = +b;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                i = 0;
            d = b0(28) | 0;
            if ((d | 0) == 0) {
                aw(a + 3384 | 0, 1)
            }
            e = a3(c[a + 8 >> 2] | 0) | 0;
            if ((e | 0) == 0) {
                aw(a + 3384 | 0, 1)
            }
            f = e + 16 | 0;
            h[(c[f >> 2] | 0) + 40 >> 3] = 4.0e+150;
            h[(c[f >> 2] | 0) + 48 >> 3] = b;
            f = e + 4 | 0;
            h[(c[(c[f >> 2] | 0) + 16 >> 2] | 0) + 40 >> 3] = -4.0e+150;
            h[(c[(c[f >> 2] | 0) + 16 >> 2] | 0) + 48 >> 3] = b;
            c[a + 112 >> 2] = c[(c[f >> 2] | 0) + 16 >> 2];
            c[d >> 2] = e;
            c[d + 8 >> 2] = 0;
            c[d + 12 >> 2] = 0;
            c[d + 24 >> 2] = 0;
            c[d + 16 >> 2] = 1;
            c[d + 20 >> 2] = 0;
            e = c[a + 104 >> 2] | 0;
            f = e + 16 | 0;
            g = e + 12 | 0;
            i = e | 0;
            var tessellationLimit3 = 0;
            do {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                i = c[i + 8 >> 2] | 0;
                e = c[i >> 2] | 0;
                if ((e | 0) == 0) {
                    break
                }
            } while ((aK[c[f >> 2] & 3](c[g >> 2] | 0, e, d) | 0) == 0);
            g = b0(12) | 0;
            f = g;
            if ((g | 0) == 0) {
                c[d + 4 >> 2] = 0;
                aw(a + 3384 | 0, 1)
            } else {
                c[g >> 2] = d;
                a = i + 4 | 0;
                c[g + 4 >> 2] = c[a >> 2];
                c[(c[a >> 2] | 0) + 8 >> 2] = f;
                c[g + 8 >> 2] = i;
                c[a >> 2] = f;
                c[d + 4 >> 2] = f;
                return
            }
        }

        function bS(a, b) {
            a = a | 0;
            b = b | 0;
            return
        }

        function bT(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0;
            d = b + 12 | 0;
            e = c[d >> 2] | 0;
            if ((e | 0) == 0) {
                c[d >> 2] = a;
                return
            }
            d = b + 8 | 0;
            f = c[d >> 2] | 0;
            if ((f | 0) == 0) {
                c[d >> 2] = a;
                return
            } else {
                g = c[e + 24 >> 2] | 0;
                e = c[f + 24 >> 2] | 0;
                f = c[a + 24 >> 2] | 0;
                h = b0(16) | 0;
                i = b | 0;
                c[h + 12 >> 2] = c[i >> 2];
                c[h >> 2] = g;
                c[h + 4 >> 2] = e;
                c[h + 8 >> 2] = f;
                f = b + 4 | 0;
                c[f >> 2] = (c[f >> 2] | 0) + 1;
                c[i >> 2] = h;
                c[d >> 2] = a;
                return
            }
        }

        function bU(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0;
            d = b + 8 | 0;
            e = c[d >> 2] | 0;
            if ((e | 0) == 0) {
                c[d >> 2] = a;
                return
            }
            f = b + 12 | 0;
            g = c[f >> 2] | 0;
            if ((g | 0) == 0) {
                c[f >> 2] = a;
                return
            }
            h = b + 24 | 0;
            if ((c[h >> 2] | 0) == 0) {
                i = c[e + 24 >> 2] | 0;
                j = c[g + 24 >> 2] | 0;
                k = c[a + 24 >> 2] | 0;
                l = b0(16) | 0;
                m = b | 0;
                c[l + 12 >> 2] = c[m >> 2];
                c[l >> 2] = i;
                c[l + 4 >> 2] = j;
                c[l + 8 >> 2] = k;
                k = b + 4 | 0;
                c[k >> 2] = (c[k >> 2] | 0) + 1;
                c[m >> 2] = l
            } else {
                l = c[g + 24 >> 2] | 0;
                g = c[e + 24 >> 2] | 0;
                e = c[a + 24 >> 2] | 0;
                m = b0(16) | 0;
                k = b | 0;
                c[m + 12 >> 2] = c[k >> 2];
                c[m >> 2] = l;
                c[m + 4 >> 2] = g;
                c[m + 8 >> 2] = e;
                e = b + 4 | 0;
                c[e >> 2] = (c[e >> 2] | 0) + 1;
                c[k >> 2] = m
            }
            c[h >> 2] = (c[h >> 2] | 0) == 0 & 1;
            c[d >> 2] = c[f >> 2];
            c[f >> 2] = a;
            return
        }

        function bV(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0;
            d = b + 12 | 0;
            e = c[d >> 2] | 0;
            if ((e | 0) == 0) {
                c[d >> 2] = a;
                return
            }
            f = b + 8 | 0;
            g = c[f >> 2] | 0;
            if ((g | 0) == 0) {
                c[f >> 2] = a;
                return
            } else {
                h = c[e + 24 >> 2] | 0;
                e = c[g + 24 >> 2] | 0;
                g = c[a + 24 >> 2] | 0;
                a = b0(16) | 0;
                i = b | 0;
                c[a + 12 >> 2] = c[i >> 2];
                c[a >> 2] = h;
                c[a + 4 >> 2] = e;
                c[a + 8 >> 2] = g;
                g = b + 4 | 0;
                c[g >> 2] = (c[g >> 2] | 0) + 1;
                c[i >> 2] = a;
                c[d >> 2] = 0;
                c[f >> 2] = 0;
                return
            }
        }

        function bW(a, b) {
            a = a | 0;
            b = b | 0;
            aI[c[b + 28 >> 2] & 31](a, b);
            return
        }

        function bX(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0;
            d = i;
            c[b + 12 >> 2] = 0;
            c[b + 8 >> 2] = 0;
            c[b + 24 >> 2] = 0;
            if ((a | 0) == 5) {
                c[b + 28 >> 2] = 18;
                i = d;
                return
            } else if ((a | 0) == 6) {
                c[b + 28 >> 2] = 12;
                i = d;
                return
            } else if ((a | 0) == 4) {
                c[b + 28 >> 2] = 8;
                i = d;
                return
            } else {
                aq(c[m >> 2] | 0, 176, (t = i, i = i + 8 | 0, c[t >> 2] = a, t) | 0) | 0;
                c[b + 28 >> 2] = 4;
                i = d;
                return
            }
        }

        function bY(a, b, d, e, f) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            e = e | 0;
            f = f | 0;
            var g = 0.0,
                i = 0.0;
            g = +h[a >> 3];
            i = +h[a + 8 >> 3];
            a = b0(32) | 0;
            d = a;
            b = f + 16 | 0;
            c[a + 28 >> 2] = c[b >> 2];
            h[a >> 3] = g;
            h[a + 8 >> 3] = i;
            h[a + 16 >> 3] = 0.0;
            f = c[b >> 2] | 0;
            if ((f | 0) == 0) {
                c[a + 24 >> 2] = 0;
                c[b >> 2] = d;
                c[e >> 2] = a;
                return
            } else {
                c[a + 24 >> 2] = (c[f + 24 >> 2] | 0) + 1;
                c[b >> 2] = d;
                c[e >> 2] = a;
                return
            }
        }

        function bZ(a, b, d, e, f) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            e = e | 0;
            f = f | 0;
            var g = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0;
            g = a + 16 | 0;
            i = (c[(c[g >> 2] | 0) + 24 >> 2] | 0) + 1 | 0;
            c[e >> 2] = i;
            e = a + 4 | 0;
            j = c[e >> 2] | 0;
            c[f >> 2] = j;
            c[b >> 2] = b0(i << 4) | 0;
            i = c[e >> 2] | 0;
            if ((i | 0) == 0) {
                k = 0
            } else {
                k = b0(i * 12 & -1) | 0
            }
            c[d >> 2] = k;
            k = c[g >> 2] | 0;
            if ((k | 0) != 0) {
                i = k;
                var tessellationLimit4 = 0;
                do {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    h[(c[b >> 2] | 0) + (c[i + 24 >> 2] << 1 << 3) >> 3] = +h[i >> 3];
                    k = c[g >> 2] | 0;
                    h[(c[b >> 2] | 0) + ((c[k + 24 >> 2] << 1 | 1) << 3) >> 3] = +h[k + 8 >> 3];
                    k = c[g >> 2] | 0;
                    i = c[k + 28 >> 2] | 0;
                    b1(k);
                    c[g >> 2] = i;
                } while ((i | 0) != 0)
            }
            i = a | 0;
            a = c[i >> 2] | 0;
            if ((a | 0) == 0) {
                return
            } else {
                l = j;
                m = a
            }
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                a = l * 3 & -1;
                c[(c[d >> 2] | 0) + (a - 3 << 2) >> 2] = c[m >> 2];
                c[(c[d >> 2] | 0) + (a - 2 << 2) >> 2] = c[(c[i >> 2] | 0) + 4 >> 2];
                c[(c[d >> 2] | 0) + (a - 1 << 2) >> 2] = c[(c[i >> 2] | 0) + 8 >> 2];
                a = c[i >> 2] | 0;
                j = c[a + 12 >> 2] | 0;
                b1(a);
                c[i >> 2] = j;
                if ((j | 0) == 0) {
                    break
                }
                l = l - 1 | 0;
                m = j
            }
            return
        }

        function b_(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0.0,
                f = 0.0,
                g = 0,
                i = 0,
                j = 0.0,
                k = 0,
                l = 0.0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0.0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0;
            b = c[a + 8 >> 2] | 0;
            a = c[b + 12 >> 2] | 0;
            if ((a | 0) == (b | 0)) {
                ay(312, 82, 2408, 968);
                return 0
            }
            if ((c[a + 12 >> 2] | 0) == (b | 0)) {
                ay(312, 82, 2408, 968);
                return 0
            } else {
                d = b
            }
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                b = c[(c[d + 4 >> 2] | 0) + 16 >> 2] | 0;
                e = +h[b + 40 >> 3];
                a = c[d + 16 >> 2] | 0;
                f = +h[a + 40 >> 3];
                if (e >= f) {
                    if (e != f) {
                        g = d;
                        i = a;
                        j = f;
                        k = b;
                        l = e;
                        break
                    }
                    if (+h[b + 48 >> 3] > +h[a + 48 >> 3]) {
                        g = d;
                        i = a;
                        j = f;
                        k = b;
                        l = e;
                        break
                    }
                }
                d = c[(c[d + 8 >> 2] | 0) + 4 >> 2] | 0
            }
            var tessellationLimit = 0;
            while (1) {
                if (++tessellationLimit > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                if (j >= l) {
                    if (j != l) {
                        break
                    }
                    if (+h[i + 48 >> 3] > +h[k + 48 >> 3]) {
                        break
                    }
                }
                d = c[g + 12 >> 2] | 0;
                b = c[d + 16 >> 2] | 0;
                a = c[(c[d + 4 >> 2] | 0) + 16 >> 2] | 0;
                g = d;
                i = b;
                j = +h[b + 40 >> 3];
                k = a;
                l = +h[a + 40 >> 3]
            }
            k = c[(c[g + 8 >> 2] | 0) + 4 >> 2] | 0;
            L2344: do {
                if ((c[g + 12 >> 2] | 0) == (k | 0)) {
                    m = k;
                    n = g;
                    o = 1806
                } else {
                    i = k;
                    a = g;
                    L2345: {
                        var tessellationLimit4 = 0;
                        while (1) {
                            if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            b = i + 16 | 0;
                            d = i + 12 | 0;
                            p = a;
                            var tessellationLimit5 = 0;
                            while (1) {
                                if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                q = c[(c[p + 4 >> 2] | 0) + 16 >> 2] | 0;
                                l = +h[q + 40 >> 3];
                                r = c[b >> 2] | 0;
                                j = +h[r + 40 >> 3];
                                if (l < j) {
                                    break
                                }
                                if (l == j) {
                                    if (+h[q + 48 >> 3] <= +h[r + 48 >> 3]) {
                                        break
                                    }
                                }
                                r = c[d >> 2] | 0;
                                L2353: do {
                                    if ((r | 0) == (p | 0)) {
                                        s = p;
                                        t = p
                                    } else {
                                        q = p;
                                        u = r;
                                        var tessellationLimit9 = 0;
                                        while (1) {
                                            if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                                throw new Error("Tessellate limit exceeded");
                                            }
                                            v = c[(c[q + 8 >> 2] | 0) + 4 >> 2] | 0;
                                            w = c[v + 16 >> 2] | 0;
                                            j = +h[w + 40 >> 3];
                                            x = c[(c[v + 4 >> 2] | 0) + 16 >> 2] | 0;
                                            l = +h[x + 40 >> 3];
                                            do {
                                                if (j >= l) {
                                                    if (j == l) {
                                                        if (+h[w + 48 >> 3] <= +h[x + 48 >> 3]) {
                                                            break
                                                        }
                                                    }
                                                    y = c[(c[q + 4 >> 2] | 0) + 16 >> 2] | 0;
                                                    z = c[q + 16 >> 2] | 0;
                                                    e = +h[y + 40 >> 3];
                                                    f = +h[z + 40 >> 3];
                                                    if (e >= f) {
                                                        if (e != f) {
                                                            o = 1813;
                                                            break L2345
                                                        }
                                                        if (+h[y + 48 >> 3] > +h[z + 48 >> 3]) {
                                                            o = 1814;
                                                            break L2345
                                                        }
                                                    }
                                                    if (f >= j) {
                                                        if (f != j) {
                                                            o = 1816;
                                                            break L2345
                                                        }
                                                        if (+h[z + 48 >> 3] > +h[w + 48 >> 3]) {
                                                            o = 1815;
                                                            break L2345
                                                        }
                                                    }
                                                    A = f - e;
                                                    e = j - f;
                                                    if (A + e <= 0.0) {
                                                        break
                                                    }
                                                    f = +h[z + 48 >> 3];
                                                    if (A * (f - +h[w + 48 >> 3]) + e * (f - +h[y + 48 >> 3]) < 0.0) {
                                                        s = q;
                                                        t = u;
                                                        break L2353
                                                    }
                                                }
                                            } while (0);
                                            w = a7(q, v) | 0;
                                            if ((w | 0) == 0) {
                                                B = 0;
                                                o = 1820;
                                                break L2345
                                            }
                                            x = c[w + 4 >> 2] | 0;
                                            w = c[d >> 2] | 0;
                                            if ((w | 0) == (x | 0)) {
                                                s = x;
                                                t = x;
                                                break
                                            } else {
                                                q = x;
                                                u = w
                                            }
                                        }
                                    }
                                } while (0);
                                r = c[s + 12 >> 2] | 0;
                                if ((c[r + 12 >> 2] | 0) == (i | 0)) {
                                    C = r;
                                    D = i;
                                    E = t;
                                    break L2344
                                } else {
                                    p = r
                                }
                            }
                            b = c[d >> 2] | 0;
                            L2374: do {
                                if ((b | 0) == (p | 0)) {
                                    F = i
                                } else {
                                    r = i;
                                    u = b;
                                    var tessellationLimit9 = 0;
                                    while (1) {
                                        if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                            throw new Error("Tessellate limit exceeded");
                                        }
                                        q = c[(c[u + 4 >> 2] | 0) + 16 >> 2] | 0;
                                        j = +h[q + 40 >> 3];
                                        w = c[u + 16 >> 2] | 0;
                                        l = +h[w + 40 >> 3];
                                        do {
                                            if (j >= l) {
                                                if (j == l) {
                                                    if (+h[q + 48 >> 3] <= +h[w + 48 >> 3]) {
                                                        break
                                                    }
                                                }
                                                x = c[r + 16 >> 2] | 0;
                                                y = c[(c[r + 4 >> 2] | 0) + 16 >> 2] | 0;
                                                f = +h[x + 40 >> 3];
                                                e = +h[y + 40 >> 3];
                                                if (f >= e) {
                                                    if (f != e) {
                                                        o = 1826;
                                                        break L2345
                                                    }
                                                    if (+h[x + 48 >> 3] > +h[y + 48 >> 3]) {
                                                        o = 1827;
                                                        break L2345
                                                    }
                                                }
                                                if (e >= j) {
                                                    if (e != j) {
                                                        o = 1825;
                                                        break L2345
                                                    }
                                                    if (+h[y + 48 >> 3] > +h[q + 48 >> 3]) {
                                                        o = 1824;
                                                        break L2345
                                                    }
                                                }
                                                A = e - f;
                                                f = j - e;
                                                if (A + f <= 0.0) {
                                                    break
                                                }
                                                e = +h[y + 48 >> 3];
                                                if (A * (e - +h[q + 48 >> 3]) + f * (e - +h[x + 48 >> 3]) > 0.0) {
                                                    F = r;
                                                    break L2374
                                                }
                                            }
                                        } while (0);
                                        q = a7(u, r) | 0;
                                        if ((q | 0) == 0) {
                                            B = 0;
                                            o = 1823;
                                            break L2345
                                        }
                                        w = c[q + 4 >> 2] | 0;
                                        q = c[w + 12 >> 2] | 0;
                                        if ((q | 0) == (p | 0)) {
                                            F = w;
                                            break
                                        } else {
                                            r = w;
                                            u = q
                                        }
                                    }
                                }
                            } while (0);
                            b = c[(c[F + 8 >> 2] | 0) + 4 >> 2] | 0;
                            if ((c[p + 12 >> 2] | 0) == (b | 0)) {
                                m = b;
                                n = p;
                                o = 1806;
                                break L2344
                            } else {
                                i = b;
                                a = p
                            }
                        }
                    }
                    if ((o | 0) == 1826) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1827) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1815) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1816) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1813) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1814) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1823) {
                        return B | 0
                    } else if ((o | 0) == 1824) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1825) {
                        ay(688, 85, 2464, 1384);
                        return 0
                    } else if ((o | 0) == 1820) {
                        return B | 0
                    }
                }
            } while (0);
            if ((o | 0) == 1806) {
                C = n;
                D = m;
                E = c[m + 12 >> 2] | 0
            }
            if ((E | 0) == (C | 0)) {
                ay(312, 118, 2408, 736);
                return 0
            }
            if ((c[E + 12 >> 2] | 0) == (C | 0)) {
                B = 1;
                return B | 0
            } else {
                G = D;
                H = E
            }
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                E = a7(H, G) | 0;
                if ((E | 0) == 0) {
                    B = 0;
                    o = 1819;
                    break
                }
                D = c[E + 4 >> 2] | 0;
                E = c[D + 12 >> 2] | 0;
                if ((c[E + 12 >> 2] | 0) == (C | 0)) {
                    B = 1;
                    o = 1821;
                    break
                } else {
                    G = D;
                    H = E
                }
            }
            if ((o | 0) == 1821) {
                return B | 0
            } else if ((o | 0) == 1819) {
                return B | 0
            }
            return 0
        }

        function b$(a, b, d, e, f, g) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            e = e | 0;
            f = f | 0;
            g = g | 0;
            var i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0.0,
                v = 0.0,
                w = 0,
                x = 0,
                y = 0,
                z = 0;
            i = b0(3432) | 0;
            if ((i | 0) == 0) {
                j = 0
            } else {
                c[i >> 2] = 0;
                h[i + 88 >> 3] = 0.0;
                b9(i + 16 | 0, 0, 24);
                c[i + 96 >> 2] = 100130;
                c[i + 120 >> 2] = 0;
                c[i + 124 >> 2] = 0;
                c[i + 132 >> 2] = 10;
                c[i + 136 >> 2] = 16;
                c[i + 140 >> 2] = 20;
                c[i + 144 >> 2] = 2;
                c[i + 12 >> 2] = 18;
                c[i + 116 >> 2] = 2;
                c[i + 148 >> 2] = 8;
                c[i + 3360 >> 2] = 26;
                c[i + 3364 >> 2] = 20;
                c[i + 3368 >> 2] = 6;
                c[i + 3372 >> 2] = 4;
                c[i + 3376 >> 2] = 16;
                c[i + 3380 >> 2] = 4;
                c[i + 3424 >> 2] = 0;
                j = i
            }
            i = b0(32) | 0;
            k = i;
            b9(i | 0, 0, 20);
            c[i + 28 >> 2] = 4;
            c[i + 24 >> 2] = 0;
            c[j + 3368 >> 2] = 22;
            c[j + 3360 >> 2] = 2;
            c[j + 3380 >> 2] = 2;
            l = j | 0;
            if ((c[l >> 2] | 0) != 0) {
                br(j, 0)
            }
            c[l >> 2] = 1;
            m = j + 156 | 0;
            c[m >> 2] = 0;
            n = j + 152 | 0;
            c[n >> 2] = 0;
            c[j + 8 >> 2] = 0;
            c[j + 3424 >> 2] = i;
            o = j + 4 | 0;
            p = g - 4 | 0;
            g = i + 16 | 0;
            q = f;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                f = q + 4 | 0;
                r = c[q >> 2] | 0;
                s = c[f >> 2] | 0;
                c[l >> 2] = 2;
                c[o >> 2] = 0;
                if ((c[m >> 2] | 0) > 0) {
                    c[n >> 2] = 1
                }
                do {
                    if ((r | 0) != (s | 0)) {
                        t = r;
                        var tessellationLimit6 = 0;
                        do {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            u = +h[t >> 3];
                            v = +h[t + 8 >> 3];
                            w = b0(32) | 0;
                            c[w + 28 >> 2] = c[g >> 2];
                            x = w;
                            h[x >> 3] = u;
                            h[w + 8 >> 3] = v;
                            h[w + 16 >> 3] = 0.0;
                            y = c[g >> 2] | 0;
                            if ((y | 0) == 0) {
                                c[w + 24 >> 2] = 0
                            } else {
                                c[w + 24 >> 2] = (c[y + 24 >> 2] | 0) + 1
                            }
                            c[g >> 2] = w;
                            t = t + 16 | 0;
                            bu(j, x, w);
                        } while ((t | 0) != (s | 0));
                        if ((c[l >> 2] | 0) == 2) {
                            break
                        }
                        br(j, 2)
                    }
                } while (0);
                c[l >> 2] = 1;
                if ((f | 0) == (p | 0)) {
                    break
                } else {
                    q = f
                }
            }
            bw(j);
            bZ(k, a, d, b, e);
            b1(i);
            if ((c[l >> 2] | 0) == 0) {
                z = j;
                b1(z);
                return
            }
            br(j, 0);
            z = j;
            b1(z);
            return
        }

        function b0(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0,
                P = 0,
                Q = 0,
                R = 0,
                S = 0,
                T = 0,
                U = 0,
                V = 0,
                W = 0,
                X = 0,
                Y = 0,
                Z = 0,
                _ = 0,
                $ = 0,
                aa = 0,
                ab = 0,
                ac = 0,
                ad = 0,
                ae = 0,
                af = 0,
                ag = 0,
                ah = 0,
                ai = 0,
                aj = 0,
                ak = 0,
                al = 0,
                am = 0,
                an = 0,
                aq = 0,
                ar = 0,
                as = 0,
                at = 0,
                au = 0,
                av = 0,
                aw = 0,
                ax = 0,
                ay = 0,
                az = 0,
                aA = 0,
                aD = 0,
                aE = 0,
                aG = 0,
                aH = 0,
                aI = 0,
                aJ = 0,
                aK = 0,
                aL = 0;
            do {
                if (a >>> 0 < 245) {
                    if (a >>> 0 < 11) {
                        b = 16
                    } else {
                        b = a + 11 & -8
                    }
                    d = b >>> 3;
                    e = c[436] | 0;
                    f = e >>> (d >>> 0);
                    if ((f & 3 | 0) != 0) {
                        g = (f & 1 ^ 1) + d | 0;
                        h = g << 1;
                        i = 1784 + (h << 2) | 0;
                        j = 1784 + (h + 2 << 2) | 0;
                        h = c[j >> 2] | 0;
                        k = h + 8 | 0;
                        l = c[k >> 2] | 0;
                        do {
                            if ((i | 0) == (l | 0)) {
                                c[436] = e & (1 << g ^ -1)
                            } else {
                                if (l >>> 0 < (c[440] | 0) >>> 0) {
                                    ap();
                                    return 0;
                                    return 0
                                }
                                m = l + 12 | 0;
                                if ((c[m >> 2] | 0) == (h | 0)) {
                                    c[m >> 2] = i;
                                    c[j >> 2] = l;
                                    break
                                } else {
                                    ap();
                                    return 0;
                                    return 0
                                }
                            }
                        } while (0);
                        l = g << 3;
                        c[h + 4 >> 2] = l | 3;
                        j = h + (l | 4) | 0;
                        c[j >> 2] = c[j >> 2] | 1;
                        n = k;
                        return n | 0
                    }
                    if (b >>> 0 <= (c[438] | 0) >>> 0) {
                        o = b;
                        break
                    }
                    if ((f | 0) != 0) {
                        j = 2 << d;
                        l = f << d & (j | -j);
                        j = (l & -l) - 1 | 0;
                        l = j >>> 12 & 16;
                        i = j >>> (l >>> 0);
                        j = i >>> 5 & 8;
                        m = i >>> (j >>> 0);
                        i = m >>> 2 & 4;
                        p = m >>> (i >>> 0);
                        m = p >>> 1 & 2;
                        q = p >>> (m >>> 0);
                        p = q >>> 1 & 1;
                        r = (j | l | i | m | p) + (q >>> (p >>> 0)) | 0;
                        p = r << 1;
                        q = 1784 + (p << 2) | 0;
                        m = 1784 + (p + 2 << 2) | 0;
                        p = c[m >> 2] | 0;
                        i = p + 8 | 0;
                        l = c[i >> 2] | 0;
                        do {
                            if ((q | 0) == (l | 0)) {
                                c[436] = e & (1 << r ^ -1)
                            } else {
                                if (l >>> 0 < (c[440] | 0) >>> 0) {
                                    ap();
                                    return 0;
                                    return 0
                                }
                                j = l + 12 | 0;
                                if ((c[j >> 2] | 0) == (p | 0)) {
                                    c[j >> 2] = q;
                                    c[m >> 2] = l;
                                    break
                                } else {
                                    ap();
                                    return 0;
                                    return 0
                                }
                            }
                        } while (0);
                        l = r << 3;
                        m = l - b | 0;
                        c[p + 4 >> 2] = b | 3;
                        q = p;
                        e = q + b | 0;
                        c[q + (b | 4) >> 2] = m | 1;
                        c[q + l >> 2] = m;
                        l = c[438] | 0;
                        if ((l | 0) != 0) {
                            q = c[441] | 0;
                            d = l >>> 3;
                            l = d << 1;
                            f = 1784 + (l << 2) | 0;
                            k = c[436] | 0;
                            h = 1 << d;
                            do {
                                if ((k & h | 0) == 0) {
                                    c[436] = k | h;
                                    s = f;
                                    t = 1784 + (l + 2 << 2) | 0
                                } else {
                                    d = 1784 + (l + 2 << 2) | 0;
                                    g = c[d >> 2] | 0;
                                    if (g >>> 0 >= (c[440] | 0) >>> 0) {
                                        s = g;
                                        t = d;
                                        break
                                    }
                                    ap();
                                    return 0;
                                    return 0
                                }
                            } while (0);
                            c[t >> 2] = q;
                            c[s + 12 >> 2] = q;
                            c[q + 8 >> 2] = s;
                            c[q + 12 >> 2] = f
                        }
                        c[438] = m;
                        c[441] = e;
                        n = i;
                        return n | 0
                    }
                    l = c[437] | 0;
                    if ((l | 0) == 0) {
                        o = b;
                        break
                    }
                    h = (l & -l) - 1 | 0;
                    l = h >>> 12 & 16;
                    k = h >>> (l >>> 0);
                    h = k >>> 5 & 8;
                    p = k >>> (h >>> 0);
                    k = p >>> 2 & 4;
                    r = p >>> (k >>> 0);
                    p = r >>> 1 & 2;
                    d = r >>> (p >>> 0);
                    r = d >>> 1 & 1;
                    g = c[2048 + ((h | l | k | p | r) + (d >>> (r >>> 0)) << 2) >> 2] | 0;
                    r = g;
                    d = g;
                    p = (c[g + 4 >> 2] & -8) - b | 0;
                    var tessellationLimit5 = 0;
                    while (1) {
                        if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        g = c[r + 16 >> 2] | 0;
                        if ((g | 0) == 0) {
                            k = c[r + 20 >> 2] | 0;
                            if ((k | 0) == 0) {
                                break
                            } else {
                                u = k
                            }
                        } else {
                            u = g
                        }
                        g = (c[u + 4 >> 2] & -8) - b | 0;
                        k = g >>> 0 < p >>> 0;
                        r = u;
                        d = k ? u : d;
                        p = k ? g : p
                    }
                    r = d;
                    i = c[440] | 0;
                    if (r >>> 0 < i >>> 0) {
                        ap();
                        return 0;
                        return 0
                    }
                    e = r + b | 0;
                    m = e;
                    if (r >>> 0 >= e >>> 0) {
                        ap();
                        return 0;
                        return 0
                    }
                    e = c[d + 24 >> 2] | 0;
                    f = c[d + 12 >> 2] | 0;
                    do {
                        if ((f | 0) == (d | 0)) {
                            q = d + 20 | 0;
                            g = c[q >> 2] | 0;
                            if ((g | 0) == 0) {
                                k = d + 16 | 0;
                                l = c[k >> 2] | 0;
                                if ((l | 0) == 0) {
                                    v = 0;
                                    break
                                } else {
                                    w = l;
                                    x = k
                                }
                            } else {
                                w = g;
                                x = q
                            }
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                q = w + 20 | 0;
                                g = c[q >> 2] | 0;
                                if ((g | 0) != 0) {
                                    w = g;
                                    x = q;
                                    continue
                                }
                                q = w + 16 | 0;
                                g = c[q >> 2] | 0;
                                if ((g | 0) == 0) {
                                    break
                                } else {
                                    w = g;
                                    x = q
                                }
                            }
                            if (x >>> 0 < i >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[x >> 2] = 0;
                                v = w;
                                break
                            }
                        } else {
                            q = c[d + 8 >> 2] | 0;
                            if (q >>> 0 < i >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            g = q + 12 | 0;
                            if ((c[g >> 2] | 0) != (d | 0)) {
                                ap();
                                return 0;
                                return 0
                            }
                            k = f + 8 | 0;
                            if ((c[k >> 2] | 0) == (d | 0)) {
                                c[g >> 2] = f;
                                c[k >> 2] = q;
                                v = f;
                                break
                            } else {
                                ap();
                                return 0;
                                return 0
                            }
                        }
                    } while (0);
                    L2647: do {
                        if ((e | 0) != 0) {
                            f = d + 28 | 0;
                            i = 2048 + (c[f >> 2] << 2) | 0;
                            do {
                                if ((d | 0) == (c[i >> 2] | 0)) {
                                    c[i >> 2] = v;
                                    if ((v | 0) != 0) {
                                        break
                                    }
                                    c[437] = c[437] & (1 << c[f >> 2] ^ -1);
                                    break L2647
                                } else {
                                    if (e >>> 0 < (c[440] | 0) >>> 0) {
                                        ap();
                                        return 0;
                                        return 0
                                    }
                                    q = e + 16 | 0;
                                    if ((c[q >> 2] | 0) == (d | 0)) {
                                        c[q >> 2] = v
                                    } else {
                                        c[e + 20 >> 2] = v
                                    }
                                    if ((v | 0) == 0) {
                                        break L2647
                                    }
                                }
                            } while (0);
                            if (v >>> 0 < (c[440] | 0) >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            c[v + 24 >> 2] = e;
                            f = c[d + 16 >> 2] | 0;
                            do {
                                if ((f | 0) != 0) {
                                    if (f >>> 0 < (c[440] | 0) >>> 0) {
                                        ap();
                                        return 0;
                                        return 0
                                    } else {
                                        c[v + 16 >> 2] = f;
                                        c[f + 24 >> 2] = v;
                                        break
                                    }
                                }
                            } while (0);
                            f = c[d + 20 >> 2] | 0;
                            if ((f | 0) == 0) {
                                break
                            }
                            if (f >>> 0 < (c[440] | 0) >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[v + 20 >> 2] = f;
                                c[f + 24 >> 2] = v;
                                break
                            }
                        }
                    } while (0);
                    if (p >>> 0 < 16) {
                        e = p + b | 0;
                        c[d + 4 >> 2] = e | 3;
                        f = r + (e + 4) | 0;
                        c[f >> 2] = c[f >> 2] | 1
                    } else {
                        c[d + 4 >> 2] = b | 3;
                        c[r + (b | 4) >> 2] = p | 1;
                        c[r + (p + b) >> 2] = p;
                        f = c[438] | 0;
                        if ((f | 0) != 0) {
                            e = c[441] | 0;
                            i = f >>> 3;
                            f = i << 1;
                            q = 1784 + (f << 2) | 0;
                            k = c[436] | 0;
                            g = 1 << i;
                            do {
                                if ((k & g | 0) == 0) {
                                    c[436] = k | g;
                                    y = q;
                                    z = 1784 + (f + 2 << 2) | 0
                                } else {
                                    i = 1784 + (f + 2 << 2) | 0;
                                    l = c[i >> 2] | 0;
                                    if (l >>> 0 >= (c[440] | 0) >>> 0) {
                                        y = l;
                                        z = i;
                                        break
                                    }
                                    ap();
                                    return 0;
                                    return 0
                                }
                            } while (0);
                            c[z >> 2] = e;
                            c[y + 12 >> 2] = e;
                            c[e + 8 >> 2] = y;
                            c[e + 12 >> 2] = q
                        }
                        c[438] = p;
                        c[441] = m
                    }
                    f = d + 8 | 0;
                    if ((f | 0) == 0) {
                        o = b;
                        break
                    } else {
                        n = f
                    }
                    return n | 0
                } else {
                    if (a >>> 0 > 4294967231) {
                        o = -1;
                        break
                    }
                    f = a + 11 | 0;
                    g = f & -8;
                    k = c[437] | 0;
                    if ((k | 0) == 0) {
                        o = g;
                        break
                    }
                    r = -g | 0;
                    i = f >>> 8;
                    do {
                        if ((i | 0) == 0) {
                            A = 0
                        } else {
                            if (g >>> 0 > 16777215) {
                                A = 31;
                                break
                            }
                            f = (i + 1048320 | 0) >>> 16 & 8;
                            l = i << f;
                            h = (l + 520192 | 0) >>> 16 & 4;
                            j = l << h;
                            l = (j + 245760 | 0) >>> 16 & 2;
                            B = 14 - (h | f | l) + (j << l >>> 15) | 0;
                            A = g >>> ((B + 7 | 0) >>> 0) & 1 | B << 1
                        }
                    } while (0);
                    i = c[2048 + (A << 2) >> 2] | 0;
                    L2455: do {
                        if ((i | 0) == 0) {
                            C = 0;
                            D = r;
                            E = 0
                        } else {
                            if ((A | 0) == 31) {
                                F = 0
                            } else {
                                F = 25 - (A >>> 1) | 0
                            }
                            d = 0;
                            m = r;
                            p = i;
                            q = g << F;
                            e = 0;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                B = c[p + 4 >> 2] & -8;
                                l = B - g | 0;
                                if (l >>> 0 < m >>> 0) {
                                    if ((B | 0) == (g | 0)) {
                                        C = p;
                                        D = l;
                                        E = p;
                                        break L2455
                                    } else {
                                        G = p;
                                        H = l
                                    }
                                } else {
                                    G = d;
                                    H = m
                                }
                                l = c[p + 20 >> 2] | 0;
                                B = c[p + 16 + (q >>> 31 << 2) >> 2] | 0;
                                j = (l | 0) == 0 | (l | 0) == (B | 0) ? e : l;
                                if ((B | 0) == 0) {
                                    C = G;
                                    D = H;
                                    E = j;
                                    break
                                } else {
                                    d = G;
                                    m = H;
                                    p = B;
                                    q = q << 1;
                                    e = j
                                }
                            }
                        }
                    } while (0);
                    if ((E | 0) == 0 & (C | 0) == 0) {
                        i = 2 << A;
                        r = k & (i | -i);
                        if ((r | 0) == 0) {
                            o = g;
                            break
                        }
                        i = (r & -r) - 1 | 0;
                        r = i >>> 12 & 16;
                        e = i >>> (r >>> 0);
                        i = e >>> 5 & 8;
                        q = e >>> (i >>> 0);
                        e = q >>> 2 & 4;
                        p = q >>> (e >>> 0);
                        q = p >>> 1 & 2;
                        m = p >>> (q >>> 0);
                        p = m >>> 1 & 1;
                        I = c[2048 + ((i | r | e | q | p) + (m >>> (p >>> 0)) << 2) >> 2] | 0
                    } else {
                        I = E
                    }
                    if ((I | 0) == 0) {
                        J = D;
                        K = C
                    } else {
                        p = I;
                        m = D;
                        q = C;
                        var tessellationLimit6 = 0;
                        while (1) {
                            if (++tessellationLimit6 > TESSELLATION_LIMIT) {
                                throw new Error("Tessellate limit exceeded");
                            }
                            e = (c[p + 4 >> 2] & -8) - g | 0;
                            r = e >>> 0 < m >>> 0;
                            i = r ? e : m;
                            e = r ? p : q;
                            r = c[p + 16 >> 2] | 0;
                            if ((r | 0) != 0) {
                                p = r;
                                m = i;
                                q = e;
                                continue
                            }
                            r = c[p + 20 >> 2] | 0;
                            if ((r | 0) == 0) {
                                J = i;
                                K = e;
                                break
                            } else {
                                p = r;
                                m = i;
                                q = e
                            }
                        }
                    }
                    if ((K | 0) == 0) {
                        o = g;
                        break
                    }
                    if (J >>> 0 >= ((c[438] | 0) - g | 0) >>> 0) {
                        o = g;
                        break
                    }
                    q = K;
                    m = c[440] | 0;
                    if (q >>> 0 < m >>> 0) {
                        ap();
                        return 0;
                        return 0
                    }
                    p = q + g | 0;
                    k = p;
                    if (q >>> 0 >= p >>> 0) {
                        ap();
                        return 0;
                        return 0
                    }
                    e = c[K + 24 >> 2] | 0;
                    i = c[K + 12 >> 2] | 0;
                    do {
                        if ((i | 0) == (K | 0)) {
                            r = K + 20 | 0;
                            d = c[r >> 2] | 0;
                            if ((d | 0) == 0) {
                                j = K + 16 | 0;
                                B = c[j >> 2] | 0;
                                if ((B | 0) == 0) {
                                    L = 0;
                                    break
                                } else {
                                    M = B;
                                    N = j
                                }
                            } else {
                                M = d;
                                N = r
                            }
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                r = M + 20 | 0;
                                d = c[r >> 2] | 0;
                                if ((d | 0) != 0) {
                                    M = d;
                                    N = r;
                                    continue
                                }
                                r = M + 16 | 0;
                                d = c[r >> 2] | 0;
                                if ((d | 0) == 0) {
                                    break
                                } else {
                                    M = d;
                                    N = r
                                }
                            }
                            if (N >>> 0 < m >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[N >> 2] = 0;
                                L = M;
                                break
                            }
                        } else {
                            r = c[K + 8 >> 2] | 0;
                            if (r >>> 0 < m >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            d = r + 12 | 0;
                            if ((c[d >> 2] | 0) != (K | 0)) {
                                ap();
                                return 0;
                                return 0
                            }
                            j = i + 8 | 0;
                            if ((c[j >> 2] | 0) == (K | 0)) {
                                c[d >> 2] = i;
                                c[j >> 2] = r;
                                L = i;
                                break
                            } else {
                                ap();
                                return 0;
                                return 0
                            }
                        }
                    } while (0);
                    L2505: do {
                        if ((e | 0) != 0) {
                            i = K + 28 | 0;
                            m = 2048 + (c[i >> 2] << 2) | 0;
                            do {
                                if ((K | 0) == (c[m >> 2] | 0)) {
                                    c[m >> 2] = L;
                                    if ((L | 0) != 0) {
                                        break
                                    }
                                    c[437] = c[437] & (1 << c[i >> 2] ^ -1);
                                    break L2505
                                } else {
                                    if (e >>> 0 < (c[440] | 0) >>> 0) {
                                        ap();
                                        return 0;
                                        return 0
                                    }
                                    r = e + 16 | 0;
                                    if ((c[r >> 2] | 0) == (K | 0)) {
                                        c[r >> 2] = L
                                    } else {
                                        c[e + 20 >> 2] = L
                                    }
                                    if ((L | 0) == 0) {
                                        break L2505
                                    }
                                }
                            } while (0);
                            if (L >>> 0 < (c[440] | 0) >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            c[L + 24 >> 2] = e;
                            i = c[K + 16 >> 2] | 0;
                            do {
                                if ((i | 0) != 0) {
                                    if (i >>> 0 < (c[440] | 0) >>> 0) {
                                        ap();
                                        return 0;
                                        return 0
                                    } else {
                                        c[L + 16 >> 2] = i;
                                        c[i + 24 >> 2] = L;
                                        break
                                    }
                                }
                            } while (0);
                            i = c[K + 20 >> 2] | 0;
                            if ((i | 0) == 0) {
                                break
                            }
                            if (i >>> 0 < (c[440] | 0) >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[L + 20 >> 2] = i;
                                c[i + 24 >> 2] = L;
                                break
                            }
                        }
                    } while (0);
                    do {
                        if (J >>> 0 < 16) {
                            e = J + g | 0;
                            c[K + 4 >> 2] = e | 3;
                            i = q + (e + 4) | 0;
                            c[i >> 2] = c[i >> 2] | 1
                        } else {
                            c[K + 4 >> 2] = g | 3;
                            c[q + (g | 4) >> 2] = J | 1;
                            c[q + (J + g) >> 2] = J;
                            i = J >>> 3;
                            if (J >>> 0 < 256) {
                                e = i << 1;
                                m = 1784 + (e << 2) | 0;
                                r = c[436] | 0;
                                j = 1 << i;
                                do {
                                    if ((r & j | 0) == 0) {
                                        c[436] = r | j;
                                        O = m;
                                        P = 1784 + (e + 2 << 2) | 0
                                    } else {
                                        i = 1784 + (e + 2 << 2) | 0;
                                        d = c[i >> 2] | 0;
                                        if (d >>> 0 >= (c[440] | 0) >>> 0) {
                                            O = d;
                                            P = i;
                                            break
                                        }
                                        ap();
                                        return 0;
                                        return 0
                                    }
                                } while (0);
                                c[P >> 2] = k;
                                c[O + 12 >> 2] = k;
                                c[q + (g + 8) >> 2] = O;
                                c[q + (g + 12) >> 2] = m;
                                break
                            }
                            e = p;
                            j = J >>> 8;
                            do {
                                if ((j | 0) == 0) {
                                    Q = 0
                                } else {
                                    if (J >>> 0 > 16777215) {
                                        Q = 31;
                                        break
                                    }
                                    r = (j + 1048320 | 0) >>> 16 & 8;
                                    i = j << r;
                                    d = (i + 520192 | 0) >>> 16 & 4;
                                    B = i << d;
                                    i = (B + 245760 | 0) >>> 16 & 2;
                                    l = 14 - (d | r | i) + (B << i >>> 15) | 0;
                                    Q = J >>> ((l + 7 | 0) >>> 0) & 1 | l << 1
                                }
                            } while (0);
                            j = 2048 + (Q << 2) | 0;
                            c[q + (g + 28) >> 2] = Q;
                            c[q + (g + 20) >> 2] = 0;
                            c[q + (g + 16) >> 2] = 0;
                            m = c[437] | 0;
                            l = 1 << Q;
                            if ((m & l | 0) == 0) {
                                c[437] = m | l;
                                c[j >> 2] = e;
                                c[q + (g + 24) >> 2] = j;
                                c[q + (g + 12) >> 2] = e;
                                c[q + (g + 8) >> 2] = e;
                                break
                            }
                            if ((Q | 0) == 31) {
                                R = 0
                            } else {
                                R = 25 - (Q >>> 1) | 0
                            }
                            l = J << R;
                            m = c[j >> 2] | 0;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                if ((c[m + 4 >> 2] & -8 | 0) == (J | 0)) {
                                    break
                                }
                                S = m + 16 + (l >>> 31 << 2) | 0;
                                j = c[S >> 2] | 0;
                                if ((j | 0) == 0) {
                                    T = 1998;
                                    break
                                } else {
                                    l = l << 1;
                                    m = j
                                }
                            }
                            if ((T | 0) == 1998) {
                                if (S >>> 0 < (c[440] | 0) >>> 0) {
                                    ap();
                                    return 0;
                                    return 0
                                } else {
                                    c[S >> 2] = e;
                                    c[q + (g + 24) >> 2] = m;
                                    c[q + (g + 12) >> 2] = e;
                                    c[q + (g + 8) >> 2] = e;
                                    break
                                }
                            }
                            l = m + 8 | 0;
                            j = c[l >> 2] | 0;
                            i = c[440] | 0;
                            if (m >>> 0 < i >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            if (j >>> 0 < i >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[j + 12 >> 2] = e;
                                c[l >> 2] = e;
                                c[q + (g + 8) >> 2] = j;
                                c[q + (g + 12) >> 2] = m;
                                c[q + (g + 24) >> 2] = 0;
                                break
                            }
                        }
                    } while (0);
                    q = K + 8 | 0;
                    if ((q | 0) == 0) {
                        o = g;
                        break
                    } else {
                        n = q
                    }
                    return n | 0
                }
            } while (0);
            K = c[438] | 0;
            if (o >>> 0 <= K >>> 0) {
                S = K - o | 0;
                J = c[441] | 0;
                if (S >>> 0 > 15) {
                    R = J;
                    c[441] = R + o;
                    c[438] = S;
                    c[R + (o + 4) >> 2] = S | 1;
                    c[R + K >> 2] = S;
                    c[J + 4 >> 2] = o | 3
                } else {
                    c[438] = 0;
                    c[441] = 0;
                    c[J + 4 >> 2] = K | 3;
                    S = J + (K + 4) | 0;
                    c[S >> 2] = c[S >> 2] | 1
                }
                n = J + 8 | 0;
                return n | 0
            }
            J = c[439] | 0;
            if (o >>> 0 < J >>> 0) {
                S = J - o | 0;
                c[439] = S;
                J = c[442] | 0;
                K = J;
                c[442] = K + o;
                c[K + (o + 4) >> 2] = S | 1;
                c[J + 4 >> 2] = o | 3;
                n = J + 8 | 0;
                return n | 0
            }
            do {
                if ((c[2] | 0) == 0) {
                    J = ao(8) | 0;
                    if ((J - 1 & J | 0) == 0) {
                        c[4] = J;
                        c[3] = J;
                        c[5] = -1;
                        c[6] = 2097152;
                        c[7] = 0;
                        c[547] = 0;
                        c[2] = (aF(0) | 0) & -16 ^ 1431655768;
                        break
                    } else {
                        ap();
                        return 0;
                        return 0
                    }
                }
            } while (0);
            J = o + 48 | 0;
            S = c[4] | 0;
            K = o + 47 | 0;
            R = S + K | 0;
            Q = -S | 0;
            S = R & Q;
            if (S >>> 0 <= o >>> 0) {
                n = 0;
                return n | 0
            }
            O = c[546] | 0;
            do {
                if ((O | 0) != 0) {
                    P = c[544] | 0;
                    L = P + S | 0;
                    if (L >>> 0 <= P >>> 0 | L >>> 0 > O >>> 0) {
                        n = 0
                    } else {
                        break
                    }
                    return n | 0
                }
            } while (0);
            L2714: do {
                if ((c[547] & 4 | 0) == 0) {
                    O = c[442] | 0;
                    L2716: do {
                        if ((O | 0) == 0) {
                            T = 2028
                        } else {
                            L = O;
                            P = 2192;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                U = P | 0;
                                M = c[U >> 2] | 0;
                                if (M >>> 0 <= L >>> 0) {
                                    V = P + 4 | 0;
                                    if ((M + (c[V >> 2] | 0) | 0) >>> 0 > L >>> 0) {
                                        break
                                    }
                                }
                                M = c[P + 8 >> 2] | 0;
                                if ((M | 0) == 0) {
                                    T = 2028;
                                    break L2716
                                } else {
                                    P = M
                                }
                            }
                            if ((P | 0) == 0) {
                                T = 2028;
                                break
                            }
                            L = R - (c[439] | 0) & Q;
                            if (L >>> 0 >= 2147483647) {
                                W = 0;
                                break
                            }
                            m = aB(L | 0) | 0;
                            e = (m | 0) == ((c[U >> 2] | 0) + (c[V >> 2] | 0) | 0);
                            X = e ? m : -1;
                            Y = e ? L : 0;
                            Z = m;
                            _ = L;
                            T = 2037
                        }
                    } while (0);
                    do {
                        if ((T | 0) == 2028) {
                            O = aB(0) | 0;
                            if ((O | 0) == -1) {
                                W = 0;
                                break
                            }
                            g = O;
                            L = c[3] | 0;
                            m = L - 1 | 0;
                            if ((m & g | 0) == 0) {
                                $ = S
                            } else {
                                $ = S - g + (m + g & -L) | 0
                            }
                            L = c[544] | 0;
                            g = L + $ | 0;
                            if (!($ >>> 0 > o >>> 0 & $ >>> 0 < 2147483647)) {
                                W = 0;
                                break
                            }
                            m = c[546] | 0;
                            if ((m | 0) != 0) {
                                if (g >>> 0 <= L >>> 0 | g >>> 0 > m >>> 0) {
                                    W = 0;
                                    break
                                }
                            }
                            m = aB($ | 0) | 0;
                            g = (m | 0) == (O | 0);
                            X = g ? O : -1;
                            Y = g ? $ : 0;
                            Z = m;
                            _ = $;
                            T = 2037
                        }
                    } while (0);
                    L2736: do {
                        if ((T | 0) == 2037) {
                            m = -_ | 0;
                            if ((X | 0) != -1) {
                                aa = Y;
                                ab = X;
                                T = 2048;
                                break L2714
                            }
                            do {
                                if ((Z | 0) != -1 & _ >>> 0 < 2147483647 & _ >>> 0 < J >>> 0) {
                                    g = c[4] | 0;
                                    O = K - _ + g & -g;
                                    if (O >>> 0 >= 2147483647) {
                                        ac = _;
                                        break
                                    }
                                    if ((aB(O | 0) | 0) == -1) {
                                        aB(m | 0) | 0;
                                        W = Y;
                                        break L2736
                                    } else {
                                        ac = O + _ | 0;
                                        break
                                    }
                                } else {
                                    ac = _
                                }
                            } while (0);
                            if ((Z | 0) == -1) {
                                W = Y
                            } else {
                                aa = ac;
                                ab = Z;
                                T = 2048;
                                break L2714
                            }
                        }
                    } while (0);
                    c[547] = c[547] | 4;
                    ad = W;
                    T = 2045
                } else {
                    ad = 0;
                    T = 2045
                }
            } while (0);
            do {
                if ((T | 0) == 2045) {
                    if (S >>> 0 >= 2147483647) {
                        break
                    }
                    W = aB(S | 0) | 0;
                    Z = aB(0) | 0;
                    if (!((Z | 0) != -1 & (W | 0) != -1 & W >>> 0 < Z >>> 0)) {
                        break
                    }
                    ac = Z - W | 0;
                    Z = ac >>> 0 > (o + 40 | 0) >>> 0;
                    Y = Z ? W : -1;
                    if ((Y | 0) != -1) {
                        aa = Z ? ac : ad;
                        ab = Y;
                        T = 2048
                    }
                }
            } while (0);
            do {
                if ((T | 0) == 2048) {
                    ad = (c[544] | 0) + aa | 0;
                    c[544] = ad;
                    if (ad >>> 0 > (c[545] | 0) >>> 0) {
                        c[545] = ad
                    }
                    ad = c[442] | 0;
                    L2756: do {
                        if ((ad | 0) == 0) {
                            S = c[440] | 0;
                            if ((S | 0) == 0 | ab >>> 0 < S >>> 0) {
                                c[440] = ab
                            }
                            c[548] = ab;
                            c[549] = aa;
                            c[551] = 0;
                            c[445] = c[2];
                            c[444] = -1;
                            S = 0;
                            var tessellationLimit8 = 0;
                            do {
                                if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                Y = S << 1;
                                ac = 1784 + (Y << 2) | 0;
                                c[1784 + (Y + 3 << 2) >> 2] = ac;
                                c[1784 + (Y + 2 << 2) >> 2] = ac;
                                S = S + 1 | 0;
                            } while (S >>> 0 < 32);
                            S = ab + 8 | 0;
                            if ((S & 7 | 0) == 0) {
                                ae = 0
                            } else {
                                ae = -S & 7
                            }
                            S = aa - 40 - ae | 0;
                            c[442] = ab + ae;
                            c[439] = S;
                            c[ab + (ae + 4) >> 2] = S | 1;
                            c[ab + (aa - 36) >> 2] = 40;
                            c[443] = c[6]
                        } else {
                            S = 2192;
                            var tessellationLimit8 = 0;
                            while (1) {
                                if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                af = c[S >> 2] | 0;
                                ag = S + 4 | 0;
                                ah = c[ag >> 2] | 0;
                                if ((ab | 0) == (af + ah | 0)) {
                                    T = 2060;
                                    break
                                }
                                ac = c[S + 8 >> 2] | 0;
                                if ((ac | 0) == 0) {
                                    break
                                } else {
                                    S = ac
                                }
                            }
                            do {
                                if ((T | 0) == 2060) {
                                    if ((c[S + 12 >> 2] & 8 | 0) != 0) {
                                        break
                                    }
                                    ac = ad;
                                    if (!(ac >>> 0 >= af >>> 0 & ac >>> 0 < ab >>> 0)) {
                                        break
                                    }
                                    c[ag >> 2] = ah + aa;
                                    ac = c[442] | 0;
                                    Y = (c[439] | 0) + aa | 0;
                                    Z = ac;
                                    W = ac + 8 | 0;
                                    if ((W & 7 | 0) == 0) {
                                        ai = 0
                                    } else {
                                        ai = -W & 7
                                    }
                                    W = Y - ai | 0;
                                    c[442] = Z + ai;
                                    c[439] = W;
                                    c[Z + (ai + 4) >> 2] = W | 1;
                                    c[Z + (Y + 4) >> 2] = 40;
                                    c[443] = c[6];
                                    break L2756
                                }
                            } while (0);
                            if (ab >>> 0 < (c[440] | 0) >>> 0) {
                                c[440] = ab
                            }
                            S = ab + aa | 0;
                            Y = 2192;
                            var tessellationLimit8 = 0;
                            while (1) {
                                if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                aj = Y | 0;
                                if ((c[aj >> 2] | 0) == (S | 0)) {
                                    T = 2070;
                                    break
                                }
                                Z = c[Y + 8 >> 2] | 0;
                                if ((Z | 0) == 0) {
                                    break
                                } else {
                                    Y = Z
                                }
                            }
                            do {
                                if ((T | 0) == 2070) {
                                    if ((c[Y + 12 >> 2] & 8 | 0) != 0) {
                                        break
                                    }
                                    c[aj >> 2] = ab;
                                    S = Y + 4 | 0;
                                    c[S >> 2] = (c[S >> 2] | 0) + aa;
                                    S = ab + 8 | 0;
                                    if ((S & 7 | 0) == 0) {
                                        ak = 0
                                    } else {
                                        ak = -S & 7
                                    }
                                    S = ab + (aa + 8) | 0;
                                    if ((S & 7 | 0) == 0) {
                                        al = 0
                                    } else {
                                        al = -S & 7
                                    }
                                    S = ab + (al + aa) | 0;
                                    Z = S;
                                    W = ak + o | 0;
                                    ac = ab + W | 0;
                                    _ = ac;
                                    K = S - (ab + ak) - o | 0;
                                    c[ab + (ak + 4) >> 2] = o | 3;
                                    do {
                                        if ((Z | 0) == (c[442] | 0)) {
                                            J = (c[439] | 0) + K | 0;
                                            c[439] = J;
                                            c[442] = _;
                                            c[ab + (W + 4) >> 2] = J | 1
                                        } else {
                                            if ((Z | 0) == (c[441] | 0)) {
                                                J = (c[438] | 0) + K | 0;
                                                c[438] = J;
                                                c[441] = _;
                                                c[ab + (W + 4) >> 2] = J | 1;
                                                c[ab + (J + W) >> 2] = J;
                                                break
                                            }
                                            J = aa + 4 | 0;
                                            X = c[ab + (J + al) >> 2] | 0;
                                            if ((X & 3 | 0) == 1) {
                                                $ = X & -8;
                                                V = X >>> 3;
                                                L2791: do {
                                                    if (X >>> 0 < 256) {
                                                        U = c[ab + ((al | 8) + aa) >> 2] | 0;
                                                        Q = c[ab + (aa + 12 + al) >> 2] | 0;
                                                        R = 1784 + (V << 1 << 2) | 0;
                                                        do {
                                                            if ((U | 0) != (R | 0)) {
                                                                if (U >>> 0 < (c[440] | 0) >>> 0) {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                }
                                                                if ((c[U + 12 >> 2] | 0) == (Z | 0)) {
                                                                    break
                                                                }
                                                                ap();
                                                                return 0;
                                                                return 0
                                                            }
                                                        } while (0);
                                                        if ((Q | 0) == (U | 0)) {
                                                            c[436] = c[436] & (1 << V ^ -1);
                                                            break
                                                        }
                                                        do {
                                                            if ((Q | 0) == (R | 0)) {
                                                                am = Q + 8 | 0
                                                            } else {
                                                                if (Q >>> 0 < (c[440] | 0) >>> 0) {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                }
                                                                m = Q + 8 | 0;
                                                                if ((c[m >> 2] | 0) == (Z | 0)) {
                                                                    am = m;
                                                                    break
                                                                }
                                                                ap();
                                                                return 0;
                                                                return 0
                                                            }
                                                        } while (0);
                                                        c[U + 12 >> 2] = Q;
                                                        c[am >> 2] = U
                                                    } else {
                                                        R = S;
                                                        m = c[ab + ((al | 24) + aa) >> 2] | 0;
                                                        P = c[ab + (aa + 12 + al) >> 2] | 0;
                                                        do {
                                                            if ((P | 0) == (R | 0)) {
                                                                O = al | 16;
                                                                g = ab + (J + O) | 0;
                                                                L = c[g >> 2] | 0;
                                                                if ((L | 0) == 0) {
                                                                    e = ab + (O + aa) | 0;
                                                                    O = c[e >> 2] | 0;
                                                                    if ((O | 0) == 0) {
                                                                        an = 0;
                                                                        break
                                                                    } else {
                                                                        aq = O;
                                                                        ar = e
                                                                    }
                                                                } else {
                                                                    aq = L;
                                                                    ar = g
                                                                }
                                                                var tessellationLimit15 = 0;
                                                                while (1) {
                                                                    if (++tessellationLimit15 > TESSELLATION_LIMIT) {
                                                                        throw new Error("Tessellate limit exceeded");
                                                                    }
                                                                    g = aq + 20 | 0;
                                                                    L = c[g >> 2] | 0;
                                                                    if ((L | 0) != 0) {
                                                                        aq = L;
                                                                        ar = g;
                                                                        continue
                                                                    }
                                                                    g = aq + 16 | 0;
                                                                    L = c[g >> 2] | 0;
                                                                    if ((L | 0) == 0) {
                                                                        break
                                                                    } else {
                                                                        aq = L;
                                                                        ar = g
                                                                    }
                                                                }
                                                                if (ar >>> 0 < (c[440] | 0) >>> 0) {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                } else {
                                                                    c[ar >> 2] = 0;
                                                                    an = aq;
                                                                    break
                                                                }
                                                            } else {
                                                                g = c[ab + ((al | 8) + aa) >> 2] | 0;
                                                                if (g >>> 0 < (c[440] | 0) >>> 0) {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                }
                                                                L = g + 12 | 0;
                                                                if ((c[L >> 2] | 0) != (R | 0)) {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                }
                                                                e = P + 8 | 0;
                                                                if ((c[e >> 2] | 0) == (R | 0)) {
                                                                    c[L >> 2] = P;
                                                                    c[e >> 2] = g;
                                                                    an = P;
                                                                    break
                                                                } else {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                }
                                                            }
                                                        } while (0);
                                                        if ((m | 0) == 0) {
                                                            break
                                                        }
                                                        P = ab + (aa + 28 + al) | 0;
                                                        U = 2048 + (c[P >> 2] << 2) | 0;
                                                        do {
                                                            if ((R | 0) == (c[U >> 2] | 0)) {
                                                                c[U >> 2] = an;
                                                                if ((an | 0) != 0) {
                                                                    break
                                                                }
                                                                c[437] = c[437] & (1 << c[P >> 2] ^ -1);
                                                                break L2791
                                                            } else {
                                                                if (m >>> 0 < (c[440] | 0) >>> 0) {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                }
                                                                Q = m + 16 | 0;
                                                                if ((c[Q >> 2] | 0) == (R | 0)) {
                                                                    c[Q >> 2] = an
                                                                } else {
                                                                    c[m + 20 >> 2] = an
                                                                }
                                                                if ((an | 0) == 0) {
                                                                    break L2791
                                                                }
                                                            }
                                                        } while (0);
                                                        if (an >>> 0 < (c[440] | 0) >>> 0) {
                                                            ap();
                                                            return 0;
                                                            return 0
                                                        }
                                                        c[an + 24 >> 2] = m;
                                                        R = al | 16;
                                                        P = c[ab + (R + aa) >> 2] | 0;
                                                        do {
                                                            if ((P | 0) != 0) {
                                                                if (P >>> 0 < (c[440] | 0) >>> 0) {
                                                                    ap();
                                                                    return 0;
                                                                    return 0
                                                                } else {
                                                                    c[an + 16 >> 2] = P;
                                                                    c[P + 24 >> 2] = an;
                                                                    break
                                                                }
                                                            }
                                                        } while (0);
                                                        P = c[ab + (J + R) >> 2] | 0;
                                                        if ((P | 0) == 0) {
                                                            break
                                                        }
                                                        if (P >>> 0 < (c[440] | 0) >>> 0) {
                                                            ap();
                                                            return 0;
                                                            return 0
                                                        } else {
                                                            c[an + 20 >> 2] = P;
                                                            c[P + 24 >> 2] = an;
                                                            break
                                                        }
                                                    }
                                                } while (0);
                                                as = ab + (($ | al) + aa) | 0;
                                                at = $ + K | 0
                                            } else {
                                                as = Z;
                                                at = K
                                            }
                                            J = as + 4 | 0;
                                            c[J >> 2] = c[J >> 2] & -2;
                                            c[ab + (W + 4) >> 2] = at | 1;
                                            c[ab + (at + W) >> 2] = at;
                                            J = at >>> 3;
                                            if (at >>> 0 < 256) {
                                                V = J << 1;
                                                X = 1784 + (V << 2) | 0;
                                                P = c[436] | 0;
                                                m = 1 << J;
                                                do {
                                                    if ((P & m | 0) == 0) {
                                                        c[436] = P | m;
                                                        au = X;
                                                        av = 1784 + (V + 2 << 2) | 0
                                                    } else {
                                                        J = 1784 + (V + 2 << 2) | 0;
                                                        U = c[J >> 2] | 0;
                                                        if (U >>> 0 >= (c[440] | 0) >>> 0) {
                                                            au = U;
                                                            av = J;
                                                            break
                                                        }
                                                        ap();
                                                        return 0;
                                                        return 0
                                                    }
                                                } while (0);
                                                c[av >> 2] = _;
                                                c[au + 12 >> 2] = _;
                                                c[ab + (W + 8) >> 2] = au;
                                                c[ab + (W + 12) >> 2] = X;
                                                break
                                            }
                                            V = ac;
                                            m = at >>> 8;
                                            do {
                                                if ((m | 0) == 0) {
                                                    aw = 0
                                                } else {
                                                    if (at >>> 0 > 16777215) {
                                                        aw = 31;
                                                        break
                                                    }
                                                    P = (m + 1048320 | 0) >>> 16 & 8;
                                                    $ = m << P;
                                                    J = ($ + 520192 | 0) >>> 16 & 4;
                                                    U = $ << J;
                                                    $ = (U + 245760 | 0) >>> 16 & 2;
                                                    Q = 14 - (J | P | $) + (U << $ >>> 15) | 0;
                                                    aw = at >>> ((Q + 7 | 0) >>> 0) & 1 | Q << 1
                                                }
                                            } while (0);
                                            m = 2048 + (aw << 2) | 0;
                                            c[ab + (W + 28) >> 2] = aw;
                                            c[ab + (W + 20) >> 2] = 0;
                                            c[ab + (W + 16) >> 2] = 0;
                                            X = c[437] | 0;
                                            Q = 1 << aw;
                                            if ((X & Q | 0) == 0) {
                                                c[437] = X | Q;
                                                c[m >> 2] = V;
                                                c[ab + (W + 24) >> 2] = m;
                                                c[ab + (W + 12) >> 2] = V;
                                                c[ab + (W + 8) >> 2] = V;
                                                break
                                            }
                                            if ((aw | 0) == 31) {
                                                ax = 0
                                            } else {
                                                ax = 25 - (aw >>> 1) | 0
                                            }
                                            Q = at << ax;
                                            X = c[m >> 2] | 0;
                                            var tessellationLimit11 = 0;
                                            while (1) {
                                                if (++tessellationLimit11 > TESSELLATION_LIMIT) {
                                                    throw new Error("Tessellate limit exceeded");
                                                }
                                                if ((c[X + 4 >> 2] & -8 | 0) == (at | 0)) {
                                                    break
                                                }
                                                ay = X + 16 + (Q >>> 31 << 2) | 0;
                                                m = c[ay >> 2] | 0;
                                                if ((m | 0) == 0) {
                                                    T = 2143;
                                                    break
                                                } else {
                                                    Q = Q << 1;
                                                    X = m
                                                }
                                            }
                                            if ((T | 0) == 2143) {
                                                if (ay >>> 0 < (c[440] | 0) >>> 0) {
                                                    ap();
                                                    return 0;
                                                    return 0
                                                } else {
                                                    c[ay >> 2] = V;
                                                    c[ab + (W + 24) >> 2] = X;
                                                    c[ab + (W + 12) >> 2] = V;
                                                    c[ab + (W + 8) >> 2] = V;
                                                    break
                                                }
                                            }
                                            Q = X + 8 | 0;
                                            m = c[Q >> 2] | 0;
                                            $ = c[440] | 0;
                                            if (X >>> 0 < $ >>> 0) {
                                                ap();
                                                return 0;
                                                return 0
                                            }
                                            if (m >>> 0 < $ >>> 0) {
                                                ap();
                                                return 0;
                                                return 0
                                            } else {
                                                c[m + 12 >> 2] = V;
                                                c[Q >> 2] = V;
                                                c[ab + (W + 8) >> 2] = m;
                                                c[ab + (W + 12) >> 2] = X;
                                                c[ab + (W + 24) >> 2] = 0;
                                                break
                                            }
                                        }
                                    } while (0);
                                    n = ab + (ak | 8) | 0;
                                    return n | 0
                                }
                            } while (0);
                            Y = ad;
                            W = 2192;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                az = c[W >> 2] | 0;
                                if (az >>> 0 <= Y >>> 0) {
                                    aA = c[W + 4 >> 2] | 0;
                                    aD = az + aA | 0;
                                    if (aD >>> 0 > Y >>> 0) {
                                        break
                                    }
                                }
                                W = c[W + 8 >> 2] | 0
                            }
                            W = az + (aA - 39) | 0;
                            if ((W & 7 | 0) == 0) {
                                aE = 0
                            } else {
                                aE = -W & 7
                            }
                            W = az + (aA - 47 + aE) | 0;
                            ac = W >>> 0 < (ad + 16 | 0) >>> 0 ? Y : W;
                            W = ac + 8 | 0;
                            _ = ab + 8 | 0;
                            if ((_ & 7 | 0) == 0) {
                                aG = 0
                            } else {
                                aG = -_ & 7
                            }
                            _ = aa - 40 - aG | 0;
                            c[442] = ab + aG;
                            c[439] = _;
                            c[ab + (aG + 4) >> 2] = _ | 1;
                            c[ab + (aa - 36) >> 2] = 40;
                            c[443] = c[6];
                            c[ac + 4 >> 2] = 27;
                            c[W >> 2] = c[548];
                            c[W + 4 >> 2] = c[2196 >> 2];
                            c[W + 8 >> 2] = c[2200 >> 2];
                            c[W + 12 >> 2] = c[2204 >> 2];
                            c[548] = ab;
                            c[549] = aa;
                            c[551] = 0;
                            c[550] = W;
                            W = ac + 28 | 0;
                            c[W >> 2] = 7;
                            if ((ac + 32 | 0) >>> 0 < aD >>> 0) {
                                _ = W;
                                var tessellationLimit8 = 0;
                                while (1) {
                                    if (++tessellationLimit8 > TESSELLATION_LIMIT) {
                                        throw new Error("Tessellate limit exceeded");
                                    }
                                    W = _ + 4 | 0;
                                    c[W >> 2] = 7;
                                    if ((_ + 8 | 0) >>> 0 < aD >>> 0) {
                                        _ = W
                                    } else {
                                        break
                                    }
                                }
                            }
                            if ((ac | 0) == (Y | 0)) {
                                break
                            }
                            _ = ac - ad | 0;
                            W = Y + (_ + 4) | 0;
                            c[W >> 2] = c[W >> 2] & -2;
                            c[ad + 4 >> 2] = _ | 1;
                            c[Y + _ >> 2] = _;
                            W = _ >>> 3;
                            if (_ >>> 0 < 256) {
                                K = W << 1;
                                Z = 1784 + (K << 2) | 0;
                                S = c[436] | 0;
                                m = 1 << W;
                                do {
                                    if ((S & m | 0) == 0) {
                                        c[436] = S | m;
                                        aH = Z;
                                        aI = 1784 + (K + 2 << 2) | 0
                                    } else {
                                        W = 1784 + (K + 2 << 2) | 0;
                                        Q = c[W >> 2] | 0;
                                        if (Q >>> 0 >= (c[440] | 0) >>> 0) {
                                            aH = Q;
                                            aI = W;
                                            break
                                        }
                                        ap();
                                        return 0;
                                        return 0
                                    }
                                } while (0);
                                c[aI >> 2] = ad;
                                c[aH + 12 >> 2] = ad;
                                c[ad + 8 >> 2] = aH;
                                c[ad + 12 >> 2] = Z;
                                break
                            }
                            K = ad;
                            m = _ >>> 8;
                            do {
                                if ((m | 0) == 0) {
                                    aJ = 0
                                } else {
                                    if (_ >>> 0 > 16777215) {
                                        aJ = 31;
                                        break
                                    }
                                    S = (m + 1048320 | 0) >>> 16 & 8;
                                    Y = m << S;
                                    ac = (Y + 520192 | 0) >>> 16 & 4;
                                    W = Y << ac;
                                    Y = (W + 245760 | 0) >>> 16 & 2;
                                    Q = 14 - (ac | S | Y) + (W << Y >>> 15) | 0;
                                    aJ = _ >>> ((Q + 7 | 0) >>> 0) & 1 | Q << 1
                                }
                            } while (0);
                            m = 2048 + (aJ << 2) | 0;
                            c[ad + 28 >> 2] = aJ;
                            c[ad + 20 >> 2] = 0;
                            c[ad + 16 >> 2] = 0;
                            Z = c[437] | 0;
                            Q = 1 << aJ;
                            if ((Z & Q | 0) == 0) {
                                c[437] = Z | Q;
                                c[m >> 2] = K;
                                c[ad + 24 >> 2] = m;
                                c[ad + 12 >> 2] = ad;
                                c[ad + 8 >> 2] = ad;
                                break
                            }
                            if ((aJ | 0) == 31) {
                                aK = 0
                            } else {
                                aK = 25 - (aJ >>> 1) | 0
                            }
                            Q = _ << aK;
                            Z = c[m >> 2] | 0;
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                if ((c[Z + 4 >> 2] & -8 | 0) == (_ | 0)) {
                                    break
                                }
                                aL = Z + 16 + (Q >>> 31 << 2) | 0;
                                m = c[aL >> 2] | 0;
                                if ((m | 0) == 0) {
                                    T = 2178;
                                    break
                                } else {
                                    Q = Q << 1;
                                    Z = m
                                }
                            }
                            if ((T | 0) == 2178) {
                                if (aL >>> 0 < (c[440] | 0) >>> 0) {
                                    ap();
                                    return 0;
                                    return 0
                                } else {
                                    c[aL >> 2] = K;
                                    c[ad + 24 >> 2] = Z;
                                    c[ad + 12 >> 2] = ad;
                                    c[ad + 8 >> 2] = ad;
                                    break
                                }
                            }
                            Q = Z + 8 | 0;
                            _ = c[Q >> 2] | 0;
                            m = c[440] | 0;
                            if (Z >>> 0 < m >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            if (_ >>> 0 < m >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[_ + 12 >> 2] = K;
                                c[Q >> 2] = K;
                                c[ad + 8 >> 2] = _;
                                c[ad + 12 >> 2] = Z;
                                c[ad + 24 >> 2] = 0;
                                break
                            }
                        }
                    } while (0);
                    ad = c[439] | 0;
                    if (ad >>> 0 <= o >>> 0) {
                        break
                    }
                    _ = ad - o | 0;
                    c[439] = _;
                    ad = c[442] | 0;
                    Q = ad;
                    c[442] = Q + o;
                    c[Q + (o + 4) >> 2] = _ | 1;
                    c[ad + 4 >> 2] = o | 3;
                    n = ad + 8 | 0;
                    return n | 0
                }
            } while (0);
            c[(aC() | 0) >> 2] = 12;
            n = 0;
            return n | 0
        }

        function b1(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0,
                M = 0,
                N = 0,
                O = 0;
            if ((a | 0) == 0) {
                return
            }
            b = a - 8 | 0;
            d = b;
            e = c[440] | 0;
            if (b >>> 0 < e >>> 0) {
                ap()
            }
            f = c[a - 4 >> 2] | 0;
            g = f & 3;
            if ((g | 0) == 1) {
                ap()
            }
            h = f & -8;
            i = a + (h - 8) | 0;
            j = i;
            L2973: do {
                if ((f & 1 | 0) == 0) {
                    k = c[b >> 2] | 0;
                    if ((g | 0) == 0) {
                        return
                    }
                    l = -8 - k | 0;
                    m = a + l | 0;
                    n = m;
                    o = k + h | 0;
                    if (m >>> 0 < e >>> 0) {
                        ap()
                    }
                    if ((n | 0) == (c[441] | 0)) {
                        p = a + (h - 4) | 0;
                        if ((c[p >> 2] & 3 | 0) != 3) {
                            q = n;
                            r = o;
                            break
                        }
                        c[438] = o;
                        c[p >> 2] = c[p >> 2] & -2;
                        c[a + (l + 4) >> 2] = o | 1;
                        c[i >> 2] = o;
                        return
                    }
                    p = k >>> 3;
                    if (k >>> 0 < 256) {
                        k = c[a + (l + 8) >> 2] | 0;
                        s = c[a + (l + 12) >> 2] | 0;
                        t = 1784 + (p << 1 << 2) | 0;
                        do {
                            if ((k | 0) != (t | 0)) {
                                if (k >>> 0 < e >>> 0) {
                                    ap()
                                }
                                if ((c[k + 12 >> 2] | 0) == (n | 0)) {
                                    break
                                }
                                ap()
                            }
                        } while (0);
                        if ((s | 0) == (k | 0)) {
                            c[436] = c[436] & (1 << p ^ -1);
                            q = n;
                            r = o;
                            break
                        }
                        do {
                            if ((s | 0) == (t | 0)) {
                                u = s + 8 | 0
                            } else {
                                if (s >>> 0 < e >>> 0) {
                                    ap()
                                }
                                v = s + 8 | 0;
                                if ((c[v >> 2] | 0) == (n | 0)) {
                                    u = v;
                                    break
                                }
                                ap()
                            }
                        } while (0);
                        c[k + 12 >> 2] = s;
                        c[u >> 2] = k;
                        q = n;
                        r = o;
                        break
                    }
                    t = m;
                    p = c[a + (l + 24) >> 2] | 0;
                    v = c[a + (l + 12) >> 2] | 0;
                    do {
                        if ((v | 0) == (t | 0)) {
                            w = a + (l + 20) | 0;
                            x = c[w >> 2] | 0;
                            if ((x | 0) == 0) {
                                y = a + (l + 16) | 0;
                                z = c[y >> 2] | 0;
                                if ((z | 0) == 0) {
                                    A = 0;
                                    break
                                } else {
                                    B = z;
                                    C = y
                                }
                            } else {
                                B = x;
                                C = w
                            }
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                w = B + 20 | 0;
                                x = c[w >> 2] | 0;
                                if ((x | 0) != 0) {
                                    B = x;
                                    C = w;
                                    continue
                                }
                                w = B + 16 | 0;
                                x = c[w >> 2] | 0;
                                if ((x | 0) == 0) {
                                    break
                                } else {
                                    B = x;
                                    C = w
                                }
                            }
                            if (C >>> 0 < e >>> 0) {
                                ap()
                            } else {
                                c[C >> 2] = 0;
                                A = B;
                                break
                            }
                        } else {
                            w = c[a + (l + 8) >> 2] | 0;
                            if (w >>> 0 < e >>> 0) {
                                ap()
                            }
                            x = w + 12 | 0;
                            if ((c[x >> 2] | 0) != (t | 0)) {
                                ap()
                            }
                            y = v + 8 | 0;
                            if ((c[y >> 2] | 0) == (t | 0)) {
                                c[x >> 2] = v;
                                c[y >> 2] = w;
                                A = v;
                                break
                            } else {
                                ap()
                            }
                        }
                    } while (0);
                    if ((p | 0) == 0) {
                        q = n;
                        r = o;
                        break
                    }
                    v = a + (l + 28) | 0;
                    m = 2048 + (c[v >> 2] << 2) | 0;
                    do {
                        if ((t | 0) == (c[m >> 2] | 0)) {
                            c[m >> 2] = A;
                            if ((A | 0) != 0) {
                                break
                            }
                            c[437] = c[437] & (1 << c[v >> 2] ^ -1);
                            q = n;
                            r = o;
                            break L2973
                        } else {
                            if (p >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            }
                            k = p + 16 | 0;
                            if ((c[k >> 2] | 0) == (t | 0)) {
                                c[k >> 2] = A
                            } else {
                                c[p + 20 >> 2] = A
                            }
                            if ((A | 0) == 0) {
                                q = n;
                                r = o;
                                break L2973
                            }
                        }
                    } while (0);
                    if (A >>> 0 < (c[440] | 0) >>> 0) {
                        ap()
                    }
                    c[A + 24 >> 2] = p;
                    t = c[a + (l + 16) >> 2] | 0;
                    do {
                        if ((t | 0) != 0) {
                            if (t >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            } else {
                                c[A + 16 >> 2] = t;
                                c[t + 24 >> 2] = A;
                                break
                            }
                        }
                    } while (0);
                    t = c[a + (l + 20) >> 2] | 0;
                    if ((t | 0) == 0) {
                        q = n;
                        r = o;
                        break
                    }
                    if (t >>> 0 < (c[440] | 0) >>> 0) {
                        ap()
                    } else {
                        c[A + 20 >> 2] = t;
                        c[t + 24 >> 2] = A;
                        q = n;
                        r = o;
                        break
                    }
                } else {
                    q = d;
                    r = h
                }
            } while (0);
            d = q;
            if (d >>> 0 >= i >>> 0) {
                ap()
            }
            A = a + (h - 4) | 0;
            e = c[A >> 2] | 0;
            if ((e & 1 | 0) == 0) {
                ap()
            }
            do {
                if ((e & 2 | 0) == 0) {
                    if ((j | 0) == (c[442] | 0)) {
                        B = (c[439] | 0) + r | 0;
                        c[439] = B;
                        c[442] = q;
                        c[q + 4 >> 2] = B | 1;
                        if ((q | 0) == (c[441] | 0)) {
                            c[441] = 0;
                            c[438] = 0
                        }
                        if (B >>> 0 <= (c[443] | 0) >>> 0) {
                            return
                        }
                        b3(0) | 0;
                        return
                    }
                    if ((j | 0) == (c[441] | 0)) {
                        B = (c[438] | 0) + r | 0;
                        c[438] = B;
                        c[441] = q;
                        c[q + 4 >> 2] = B | 1;
                        c[d + B >> 2] = B;
                        return
                    }
                    B = (e & -8) + r | 0;
                    C = e >>> 3;
                    L3079: do {
                        if (e >>> 0 < 256) {
                            u = c[a + h >> 2] | 0;
                            g = c[a + (h | 4) >> 2] | 0;
                            b = 1784 + (C << 1 << 2) | 0;
                            do {
                                if ((u | 0) != (b | 0)) {
                                    if (u >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    }
                                    if ((c[u + 12 >> 2] | 0) == (j | 0)) {
                                        break
                                    }
                                    ap()
                                }
                            } while (0);
                            if ((g | 0) == (u | 0)) {
                                c[436] = c[436] & (1 << C ^ -1);
                                break
                            }
                            do {
                                if ((g | 0) == (b | 0)) {
                                    D = g + 8 | 0
                                } else {
                                    if (g >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    }
                                    f = g + 8 | 0;
                                    if ((c[f >> 2] | 0) == (j | 0)) {
                                        D = f;
                                        break
                                    }
                                    ap()
                                }
                            } while (0);
                            c[u + 12 >> 2] = g;
                            c[D >> 2] = u
                        } else {
                            b = i;
                            f = c[a + (h + 16) >> 2] | 0;
                            t = c[a + (h | 4) >> 2] | 0;
                            do {
                                if ((t | 0) == (b | 0)) {
                                    p = a + (h + 12) | 0;
                                    v = c[p >> 2] | 0;
                                    if ((v | 0) == 0) {
                                        m = a + (h + 8) | 0;
                                        k = c[m >> 2] | 0;
                                        if ((k | 0) == 0) {
                                            E = 0;
                                            break
                                        } else {
                                            F = k;
                                            G = m
                                        }
                                    } else {
                                        F = v;
                                        G = p
                                    }
                                    var tessellationLimit9 = 0;
                                    while (1) {
                                        if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                            throw new Error("Tessellate limit exceeded");
                                        }
                                        p = F + 20 | 0;
                                        v = c[p >> 2] | 0;
                                        if ((v | 0) != 0) {
                                            F = v;
                                            G = p;
                                            continue
                                        }
                                        p = F + 16 | 0;
                                        v = c[p >> 2] | 0;
                                        if ((v | 0) == 0) {
                                            break
                                        } else {
                                            F = v;
                                            G = p
                                        }
                                    }
                                    if (G >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    } else {
                                        c[G >> 2] = 0;
                                        E = F;
                                        break
                                    }
                                } else {
                                    p = c[a + h >> 2] | 0;
                                    if (p >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    }
                                    v = p + 12 | 0;
                                    if ((c[v >> 2] | 0) != (b | 0)) {
                                        ap()
                                    }
                                    m = t + 8 | 0;
                                    if ((c[m >> 2] | 0) == (b | 0)) {
                                        c[v >> 2] = t;
                                        c[m >> 2] = p;
                                        E = t;
                                        break
                                    } else {
                                        ap()
                                    }
                                }
                            } while (0);
                            if ((f | 0) == 0) {
                                break
                            }
                            t = a + (h + 20) | 0;
                            u = 2048 + (c[t >> 2] << 2) | 0;
                            do {
                                if ((b | 0) == (c[u >> 2] | 0)) {
                                    c[u >> 2] = E;
                                    if ((E | 0) != 0) {
                                        break
                                    }
                                    c[437] = c[437] & (1 << c[t >> 2] ^ -1);
                                    break L3079
                                } else {
                                    if (f >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    }
                                    g = f + 16 | 0;
                                    if ((c[g >> 2] | 0) == (b | 0)) {
                                        c[g >> 2] = E
                                    } else {
                                        c[f + 20 >> 2] = E
                                    }
                                    if ((E | 0) == 0) {
                                        break L3079
                                    }
                                }
                            } while (0);
                            if (E >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            }
                            c[E + 24 >> 2] = f;
                            b = c[a + (h + 8) >> 2] | 0;
                            do {
                                if ((b | 0) != 0) {
                                    if (b >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    } else {
                                        c[E + 16 >> 2] = b;
                                        c[b + 24 >> 2] = E;
                                        break
                                    }
                                }
                            } while (0);
                            b = c[a + (h + 12) >> 2] | 0;
                            if ((b | 0) == 0) {
                                break
                            }
                            if (b >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            } else {
                                c[E + 20 >> 2] = b;
                                c[b + 24 >> 2] = E;
                                break
                            }
                        }
                    } while (0);
                    c[q + 4 >> 2] = B | 1;
                    c[d + B >> 2] = B;
                    if ((q | 0) != (c[441] | 0)) {
                        H = B;
                        break
                    }
                    c[438] = B;
                    return
                } else {
                    c[A >> 2] = e & -2;
                    c[q + 4 >> 2] = r | 1;
                    c[d + r >> 2] = r;
                    H = r
                }
            } while (0);
            r = H >>> 3;
            if (H >>> 0 < 256) {
                d = r << 1;
                e = 1784 + (d << 2) | 0;
                A = c[436] | 0;
                E = 1 << r;
                do {
                    if ((A & E | 0) == 0) {
                        c[436] = A | E;
                        I = e;
                        J = 1784 + (d + 2 << 2) | 0
                    } else {
                        r = 1784 + (d + 2 << 2) | 0;
                        h = c[r >> 2] | 0;
                        if (h >>> 0 >= (c[440] | 0) >>> 0) {
                            I = h;
                            J = r;
                            break
                        }
                        ap()
                    }
                } while (0);
                c[J >> 2] = q;
                c[I + 12 >> 2] = q;
                c[q + 8 >> 2] = I;
                c[q + 12 >> 2] = e;
                return
            }
            e = q;
            I = H >>> 8;
            do {
                if ((I | 0) == 0) {
                    K = 0
                } else {
                    if (H >>> 0 > 16777215) {
                        K = 31;
                        break
                    }
                    J = (I + 1048320 | 0) >>> 16 & 8;
                    d = I << J;
                    E = (d + 520192 | 0) >>> 16 & 4;
                    A = d << E;
                    d = (A + 245760 | 0) >>> 16 & 2;
                    r = 14 - (E | J | d) + (A << d >>> 15) | 0;
                    K = H >>> ((r + 7 | 0) >>> 0) & 1 | r << 1
                }
            } while (0);
            I = 2048 + (K << 2) | 0;
            c[q + 28 >> 2] = K;
            c[q + 20 >> 2] = 0;
            c[q + 16 >> 2] = 0;
            r = c[437] | 0;
            d = 1 << K;
            do {
                if ((r & d | 0) == 0) {
                    c[437] = r | d;
                    c[I >> 2] = e;
                    c[q + 24 >> 2] = I;
                    c[q + 12 >> 2] = q;
                    c[q + 8 >> 2] = q
                } else {
                    if ((K | 0) == 31) {
                        L = 0
                    } else {
                        L = 25 - (K >>> 1) | 0
                    }
                    A = H << L;
                    J = c[I >> 2] | 0;
                    var tessellationLimit5 = 0;
                    while (1) {
                        if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        if ((c[J + 4 >> 2] & -8 | 0) == (H | 0)) {
                            break
                        }
                        M = J + 16 + (A >>> 31 << 2) | 0;
                        E = c[M >> 2] | 0;
                        if ((E | 0) == 0) {
                            N = 2357;
                            break
                        } else {
                            A = A << 1;
                            J = E
                        }
                    }
                    if ((N | 0) == 2357) {
                        if (M >>> 0 < (c[440] | 0) >>> 0) {
                            ap()
                        } else {
                            c[M >> 2] = e;
                            c[q + 24 >> 2] = J;
                            c[q + 12 >> 2] = q;
                            c[q + 8 >> 2] = q;
                            break
                        }
                    }
                    A = J + 8 | 0;
                    B = c[A >> 2] | 0;
                    E = c[440] | 0;
                    if (J >>> 0 < E >>> 0) {
                        ap()
                    }
                    if (B >>> 0 < E >>> 0) {
                        ap()
                    } else {
                        c[B + 12 >> 2] = e;
                        c[A >> 2] = e;
                        c[q + 8 >> 2] = B;
                        c[q + 12 >> 2] = J;
                        c[q + 24 >> 2] = 0;
                        break
                    }
                }
            } while (0);
            q = (c[444] | 0) - 1 | 0;
            c[444] = q;
            if ((q | 0) == 0) {
                O = 2200
            } else {
                return
            }
            var tessellationLimit4 = 0;
            while (1) {
                if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                q = c[O >> 2] | 0;
                if ((q | 0) == 0) {
                    break
                } else {
                    O = q + 8 | 0
                }
            }
            c[444] = -1;
            return
        }

        function b2(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0;
            if ((a | 0) == 0) {
                d = b0(b) | 0;
                return d | 0
            }
            if (b >>> 0 > 4294967231) {
                c[(aC() | 0) >> 2] = 12;
                d = 0;
                return d | 0
            }
            if (b >>> 0 < 11) {
                e = 16
            } else {
                e = b + 11 & -8
            }
            f = b4(a - 8 | 0, e) | 0;
            if ((f | 0) != 0) {
                d = f + 8 | 0;
                return d | 0
            }
            f = b0(b) | 0;
            if ((f | 0) == 0) {
                d = 0;
                return d | 0
            }
            e = c[a - 4 >> 2] | 0;
            g = (e & -8) - ((e & 3 | 0) == 0 ? 8 : 4) | 0;
            e = g >>> 0 < b >>> 0 ? g : b;
            b6(f | 0, a | 0, e) | 0;
            b1(a);
            d = f;
            return d | 0
        }

        function b3(a) {
            a = a | 0;
            var b = 0,
                d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0;
            do {
                if ((c[2] | 0) == 0) {
                    b = ao(8) | 0;
                    if ((b - 1 & b | 0) == 0) {
                        c[4] = b;
                        c[3] = b;
                        c[5] = -1;
                        c[6] = 2097152;
                        c[7] = 0;
                        c[547] = 0;
                        c[2] = (aF(0) | 0) & -16 ^ 1431655768;
                        break
                    } else {
                        ap();
                        return 0;
                        return 0
                    }
                }
            } while (0);
            if (a >>> 0 >= 4294967232) {
                d = 0;
                return d | 0
            }
            b = c[442] | 0;
            if ((b | 0) == 0) {
                d = 0;
                return d | 0
            }
            e = c[439] | 0;
            do {
                if (e >>> 0 > (a + 40 | 0) >>> 0) {
                    f = c[4] | 0;
                    g = _((((-40 - a - 1 + e + f | 0) >>> 0) / (f >>> 0) >>> 0) - 1 | 0, f) | 0;
                    h = b;
                    i = 2192;
                    var tessellationLimit5 = 0;
                    while (1) {
                        if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        j = c[i >> 2] | 0;
                        if (j >>> 0 <= h >>> 0) {
                            if ((j + (c[i + 4 >> 2] | 0) | 0) >>> 0 > h >>> 0) {
                                k = i;
                                break
                            }
                        }
                        j = c[i + 8 >> 2] | 0;
                        if ((j | 0) == 0) {
                            k = 0;
                            break
                        } else {
                            i = j
                        }
                    }
                    if ((c[k + 12 >> 2] & 8 | 0) != 0) {
                        break
                    }
                    i = aB(0) | 0;
                    h = k + 4 | 0;
                    if ((i | 0) != ((c[k >> 2] | 0) + (c[h >> 2] | 0) | 0)) {
                        break
                    }
                    j = aB(-(g >>> 0 > 2147483646 ? -2147483648 - f | 0 : g) | 0) | 0;
                    l = aB(0) | 0;
                    if (!((j | 0) != -1 & l >>> 0 < i >>> 0)) {
                        break
                    }
                    j = i - l | 0;
                    if ((i | 0) == (l | 0)) {
                        break
                    }
                    c[h >> 2] = (c[h >> 2] | 0) - j;
                    c[544] = (c[544] | 0) - j;
                    h = c[442] | 0;
                    m = (c[439] | 0) - j | 0;
                    j = h;
                    n = h + 8 | 0;
                    if ((n & 7 | 0) == 0) {
                        o = 0
                    } else {
                        o = -n & 7
                    }
                    n = m - o | 0;
                    c[442] = j + o;
                    c[439] = n;
                    c[j + (o + 4) >> 2] = n | 1;
                    c[j + (m + 4) >> 2] = 40;
                    c[443] = c[6];
                    d = (i | 0) != (l | 0) & 1;
                    return d | 0
                }
            } while (0);
            if ((c[439] | 0) >>> 0 <= (c[443] | 0) >>> 0) {
                d = 0;
                return d | 0
            }
            c[443] = -1;
            d = 0;
            return d | 0
        }

        function b4(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0;
            d = a + 4 | 0;
            e = c[d >> 2] | 0;
            f = e & -8;
            g = a;
            h = g + f | 0;
            i = h;
            j = c[440] | 0;
            if (g >>> 0 < j >>> 0) {
                ap();
                return 0;
                return 0
            }
            k = e & 3;
            if (!((k | 0) != 1 & g >>> 0 < h >>> 0)) {
                ap();
                return 0;
                return 0
            }
            l = g + (f | 4) | 0;
            m = c[l >> 2] | 0;
            if ((m & 1 | 0) == 0) {
                ap();
                return 0;
                return 0
            }
            if ((k | 0) == 0) {
                if (b >>> 0 < 256) {
                    n = 0;
                    return n | 0
                }
                do {
                    if (f >>> 0 >= (b + 4 | 0) >>> 0) {
                        if ((f - b | 0) >>> 0 > c[4] << 1 >>> 0) {
                            break
                        } else {
                            n = a
                        }
                        return n | 0
                    }
                } while (0);
                n = 0;
                return n | 0
            }
            if (f >>> 0 >= b >>> 0) {
                k = f - b | 0;
                if (k >>> 0 <= 15) {
                    n = a;
                    return n | 0
                }
                c[d >> 2] = e & 1 | b | 2;
                c[g + (b + 4) >> 2] = k | 3;
                c[l >> 2] = c[l >> 2] | 1;
                b5(g + b | 0, k);
                n = a;
                return n | 0
            }
            if ((i | 0) == (c[442] | 0)) {
                k = (c[439] | 0) + f | 0;
                if (k >>> 0 <= b >>> 0) {
                    n = 0;
                    return n | 0
                }
                l = k - b | 0;
                c[d >> 2] = e & 1 | b | 2;
                c[g + (b + 4) >> 2] = l | 1;
                c[442] = g + b;
                c[439] = l;
                n = a;
                return n | 0
            }
            if ((i | 0) == (c[441] | 0)) {
                l = (c[438] | 0) + f | 0;
                if (l >>> 0 < b >>> 0) {
                    n = 0;
                    return n | 0
                }
                k = l - b | 0;
                if (k >>> 0 > 15) {
                    c[d >> 2] = e & 1 | b | 2;
                    c[g + (b + 4) >> 2] = k | 1;
                    c[g + l >> 2] = k;
                    o = g + (l + 4) | 0;
                    c[o >> 2] = c[o >> 2] & -2;
                    p = g + b | 0;
                    q = k
                } else {
                    c[d >> 2] = e & 1 | l | 2;
                    e = g + (l + 4) | 0;
                    c[e >> 2] = c[e >> 2] | 1;
                    p = 0;
                    q = 0
                }
                c[438] = q;
                c[441] = p;
                n = a;
                return n | 0
            }
            if ((m & 2 | 0) != 0) {
                n = 0;
                return n | 0
            }
            p = (m & -8) + f | 0;
            if (p >>> 0 < b >>> 0) {
                n = 0;
                return n | 0
            }
            q = p - b | 0;
            e = m >>> 3;
            L3299: do {
                if (m >>> 0 < 256) {
                    l = c[g + (f + 8) >> 2] | 0;
                    k = c[g + (f + 12) >> 2] | 0;
                    o = 1784 + (e << 1 << 2) | 0;
                    do {
                        if ((l | 0) != (o | 0)) {
                            if (l >>> 0 < j >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            if ((c[l + 12 >> 2] | 0) == (i | 0)) {
                                break
                            }
                            ap();
                            return 0;
                            return 0
                        }
                    } while (0);
                    if ((k | 0) == (l | 0)) {
                        c[436] = c[436] & (1 << e ^ -1);
                        break
                    }
                    do {
                        if ((k | 0) == (o | 0)) {
                            r = k + 8 | 0
                        } else {
                            if (k >>> 0 < j >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            s = k + 8 | 0;
                            if ((c[s >> 2] | 0) == (i | 0)) {
                                r = s;
                                break
                            }
                            ap();
                            return 0;
                            return 0
                        }
                    } while (0);
                    c[l + 12 >> 2] = k;
                    c[r >> 2] = l
                } else {
                    o = h;
                    s = c[g + (f + 24) >> 2] | 0;
                    t = c[g + (f + 12) >> 2] | 0;
                    do {
                        if ((t | 0) == (o | 0)) {
                            u = g + (f + 20) | 0;
                            v = c[u >> 2] | 0;
                            if ((v | 0) == 0) {
                                w = g + (f + 16) | 0;
                                x = c[w >> 2] | 0;
                                if ((x | 0) == 0) {
                                    y = 0;
                                    break
                                } else {
                                    z = x;
                                    A = w
                                }
                            } else {
                                z = v;
                                A = u
                            }
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                u = z + 20 | 0;
                                v = c[u >> 2] | 0;
                                if ((v | 0) != 0) {
                                    z = v;
                                    A = u;
                                    continue
                                }
                                u = z + 16 | 0;
                                v = c[u >> 2] | 0;
                                if ((v | 0) == 0) {
                                    break
                                } else {
                                    z = v;
                                    A = u
                                }
                            }
                            if (A >>> 0 < j >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[A >> 2] = 0;
                                y = z;
                                break
                            }
                        } else {
                            u = c[g + (f + 8) >> 2] | 0;
                            if (u >>> 0 < j >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            v = u + 12 | 0;
                            if ((c[v >> 2] | 0) != (o | 0)) {
                                ap();
                                return 0;
                                return 0
                            }
                            w = t + 8 | 0;
                            if ((c[w >> 2] | 0) == (o | 0)) {
                                c[v >> 2] = t;
                                c[w >> 2] = u;
                                y = t;
                                break
                            } else {
                                ap();
                                return 0;
                                return 0
                            }
                        }
                    } while (0);
                    if ((s | 0) == 0) {
                        break
                    }
                    t = g + (f + 28) | 0;
                    l = 2048 + (c[t >> 2] << 2) | 0;
                    do {
                        if ((o | 0) == (c[l >> 2] | 0)) {
                            c[l >> 2] = y;
                            if ((y | 0) != 0) {
                                break
                            }
                            c[437] = c[437] & (1 << c[t >> 2] ^ -1);
                            break L3299
                        } else {
                            if (s >>> 0 < (c[440] | 0) >>> 0) {
                                ap();
                                return 0;
                                return 0
                            }
                            k = s + 16 | 0;
                            if ((c[k >> 2] | 0) == (o | 0)) {
                                c[k >> 2] = y
                            } else {
                                c[s + 20 >> 2] = y
                            }
                            if ((y | 0) == 0) {
                                break L3299
                            }
                        }
                    } while (0);
                    if (y >>> 0 < (c[440] | 0) >>> 0) {
                        ap();
                        return 0;
                        return 0
                    }
                    c[y + 24 >> 2] = s;
                    o = c[g + (f + 16) >> 2] | 0;
                    do {
                        if ((o | 0) != 0) {
                            if (o >>> 0 < (c[440] | 0) >>> 0) {
                                ap();
                                return 0;
                                return 0
                            } else {
                                c[y + 16 >> 2] = o;
                                c[o + 24 >> 2] = y;
                                break
                            }
                        }
                    } while (0);
                    o = c[g + (f + 20) >> 2] | 0;
                    if ((o | 0) == 0) {
                        break
                    }
                    if (o >>> 0 < (c[440] | 0) >>> 0) {
                        ap();
                        return 0;
                        return 0
                    } else {
                        c[y + 20 >> 2] = o;
                        c[o + 24 >> 2] = y;
                        break
                    }
                }
            } while (0);
            if (q >>> 0 < 16) {
                c[d >> 2] = p | c[d >> 2] & 1 | 2;
                y = g + (p | 4) | 0;
                c[y >> 2] = c[y >> 2] | 1;
                n = a;
                return n | 0
            } else {
                c[d >> 2] = c[d >> 2] & 1 | b | 2;
                c[g + (b + 4) >> 2] = q | 3;
                d = g + (p | 4) | 0;
                c[d >> 2] = c[d >> 2] | 1;
                b5(g + b | 0, q);
                n = a;
                return n | 0
            }
            return 0
        }

        function b5(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0,
                f = 0,
                g = 0,
                h = 0,
                i = 0,
                j = 0,
                k = 0,
                l = 0,
                m = 0,
                n = 0,
                o = 0,
                p = 0,
                q = 0,
                r = 0,
                s = 0,
                t = 0,
                u = 0,
                v = 0,
                w = 0,
                x = 0,
                y = 0,
                z = 0,
                A = 0,
                B = 0,
                C = 0,
                D = 0,
                E = 0,
                F = 0,
                G = 0,
                H = 0,
                I = 0,
                J = 0,
                K = 0,
                L = 0;
            d = a;
            e = d + b | 0;
            f = e;
            g = c[a + 4 >> 2] | 0;
            L3375: do {
                if ((g & 1 | 0) == 0) {
                    h = c[a >> 2] | 0;
                    if ((g & 3 | 0) == 0) {
                        return
                    }
                    i = d + (-h | 0) | 0;
                    j = i;
                    k = h + b | 0;
                    l = c[440] | 0;
                    if (i >>> 0 < l >>> 0) {
                        ap()
                    }
                    if ((j | 0) == (c[441] | 0)) {
                        m = d + (b + 4) | 0;
                        if ((c[m >> 2] & 3 | 0) != 3) {
                            n = j;
                            o = k;
                            break
                        }
                        c[438] = k;
                        c[m >> 2] = c[m >> 2] & -2;
                        c[d + (4 - h) >> 2] = k | 1;
                        c[e >> 2] = k;
                        return
                    }
                    m = h >>> 3;
                    if (h >>> 0 < 256) {
                        p = c[d + (8 - h) >> 2] | 0;
                        q = c[d + (12 - h) >> 2] | 0;
                        r = 1784 + (m << 1 << 2) | 0;
                        do {
                            if ((p | 0) != (r | 0)) {
                                if (p >>> 0 < l >>> 0) {
                                    ap()
                                }
                                if ((c[p + 12 >> 2] | 0) == (j | 0)) {
                                    break
                                }
                                ap()
                            }
                        } while (0);
                        if ((q | 0) == (p | 0)) {
                            c[436] = c[436] & (1 << m ^ -1);
                            n = j;
                            o = k;
                            break
                        }
                        do {
                            if ((q | 0) == (r | 0)) {
                                s = q + 8 | 0
                            } else {
                                if (q >>> 0 < l >>> 0) {
                                    ap()
                                }
                                t = q + 8 | 0;
                                if ((c[t >> 2] | 0) == (j | 0)) {
                                    s = t;
                                    break
                                }
                                ap()
                            }
                        } while (0);
                        c[p + 12 >> 2] = q;
                        c[s >> 2] = p;
                        n = j;
                        o = k;
                        break
                    }
                    r = i;
                    m = c[d + (24 - h) >> 2] | 0;
                    t = c[d + (12 - h) >> 2] | 0;
                    do {
                        if ((t | 0) == (r | 0)) {
                            u = 16 - h | 0;
                            v = d + (u + 4) | 0;
                            w = c[v >> 2] | 0;
                            if ((w | 0) == 0) {
                                x = d + u | 0;
                                u = c[x >> 2] | 0;
                                if ((u | 0) == 0) {
                                    y = 0;
                                    break
                                } else {
                                    z = u;
                                    A = x
                                }
                            } else {
                                z = w;
                                A = v
                            }
                            var tessellationLimit7 = 0;
                            while (1) {
                                if (++tessellationLimit7 > TESSELLATION_LIMIT) {
                                    throw new Error("Tessellate limit exceeded");
                                }
                                v = z + 20 | 0;
                                w = c[v >> 2] | 0;
                                if ((w | 0) != 0) {
                                    z = w;
                                    A = v;
                                    continue
                                }
                                v = z + 16 | 0;
                                w = c[v >> 2] | 0;
                                if ((w | 0) == 0) {
                                    break
                                } else {
                                    z = w;
                                    A = v
                                }
                            }
                            if (A >>> 0 < l >>> 0) {
                                ap()
                            } else {
                                c[A >> 2] = 0;
                                y = z;
                                break
                            }
                        } else {
                            v = c[d + (8 - h) >> 2] | 0;
                            if (v >>> 0 < l >>> 0) {
                                ap()
                            }
                            w = v + 12 | 0;
                            if ((c[w >> 2] | 0) != (r | 0)) {
                                ap()
                            }
                            x = t + 8 | 0;
                            if ((c[x >> 2] | 0) == (r | 0)) {
                                c[w >> 2] = t;
                                c[x >> 2] = v;
                                y = t;
                                break
                            } else {
                                ap()
                            }
                        }
                    } while (0);
                    if ((m | 0) == 0) {
                        n = j;
                        o = k;
                        break
                    }
                    t = d + (28 - h) | 0;
                    l = 2048 + (c[t >> 2] << 2) | 0;
                    do {
                        if ((r | 0) == (c[l >> 2] | 0)) {
                            c[l >> 2] = y;
                            if ((y | 0) != 0) {
                                break
                            }
                            c[437] = c[437] & (1 << c[t >> 2] ^ -1);
                            n = j;
                            o = k;
                            break L3375
                        } else {
                            if (m >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            }
                            i = m + 16 | 0;
                            if ((c[i >> 2] | 0) == (r | 0)) {
                                c[i >> 2] = y
                            } else {
                                c[m + 20 >> 2] = y
                            }
                            if ((y | 0) == 0) {
                                n = j;
                                o = k;
                                break L3375
                            }
                        }
                    } while (0);
                    if (y >>> 0 < (c[440] | 0) >>> 0) {
                        ap()
                    }
                    c[y + 24 >> 2] = m;
                    r = 16 - h | 0;
                    t = c[d + r >> 2] | 0;
                    do {
                        if ((t | 0) != 0) {
                            if (t >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            } else {
                                c[y + 16 >> 2] = t;
                                c[t + 24 >> 2] = y;
                                break
                            }
                        }
                    } while (0);
                    t = c[d + (r + 4) >> 2] | 0;
                    if ((t | 0) == 0) {
                        n = j;
                        o = k;
                        break
                    }
                    if (t >>> 0 < (c[440] | 0) >>> 0) {
                        ap()
                    } else {
                        c[y + 20 >> 2] = t;
                        c[t + 24 >> 2] = y;
                        n = j;
                        o = k;
                        break
                    }
                } else {
                    n = a;
                    o = b
                }
            } while (0);
            a = c[440] | 0;
            if (e >>> 0 < a >>> 0) {
                ap()
            }
            y = d + (b + 4) | 0;
            z = c[y >> 2] | 0;
            do {
                if ((z & 2 | 0) == 0) {
                    if ((f | 0) == (c[442] | 0)) {
                        A = (c[439] | 0) + o | 0;
                        c[439] = A;
                        c[442] = n;
                        c[n + 4 >> 2] = A | 1;
                        if ((n | 0) != (c[441] | 0)) {
                            return
                        }
                        c[441] = 0;
                        c[438] = 0;
                        return
                    }
                    if ((f | 0) == (c[441] | 0)) {
                        A = (c[438] | 0) + o | 0;
                        c[438] = A;
                        c[441] = n;
                        c[n + 4 >> 2] = A | 1;
                        c[n + A >> 2] = A;
                        return
                    }
                    A = (z & -8) + o | 0;
                    s = z >>> 3;
                    L3474: do {
                        if (z >>> 0 < 256) {
                            g = c[d + (b + 8) >> 2] | 0;
                            t = c[d + (b + 12) >> 2] | 0;
                            h = 1784 + (s << 1 << 2) | 0;
                            do {
                                if ((g | 0) != (h | 0)) {
                                    if (g >>> 0 < a >>> 0) {
                                        ap()
                                    }
                                    if ((c[g + 12 >> 2] | 0) == (f | 0)) {
                                        break
                                    }
                                    ap()
                                }
                            } while (0);
                            if ((t | 0) == (g | 0)) {
                                c[436] = c[436] & (1 << s ^ -1);
                                break
                            }
                            do {
                                if ((t | 0) == (h | 0)) {
                                    B = t + 8 | 0
                                } else {
                                    if (t >>> 0 < a >>> 0) {
                                        ap()
                                    }
                                    m = t + 8 | 0;
                                    if ((c[m >> 2] | 0) == (f | 0)) {
                                        B = m;
                                        break
                                    }
                                    ap()
                                }
                            } while (0);
                            c[g + 12 >> 2] = t;
                            c[B >> 2] = g
                        } else {
                            h = e;
                            m = c[d + (b + 24) >> 2] | 0;
                            l = c[d + (b + 12) >> 2] | 0;
                            do {
                                if ((l | 0) == (h | 0)) {
                                    i = d + (b + 20) | 0;
                                    p = c[i >> 2] | 0;
                                    if ((p | 0) == 0) {
                                        q = d + (b + 16) | 0;
                                        v = c[q >> 2] | 0;
                                        if ((v | 0) == 0) {
                                            C = 0;
                                            break
                                        } else {
                                            D = v;
                                            E = q
                                        }
                                    } else {
                                        D = p;
                                        E = i
                                    }
                                    var tessellationLimit9 = 0;
                                    while (1) {
                                        if (++tessellationLimit9 > TESSELLATION_LIMIT) {
                                            throw new Error("Tessellate limit exceeded");
                                        }
                                        i = D + 20 | 0;
                                        p = c[i >> 2] | 0;
                                        if ((p | 0) != 0) {
                                            D = p;
                                            E = i;
                                            continue
                                        }
                                        i = D + 16 | 0;
                                        p = c[i >> 2] | 0;
                                        if ((p | 0) == 0) {
                                            break
                                        } else {
                                            D = p;
                                            E = i
                                        }
                                    }
                                    if (E >>> 0 < a >>> 0) {
                                        ap()
                                    } else {
                                        c[E >> 2] = 0;
                                        C = D;
                                        break
                                    }
                                } else {
                                    i = c[d + (b + 8) >> 2] | 0;
                                    if (i >>> 0 < a >>> 0) {
                                        ap()
                                    }
                                    p = i + 12 | 0;
                                    if ((c[p >> 2] | 0) != (h | 0)) {
                                        ap()
                                    }
                                    q = l + 8 | 0;
                                    if ((c[q >> 2] | 0) == (h | 0)) {
                                        c[p >> 2] = l;
                                        c[q >> 2] = i;
                                        C = l;
                                        break
                                    } else {
                                        ap()
                                    }
                                }
                            } while (0);
                            if ((m | 0) == 0) {
                                break
                            }
                            l = d + (b + 28) | 0;
                            g = 2048 + (c[l >> 2] << 2) | 0;
                            do {
                                if ((h | 0) == (c[g >> 2] | 0)) {
                                    c[g >> 2] = C;
                                    if ((C | 0) != 0) {
                                        break
                                    }
                                    c[437] = c[437] & (1 << c[l >> 2] ^ -1);
                                    break L3474
                                } else {
                                    if (m >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    }
                                    t = m + 16 | 0;
                                    if ((c[t >> 2] | 0) == (h | 0)) {
                                        c[t >> 2] = C
                                    } else {
                                        c[m + 20 >> 2] = C
                                    }
                                    if ((C | 0) == 0) {
                                        break L3474
                                    }
                                }
                            } while (0);
                            if (C >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            }
                            c[C + 24 >> 2] = m;
                            h = c[d + (b + 16) >> 2] | 0;
                            do {
                                if ((h | 0) != 0) {
                                    if (h >>> 0 < (c[440] | 0) >>> 0) {
                                        ap()
                                    } else {
                                        c[C + 16 >> 2] = h;
                                        c[h + 24 >> 2] = C;
                                        break
                                    }
                                }
                            } while (0);
                            h = c[d + (b + 20) >> 2] | 0;
                            if ((h | 0) == 0) {
                                break
                            }
                            if (h >>> 0 < (c[440] | 0) >>> 0) {
                                ap()
                            } else {
                                c[C + 20 >> 2] = h;
                                c[h + 24 >> 2] = C;
                                break
                            }
                        }
                    } while (0);
                    c[n + 4 >> 2] = A | 1;
                    c[n + A >> 2] = A;
                    if ((n | 0) != (c[441] | 0)) {
                        F = A;
                        break
                    }
                    c[438] = A;
                    return
                } else {
                    c[y >> 2] = z & -2;
                    c[n + 4 >> 2] = o | 1;
                    c[n + o >> 2] = o;
                    F = o
                }
            } while (0);
            o = F >>> 3;
            if (F >>> 0 < 256) {
                z = o << 1;
                y = 1784 + (z << 2) | 0;
                C = c[436] | 0;
                b = 1 << o;
                do {
                    if ((C & b | 0) == 0) {
                        c[436] = C | b;
                        G = y;
                        H = 1784 + (z + 2 << 2) | 0
                    } else {
                        o = 1784 + (z + 2 << 2) | 0;
                        d = c[o >> 2] | 0;
                        if (d >>> 0 >= (c[440] | 0) >>> 0) {
                            G = d;
                            H = o;
                            break
                        }
                        ap()
                    }
                } while (0);
                c[H >> 2] = n;
                c[G + 12 >> 2] = n;
                c[n + 8 >> 2] = G;
                c[n + 12 >> 2] = y;
                return
            }
            y = n;
            G = F >>> 8;
            do {
                if ((G | 0) == 0) {
                    I = 0
                } else {
                    if (F >>> 0 > 16777215) {
                        I = 31;
                        break
                    }
                    H = (G + 1048320 | 0) >>> 16 & 8;
                    z = G << H;
                    b = (z + 520192 | 0) >>> 16 & 4;
                    C = z << b;
                    z = (C + 245760 | 0) >>> 16 & 2;
                    o = 14 - (b | H | z) + (C << z >>> 15) | 0;
                    I = F >>> ((o + 7 | 0) >>> 0) & 1 | o << 1
                }
            } while (0);
            G = 2048 + (I << 2) | 0;
            c[n + 28 >> 2] = I;
            c[n + 20 >> 2] = 0;
            c[n + 16 >> 2] = 0;
            o = c[437] | 0;
            z = 1 << I;
            if ((o & z | 0) == 0) {
                c[437] = o | z;
                c[G >> 2] = y;
                c[n + 24 >> 2] = G;
                c[n + 12 >> 2] = n;
                c[n + 8 >> 2] = n;
                return
            }
            if ((I | 0) == 31) {
                J = 0
            } else {
                J = 25 - (I >>> 1) | 0
            }
            I = F << J;
            J = c[G >> 2] | 0;
            var tessellationLimit3 = 0;
            while (1) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                if ((c[J + 4 >> 2] & -8 | 0) == (F | 0)) {
                    break
                }
                K = J + 16 + (I >>> 31 << 2) | 0;
                G = c[K >> 2] | 0;
                if ((G | 0) == 0) {
                    L = 2663;
                    break
                } else {
                    I = I << 1;
                    J = G
                }
            }
            if ((L | 0) == 2663) {
                if (K >>> 0 < (c[440] | 0) >>> 0) {
                    ap()
                }
                c[K >> 2] = y;
                c[n + 24 >> 2] = J;
                c[n + 12 >> 2] = n;
                c[n + 8 >> 2] = n;
                return
            }
            K = J + 8 | 0;
            L = c[K >> 2] | 0;
            I = c[440] | 0;
            if (J >>> 0 < I >>> 0) {
                ap()
            }
            if (L >>> 0 < I >>> 0) {
                ap()
            }
            c[L + 12 >> 2] = y;
            c[K >> 2] = y;
            c[n + 8 >> 2] = L;
            c[n + 12 >> 2] = J;
            c[n + 24 >> 2] = 0;
            return
        }

        function b6(b, d, e) {
            b = b | 0;
            d = d | 0;
            e = e | 0;
            var f = 0;
            f = b | 0;
            if ((b & 3) == (d & 3)) {
                var tessellationLimit4 = 0;
                while (b & 3) {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    if ((e | 0) == 0) return f | 0;
                    a[b] = a[d] | 0;
                    b = b + 1 | 0;
                    d = d + 1 | 0;
                    e = e - 1 | 0
                }
                var tessellationLimit4 = 0;
                while ((e | 0) >= 4) {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    c[b >> 2] = c[d >> 2];
                    b = b + 4 | 0;
                    d = d + 4 | 0;
                    e = e - 4 | 0
                }
            }
            var tessellationLimit3 = 0;
            while ((e | 0) > 0) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                a[b] = a[d] | 0;
                b = b + 1 | 0;
                d = d + 1 | 0;
                e = e - 1 | 0
            }
            return f | 0
        }

        function b7(a, b, d) {
            a = a | 0;
            b = b | 0;
            d = d | 0;
            var e = 0;
            r = r + 1 | 0;
            c[a >> 2] = r;
            var tessellationLimit3 = 0;
            while ((e | 0) < 40) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                if ((c[d + (e << 2) >> 2] | 0) == 0) {
                    c[d + (e << 2) >> 2] = r;
                    c[d + ((e << 2) + 4) >> 2] = b;
                    c[d + ((e << 2) + 8) >> 2] = 0;
                    return 0
                }
                e = e + 2 | 0
            }
            aA(116);
            aA(111);
            aA(111);
            aA(32);
            aA(109);
            aA(97);
            aA(110);
            aA(121);
            aA(32);
            aA(115);
            aA(101);
            aA(116);
            aA(106);
            aA(109);
            aA(112);
            aA(115);
            aA(32);
            aA(105);
            aA(110);
            aA(32);
            aA(97);
            aA(32);
            aA(102);
            aA(117);
            aA(110);
            aA(99);
            aA(116);
            aA(105);
            aA(111);
            aA(110);
            aA(32);
            aA(99);
            aA(97);
            aA(108);
            aA(108);
            aA(44);
            aA(32);
            aA(98);
            aA(117);
            aA(105);
            aA(108);
            aA(100);
            aA(32);
            aA(119);
            aA(105);
            aA(116);
            aA(104);
            aA(32);
            aA(97);
            aA(32);
            aA(104);
            aA(105);
            aA(103);
            aA(104);
            aA(101);
            aA(114);
            aA(32);
            aA(118);
            aA(97);
            aA(108);
            aA(117);
            aA(101);
            aA(32);
            aA(102);
            aA(111);
            aA(114);
            aA(32);
            aA(77);
            aA(65);
            aA(88);
            aA(95);
            aA(83);
            aA(69);
            aA(84);
            aA(74);
            aA(77);
            aA(80);
            aA(83);
            aA(10);
            $(0);
            return 0
        }

        function b8(a, b) {
            a = a | 0;
            b = b | 0;
            var d = 0,
                e = 0;
            var tessellationLimit3 = 0;
            while ((d | 0) < 20) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                e = c[b + (d << 2) >> 2] | 0;
                if ((e | 0) == 0) break;
                if ((e | 0) == (a | 0)) {
                    return c[b + ((d << 2) + 4) >> 2] | 0
                }
                d = d + 2 | 0
            }
            return 0
        }

        function b9(b, d, e) {
            b = b | 0;
            d = d | 0;
            e = e | 0;
            var f = 0,
                g = 0,
                h = 0;
            f = b + e | 0;
            if ((e | 0) >= 20) {
                d = d & 255;
                e = b & 3;
                g = d | d << 8 | d << 16 | d << 24;
                h = f & ~3;
                if (e) {
                    e = b + 4 - e | 0;
                    var tessellationLimit5 = 0;
                    while ((b | 0) < (e | 0)) {
                        if (++tessellationLimit5 > TESSELLATION_LIMIT) {
                            throw new Error("Tessellate limit exceeded");
                        }
                        a[b] = d;
                        b = b + 1 | 0
                    }
                }
                var tessellationLimit4 = 0;
                while ((b | 0) < (h | 0)) {
                    if (++tessellationLimit4 > TESSELLATION_LIMIT) {
                        throw new Error("Tessellate limit exceeded");
                    }
                    c[b >> 2] = g;
                    b = b + 4 | 0
                }
            }
            var tessellationLimit3 = 0;
            while ((b | 0) < (f | 0)) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                a[b] = d;
                b = b + 1 | 0
            }
        }

        function ca(b) {
            b = b | 0;
            var c = 0;
            c = b;
            var tessellationLimit3 = 0;
            while (a[c] | 0) {
                if (++tessellationLimit3 > TESSELLATION_LIMIT) {
                    throw new Error("Tessellate limit exceeded");
                }
                c = c + 1 | 0
            }
            return c - b | 0
        }

        function cb(a, b) {
            a = a | 0;
            b = b | 0;
            aw(a | 0, b | 0)
        }

        function cc(a, b, c, d, e, f) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            e = e | 0;
            f = f | 0;
            aG[a & 7](b | 0, c | 0, d | 0, e | 0, f | 0)
        }

        function cd(a, b) {
            a = a | 0;
            b = b | 0;
            aH[a & 31](b | 0)
        }

        function ce(a, b, c) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            aI[a & 31](b | 0, c | 0)
        }

        function cf(a, b) {
            a = a | 0;
            b = b | 0;
            return aJ[a & 15](b | 0) | 0
        }

        function cg(a, b, c, d) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            return aK[a & 3](b | 0, c | 0, d | 0) | 0
        }

        function ch(a, b, c, d) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            aL[a & 7](b | 0, c | 0, d | 0)
        }

        function ci(a) {
            a = a | 0;
            aM[a & 3]()
        }

        function cj(a, b, c) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            return aN[a & 7](b | 0, c | 0) | 0
        }

        function ck(a, b, c, d, e) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            e = e | 0;
            aO[a & 3](b | 0, c | 0, d | 0, e | 0)
        }

        function cl(a, b, c, d, e) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            e = e | 0;
            $(0)
        }

        function cm(a) {
            a = a | 0;
            $(1)
        }

        function cn(a, b) {
            a = a | 0;
            b = b | 0;
            $(2)
        }

        function co(a) {
            a = a | 0;
            $(3);
            return 0
        }

        function cp(a, b, c) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            $(4);
            return 0
        }

        function cq(a, b, c) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            $(5)
        }

        function cr() {
            $(6)
        }

        function cs(a, b) {
            a = a | 0;
            b = b | 0;
            $(7);
            return 0
        }

        function ct(a, b, c, d) {
            a = a | 0;
            b = b | 0;
            c = c | 0;
            d = d | 0;
            $(8)
        }
        // EMSCRIPTEN_END_FUNCS
        var aG = [cl, cl, bY, cl, bi, cl, cl, cl];
        var aH = [cm, cm, a9, cm, bg, cm, by, cm, bp, cm, bj, cm, a8, cm, b1, cm, bk, cm, bn, cm, bl, cm, cm, cm, cm, cm, cm, cm, cm, cm, cm, cm];
        var aI = [cn, cn, bX, cn, bS, cn, bf, cn, bV, cn, cb, cn, bT, cn, bb, cn, bh, cn, bU, cn, be, cn, bW, cn, br, cn, bd, cn, cn, cn, cn, cn];
        var aJ = [co, co, ba, co, a6, co, b_, co, a5, co, bH, co, a3, co, b0, co];
        var aK = [cp, cp, bQ, cp];
        var aL = [cq, cq, bc, cq, bt, cq, bs, cq];
        var aM = [cr, cr, bm, cr];
        var aN = [cs, cs, a4, cs, bv, cs, cs, cs];
        var aO = [ct, ct, bo, ct];
        return {
            _testSetjmp: b8,
            _saveSetjmp: b7,
            _free: b1,
            _realloc: b2,
            _tessellate: b$,
            _strlen: ca,
            _memset: b9,
            _malloc: b0,
            _memcpy: b6,
            stackAlloc: aP,
            stackSave: aQ,
            stackRestore: aR,
            setThrew: aS,
            setTempRet0: aV,
            setTempRet1: aW,
            setTempRet2: aX,
            setTempRet3: aY,
            setTempRet4: aZ,
            setTempRet5: a_,
            setTempRet6: a$,
            setTempRet7: a0,
            setTempRet8: a1,
            setTempRet9: a2,
            dynCall_viiiii: cc,
            dynCall_vi: cd,
            dynCall_vii: ce,
            dynCall_ii: cf,
            dynCall_iiii: cg,
            dynCall_viii: ch,
            dynCall_v: ci,
            dynCall_iii: cj,
            dynCall_viiii: ck
        }
        // EMSCRIPTEN_END_ASM
    })({
        Math: Math,
        Int8Array: Int8Array,
        Int16Array: Int16Array,
        Int32Array: Int32Array,
        Uint8Array: Uint8Array,
        Uint16Array: Uint16Array,
        Uint32Array: Uint32Array,
        Float32Array: Float32Array,
        Float64Array: Float64Array
    }, {
        abort: H,
        assert: A,
        asmPrintInt: function(a, b) {
            Module.print("int " + a + "," + b)
        },
        asmPrintFloat: function(a, b) {
            Module.print("float " + a + "," + b)
        },
        min: Vb,
        invoke_viiiii: function(a, b, c, d, e, f) {
            try {
                Module.dynCall_viiiii(a, b, c, d, e, f)
            } catch (g) {
                "number" !== typeof g && "longjmp" !== g && j(g),
                    $.setThrew(1, 0)
            }
        },
        invoke_vi: function(a, b) {
            try {
                Module.dynCall_vi(a, b)
            } catch (c) {
                "number" !== typeof c && "longjmp" !== c && j(c), $.setThrew(1, 0)
            }
        },
        invoke_vii: function(a, b, c) {
            try {
                Module.dynCall_vii(a, b, c)
            } catch (d) {
                "number" !== typeof d && "longjmp" !== d && j(d), $.setThrew(1, 0)
            }
        },
        invoke_ii: function(a, b) {
            try {
                return Module.dynCall_ii(a, b)
            } catch (c) {
                "number" !== typeof c && "longjmp" !== c && j(c), $.setThrew(1, 0)
            }
        },
        invoke_iiii: function(a, b, c, d) {
            try {
                return Module.dynCall_iiii(a, b, c, d)
            } catch (e) {
                "number" !== typeof e && "longjmp" !== e &&
                    j(e), $.setThrew(1, 0)
            }
        },
        invoke_viii: function(a, b, c, d) {
            try {
                Module.dynCall_viii(a, b, c, d)
            } catch (e) {
                "number" !== typeof e && "longjmp" !== e && j(e), $.setThrew(1, 0)
            }
        },
        invoke_v: function(a) {
            try {
                Module.dynCall_v(a)
            } catch (b) {
                "number" !== typeof b && "longjmp" !== b && j(b), $.setThrew(1, 0)
            }
        },
        invoke_iii: function(a, b, c) {
            try {
                return Module.dynCall_iii(a, b, c)
            } catch (d) {
                "number" !== typeof d && "longjmp" !== d && j(d), $.setThrew(1, 0)
            }
        },
        invoke_viiii: function(a, b, c, d, e) {
            try {
                Module.dynCall_viiii(a, b, c, d, e)
            } catch (f) {
                "number" !== typeof f && "longjmp" !==
                    f && j(f), $.setThrew(1, 0)
            }
        },
        _llvm_lifetime_end: u(),
        _sysconf: function(a) {
            switch (a) {
                case 8:
                    return 4096;
                case 54:
                case 56:
                case 21:
                case 61:
                case 63:
                case 22:
                case 67:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 69:
                case 28:
                case 101:
                case 70:
                case 71:
                case 29:
                case 30:
                case 199:
                case 75:
                case 76:
                case 32:
                case 43:
                case 44:
                case 80:
                case 46:
                case 47:
                case 45:
                case 48:
                case 49:
                case 42:
                case 82:
                case 33:
                case 7:
                case 108:
                case 109:
                case 107:
                case 112:
                case 119:
                case 121:
                    return 200809;
                case 13:
                case 104:
                case 94:
                case 95:
                case 34:
                case 35:
                case 77:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                case 94:
                case 95:
                case 110:
                case 111:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 120:
                case 40:
                case 16:
                case 79:
                case 19:
                    return -1;
                case 92:
                case 93:
                case 5:
                case 72:
                case 6:
                case 74:
                case 92:
                case 93:
                case 96:
                case 97:
                case 98:
                case 99:
                case 102:
                case 103:
                case 105:
                    return 1;
                case 38:
                case 66:
                case 50:
                case 51:
                case 4:
                    return 1024;
                case 15:
                case 64:
                case 41:
                    return 32;
                case 55:
                case 37:
                case 17:
                    return 2147483647;
                case 18:
                case 1:
                    return 47839;
                case 59:
                case 57:
                    return 99;
                case 68:
                case 58:
                    return 2048;
                case 0:
                    return 2097152;
                case 3:
                    return 65536;
                case 14:
                    return 32768;
                case 73:
                    return 32767;
                case 39:
                    return 16384;
                case 60:
                    return 1E3;
                case 106:
                    return 700;
                case 52:
                    return 256;
                case 62:
                    return 255;
                case 2:
                    return 100;
                case 65:
                    return 64;
                case 36:
                    return 20;
                case 100:
                    return 16;
                case 20:
                    return 6;
                case 53:
                    return 4;
                case 10:
                    return 1
            }
            W(db);
            return -1
        },
        _abort: function() {
            I = n;
            j("abort() at " + Error().stack)
        },
        _fprintf: function(a, b, c) {
            c = Eb(b, c);
            b = ha();
            a = Bb(P(c, "i8", 1), 1, c.length, a);
            ia(b);
            return a
        },
        __reallyNegative: Db,
        _fputc: xb,
        ___setErrNo: W,
        _fwrite: Bb,
        _send: ub,
        _longjmp: function(a, b) {
            $.setThrew(a, b || 1);
            j("longjmp")
        },
        __formatString: Eb,
        ___assert_func: function(a, b, c, d) {
            j("Assertion failed: " + (d ? J(d) :
                "unknown condition") + ", at: " + [a ? J(a) : "unknown filename", b, c ? J(c) : "unknown function"] + " at " + Error().stack)
        },
        _pwrite: vb,
        _putchar: function(a) {
            return xb(a, M[X >> 2])
        },
        _sbrk: Gb,
        ___errno_location: function() {
            return eb
        },
        _llvm_lifetime_start: u(),
        _write: wb,
        _time: function(a) {
            var b = Math.floor(Date.now() / 1E3);
            a && (M[a >> 2] = b);
            return b
        },
        STACKTOP: w,
        STACK_MAX: Ia,
        tempDoublePtr: Ya,
        ABORT: I,
        NaN: NaN,
        Infinity: Infinity,
        _stderr: U
    }, R),
    zb = Module._testSetjmp = $._testSetjmp,
    yb = Module._saveSetjmp = $._saveSetjmp;
Module._free = $._free;
Module._realloc = $._realloc;
Module._tessellate = $._tessellate;
var Cb = Module._strlen = $._strlen,
    Ab = Module._memset = $._memset,
    Da = Module._malloc = $._malloc,
    Za = Module._memcpy = $._memcpy;
Module.dynCall_viiiii = $.dynCall_viiiii;
Module.dynCall_vi = $.dynCall_vi;
Module.dynCall_vii = $.dynCall_vii;
Module.dynCall_ii = $.dynCall_ii;
Module.dynCall_iiii = $.dynCall_iiii;
Module.dynCall_viii = $.dynCall_viii;
Module.dynCall_v = $.dynCall_v;
Module.dynCall_iii = $.dynCall_iii;
Module.dynCall_viiii = $.dynCall_viiii;
var na = function(a) {
        return $.stackAlloc(a)
    },
    ha = function() {
        return $.stackSave()
    },
    ia = function(a) {
        $.stackRestore(a)
    },
    Fb = q;
Module.callMain = function(a) {
    function b() {
        for (var a = 0; 3 > a; a++) d.push(0)
    }
    A(0 == T, "cannot call main when async dependencies remain! (listen on __ATMAIN__)");
    A(!Module.preRun || 0 == Module.preRun.length, "cannot call main when preRun functions remain to be called");
    a = a || [];
    Oa || (Oa = n, Ka(La));
    var c = a.length + 1,
        d = [P(S("/bin/this.program"), "i8", 0)];
    b();
    for (var e = 0; e < c - 1; e += 1) d.push(P(S(a[e]), "i8", 0)), b();
    d.push(0);
    var d = P(d, "i32", 0),
        f, a = w;
    try {
        f = Module._main(c, d, 0)
    } catch (g) {
        if ("ExitStatus" == g.name) return g.status;
        "SimulateInfiniteLoop" == g ? Module.noExitRuntime = n : j(g)
    } finally {
        w = a
    }
    return f
};

function Xa(a) {
    function b() {
        Oa || (Oa = n, Ka(La));
        Ka(Ma);
        var b = 0;
        Sa = n;
        Module._main && Wa && (b = Module.callMain(a), Module.noExitRuntime || Ka(Na));
        if (Module.postRun)
            for ("function" == typeof Module.postRun && (Module.postRun = [Module.postRun]); 0 < Module.postRun.length;) Module.postRun.pop()();
        return b
    }
    a = a || Module.arguments;
    if (0 < T) return Module.g("run() called, but dependencies remain, so not running"), 0;
    if (Module.preRun) {
        "function" == typeof Module.preRun && (Module.preRun = [Module.preRun]);
        var c = Module.preRun;
        Module.preRun = [];
        for (var d = c.length - 1; 0 <= d; d--) c[d]();
        if (0 < T) return 0
    }
    return Module.setStatus ? (Module.setStatus("Running..."), setTimeout(function() {
        setTimeout(function() {
            Module.setStatus("")
        }, 1);
        I || b()
    }, 1), 0) : b()
}
Module.run = Module.X = Xa;
if (Module.preInit)
    for ("function" == typeof Module.preInit && (Module.preInit = [Module.preInit]); 0 < Module.preInit.length;) Module.preInit.pop()();
var Wa = n;
Module.noInitialRun && (Wa = r);
Xa();