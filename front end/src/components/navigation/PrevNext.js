import React from 'react';
import Link from './Link';

const PrevNext = ({prev, next, prevUrl, nextUrl, rev}) => {
    return (
        <div>
            <hr />
            <ul className="pager">
                {prev &&
                <li className="previous">
                    <Link url={prevUrl} onClick={prev}>
                        &larr; {rev==='true'?'Newer':'Older'}
                    </Link>
                </li>
                }
                {next &&
                <li className="next">
                    <Link url={nextUrl} onClick={next}>
                        {rev==='true'?'Older':'Newer'} &rarr;
                    </Link>
                </li>
                }
            </ul>
        </div>
    );
};

export default PrevNext;