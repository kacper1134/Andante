export const arrayEquals = (a: any[], b: any[]) => {
  return (
    Array.isArray(a) &&
    Array.isArray(b) &&
    a.length === b.length &&
    a.every((val, index) => val === b[index])
  );
};

export const dedupe = (arr: any[]) => {
  return arr.reduce(
    function (p, c) {
      var id = c.id;
      if (p.temp.indexOf(id) === -1) {
        p.out.push(c);
        p.temp.push(id);
      }
      return p;
    },
    {
      temp: [],
      out: [],
    }
  ).out;
};
