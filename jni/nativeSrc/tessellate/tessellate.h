#include "glu.h"

typedef struct Triangle {
   int v[3];
   struct Triangle *prev;
} Triangle;

typedef struct Vertex {
    double pt[3];
    int index;
    struct Vertex *prev;
} Vertex;

typedef struct TessContext {
   Triangle *latest_t;
   int n_tris;
   int reversed;

   Vertex *v_prev;
   Vertex *v_prevprev;
   Vertex *latest_v;
   GLenum current_mode;
   int odd_even_strip;

   void (*vertex_cb)(Vertex *, struct TessContext *);
} TessContext;

TessContext *tessellateD
    (double **verts,
     int *nverts,
     int **tris,
     int *ntris,
     const double **contoursbegin,
     const double **contoursend);

TessContext *tessellate
    (float **verts,
     int *nverts,
     int **tris,
     int *ntris,
     const float **contoursbegin,
     const float **contoursend);
