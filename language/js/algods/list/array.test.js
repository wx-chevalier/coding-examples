import { flatternArrayByReduce } from './array';

describe('', () => {
  it('', () => {
    const expectArr = [0, 1, 2, 3];
    expect(flatternArrayByReduce([0, [1, 2], 3])).toEqual(expectArr);
  });
});
